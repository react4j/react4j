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
public record CssResource(String owner, Kind kind, List<String> dependencies) {
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
        if (Kind.KEYFRAMES == kind && !dependencies.isEmpty()) {
            throw new IllegalArgumentException("Keyframe resources must not declare dependencies");
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
        json.addProperty("version", 1);
        json.addProperty("owner", owner);
        json.addProperty("kind", kind.name());
        var entries = new JsonArray();
        dependencies.forEach(entries::add);
        json.add("dependencies", entries);
        return json + "\n";
    }

    public static CssResource read(String source, String expectedOwner) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!json.has("version") || !"1".equals(json.get("version").toString())) {
                throw new IllegalArgumentException("Unsupported CSS metadata version");
            }
            String owner = string(json, "owner");
            if (!owner.equals(expectedOwner)) {
                throw new IllegalArgumentException("CSS metadata owner differs from its path: " + expectedOwner);
            }
            var dependencies = json.getAsJsonArray("dependencies");
            if (null == dependencies) {
                throw new IllegalArgumentException("Missing CSS dependencies");
            }
            var names = new ArrayList<String>();
            for (var entry : dependencies) {
                if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException("CSS dependencies must be owner names");
                }
                names.add(entry.getAsString());
            }
            return new CssResource(owner, Kind.valueOf(string(json, "kind")), names);
        } catch (JsonParseException | IllegalStateException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid CSS metadata for " + expectedOwner, e);
        }
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
