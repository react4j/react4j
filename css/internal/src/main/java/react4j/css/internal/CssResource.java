package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/** Identity and dependencies of one processor stylesheet, independent of JavaScript liveness. */
public record CssResource(String owner, Kind kind, List<String> dependencies, List<String> includes, boolean bindings) {
    private static final Pattern OWNER = Pattern.compile(
            "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(?:\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");

    public enum Kind {
        COMPONENT,
        GLOBAL,
        KEYFRAMES
    }

    public CssResource {
        path(owner);
        dependencies = List.copyOf(dependencies);
        includes = List.copyOf(includes);
        if (!dependencies.containsAll(includes) || new HashSet<>(includes).size() != includes.size()) {
            throw new IllegalArgumentException("CSS inclusions must be distinct dependencies");
        }
        var unique = new HashSet<String>();
        for (var dependency : dependencies) {
            path(dependency);
            if (!unique.add(dependency)) {
                throw new IllegalArgumentException("Duplicate CSS dependency: " + dependency);
            }
        }
    }

    public static String path(String owner) {
        if (!OWNER.matcher(owner).matches()) {
            throw new IllegalArgumentException("Invalid CSS owner: " + owner);
        }
        return owner.replace('.', '/') + ".css";
    }

    public String json() {
        var json = new JsonObject();
        json.addProperty("version", 2);
        json.addProperty("bindings", bindings);
        json.addProperty("owner", owner);
        json.addProperty("kind", kind.name());
        var entries = new JsonArray();
        dependencies.forEach(entries::add);
        json.add("dependencies", entries);
        var included = new JsonArray();
        includes.forEach(included::add);
        json.add("includes", included);
        return json + "\n";
    }

    public static CssResource read(String source, String expectedOwner) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!json.has("version") || !"2".equals(json.get("version").toString())) {
                throw new IllegalArgumentException("Unsupported CSS metadata version");
            }
            String owner = string(json, "owner");
            if (!owner.equals(expectedOwner)) {
                throw new IllegalArgumentException("CSS metadata owner differs from its path: " + expectedOwner);
            }
            var bindings = json.get("bindings");
            if (null == bindings
                    || !bindings.isJsonPrimitive()
                    || !bindings.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("CSS metadata requires boolean bindings");
            }
            return new CssResource(
                    owner,
                    Kind.valueOf(string(json, "kind")),
                    names(json, "dependencies"),
                    names(json, "includes"),
                    bindings.getAsBoolean());
        } catch (JsonParseException | IllegalStateException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid CSS metadata for " + expectedOwner, e);
        }
    }

    private static List<String> names(JsonObject json, String key) {
        var entries = json.getAsJsonArray(key);
        if (null == entries) {
            throw new IllegalArgumentException("Missing CSS " + key);
        }
        var names = new ArrayList<String>();
        for (var entry : entries) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                throw new IllegalArgumentException("CSS dependencies must be owner names");
            }
            names.add(entry.getAsString());
        }
        return names;
    }

    private static String string(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("CSS metadata requires a string " + key);
        }
        return value.getAsString();
    }
}
