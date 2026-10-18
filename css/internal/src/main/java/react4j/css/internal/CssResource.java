package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** Identity and dependencies of one processor stylesheet, independent of JavaScript liveness. */
public record CssResource(
        String owner,
        Kind kind,
        List<String> dependencies,
        List<String> includes,
        String implementation,
        List<Binding> bindings,
        Map<String, List<String>> providers) {
    private static final Pattern OWNER = Pattern.compile(
            "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(?:\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");

    public enum Kind {
        COMPONENT,
        GLOBAL,
        KEYFRAMES
    }

    public enum SymbolKind {
        CLASS,
        ID,
        ANIMATION,
        PROPERTY
    }

    public record Binding(String method, SymbolKind kind, String value) {
        public Binding {
            if (!OWNER.matcher(method).matches() || method.contains(".") || value.isBlank()) {
                throw new IllegalArgumentException("Invalid CSS binding: " + method);
            }
        }
    }

    public CssResource {
        path(owner);
        dependencies = List.copyOf(dependencies);
        includes = List.copyOf(includes);
        bindings = List.copyOf(bindings);
        var copied = new TreeMap<String, List<String>>();
        for (var entry : providers.entrySet()) {
            String symbol = entry.getKey();
            var owners = entry.getValue();
            if (symbol.isBlank()
                    || owners.isEmpty()
                    || new HashSet<>(owners).size() != owners.size()
                    || !dependencies.containsAll(owners)) {
                throw new IllegalArgumentException("CSS symbol providers must be distinct dependencies");
            }
            copied.put(symbol, List.copyOf(owners));
        }
        providers = Map.copyOf(copied);
        if (bindings.isEmpty() != implementation.isEmpty()) {
            throw new IllegalArgumentException("CSS bindings require their generated implementation");
        }
        if (!implementation.isEmpty()) {
            path(implementation);
        }
        var methods = new HashSet<String>();
        for (var binding : bindings) {
            if (!methods.add(binding.method())) {
                throw new IllegalArgumentException("Duplicate CSS binding: " + binding.method());
            }
        }
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
        json.addProperty("version", 3);
        json.addProperty("owner", owner);
        json.addProperty("kind", kind.name());
        var entries = new JsonArray();
        dependencies.forEach(entries::add);
        json.add("dependencies", entries);
        var included = new JsonArray();
        includes.forEach(included::add);
        json.add("includes", included);
        json.addProperty("implementation", implementation);
        var methods = new JsonArray();
        for (var binding : bindings) {
            var method = new JsonObject();
            method.addProperty("method", binding.method());
            method.addProperty("kind", binding.kind().name());
            method.addProperty("value", binding.value());
            methods.add(method);
        }
        json.add("bindings", methods);
        var references = new JsonObject();
        new TreeMap<>(providers).forEach((symbol, owners) -> {
            var values = new JsonArray();
            owners.forEach(values::add);
            references.add(symbol, values);
        });
        json.add("providers", references);
        return json + "\n";
    }

    public static CssResource read(String source, String expectedOwner) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!json.has("version") || !"3".equals(json.get("version").toString())) {
                throw new IllegalArgumentException("Unsupported CSS metadata version");
            }
            String owner = string(json, "owner");
            if (!owner.equals(expectedOwner)) {
                throw new IllegalArgumentException("CSS metadata owner differs from its path: " + expectedOwner);
            }
            var bindings = new ArrayList<Binding>();
            for (var entry : json.getAsJsonArray("bindings")) {
                var method = entry.getAsJsonObject();
                bindings.add(new Binding(
                        string(method, "method"), SymbolKind.valueOf(string(method, "kind")), string(method, "value")));
            }
            var providers = new TreeMap<String, List<String>>();
            var references = json.getAsJsonObject("providers");
            references.keySet().forEach(key -> providers.put(key, names(references, key)));
            return new CssResource(
                    owner,
                    Kind.valueOf(string(json, "kind")),
                    names(json, "dependencies"),
                    names(json, "includes"),
                    string(json, "implementation"),
                    bindings,
                    providers);
        } catch (JsonParseException | IllegalStateException | ClassCastException | NullPointerException e) {
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
