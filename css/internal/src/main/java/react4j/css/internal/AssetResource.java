package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** File identities emitted by the processor, independent of file contents and deployment URLs. */
public record AssetResource(String owner, String implementation, List<Binding> bindings) {
    public record Binding(String method, String path) {
        public Binding {
            if (!method.matches("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*")) {
                throw new IllegalArgumentException("Invalid asset accessor: " + method);
            }
            validatePath(path);
        }

        public String value() {
            return "asset:" + path;
        }
    }

    public AssetResource {
        path(owner);
        CssResource.path(implementation);
        bindings = List.copyOf(bindings);
        var methods = new HashSet<String>();
        for (var binding : bindings) {
            if (!methods.add(binding.method())) {
                throw new IllegalArgumentException("Duplicate asset accessor: " + binding.method());
            }
        }
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("@Assets requires at least one @Asset accessor");
        }
    }

    public static String path(String owner) {
        return CssResource.path(owner).replaceFirst("\\.css$", ".assets.json");
    }

    public static String resolve(String owner, String relative) {
        CssResource.path(owner);
        validatePath(relative);
        int separator = owner.lastIndexOf('.');
        return (separator < 0 ? "" : owner.substring(0, separator).replace('.', '/') + "/") + relative;
    }

    public static void validatePath(String path) {
        if (path.isEmpty() || path.chars().anyMatch(c -> Character.isISOControl(c) || "\\:?#%".indexOf(c) >= 0)) {
            throw new IllegalArgumentException("Invalid logical asset path: " + path);
        }
        for (var part : path.split("/", -1)) {
            if (part.isEmpty() || ".".equals(part) || "..".equals(part)) {
                throw new IllegalArgumentException("Asset paths must be relative and canonical: " + path);
            }
        }
    }

    public String json() {
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("owner", owner);
        json.addProperty("implementation", implementation);
        var entries = new JsonArray();
        for (var binding : bindings) {
            var item = new JsonObject();
            item.addProperty("method", binding.method());
            item.addProperty("path", binding.path());
            entries.add(item);
        }
        json.add("bindings", entries);
        return json + "\n";
    }

    static AssetResource read(String source, String expectedOwner) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!"1".equals(json.get("version").toString()) || !expectedOwner.equals(string(json, "owner"))) {
                throw new IllegalArgumentException("Asset metadata version or owner differs from its path");
            }
            return new AssetResource(
                    expectedOwner,
                    string(json, "implementation"),
                    json.getAsJsonArray("bindings").asList().stream()
                            .map(value -> value.getAsJsonObject())
                            .map(value -> new Binding(string(value, "method"), string(value, "path")))
                            .toList());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Invalid asset metadata for " + expectedOwner + ": " + e.getMessage(), e);
        }
    }

    public static Map<String, AssetResource> readTrees(List<Path> trees) throws IOException {
        var resources = new TreeMap<String, AssetResource>();
        for (var tree : trees.stream().distinct().toList()) {
            try (var files = Files.walk(tree)) {
                for (var file : files.filter(Files::isRegularFile).sorted().toList()) {
                    String path = tree.relativize(file).toString().replace('\\', '/');
                    if (path.endsWith(".assets.json")) {
                        String owner = path.substring(0, path.length() - ".assets.json".length())
                                .replace('/', '.');
                        if (null != resources.putIfAbsent(owner, read(Files.readString(file), owner))) {
                            throw new IllegalArgumentException("Duplicate asset definition: " + owner);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableMap(resources);
    }

    /** Reads declared paths only; the action does not consume asset bytes. */
    public static Map<String, String> catalogue(List<Path> manifests) throws IOException {
        var files = new TreeMap<String, String>();
        for (var manifest : manifests.stream().distinct().toList()) {
            var json = JsonParser.parseString(Files.readString(manifest)).getAsJsonObject();
            for (var entry : json.entrySet()) {
                validatePath(entry.getKey());
                String file = string(json, entry.getKey());
                if (file.isBlank()) {
                    throw new IllegalArgumentException("Empty declared asset file: " + entry.getKey());
                }
                String previous = files.putIfAbsent(entry.getKey(), file);
                if (null != previous && !previous.equals(file)) {
                    throw new IllegalArgumentException("Conflicting declared asset path: " + entry.getKey());
                }
            }
        }
        return Collections.unmodifiableMap(files);
    }

    public static void validate(Map<String, AssetResource> resources, Map<String, String> catalogue) {
        for (var resource : resources.values()) {
            for (var binding : resource.bindings()) {
                if (!catalogue.containsKey(binding.path())) {
                    throw new IllegalArgumentException("Undeclared asset " + binding.path() + " referenced by "
                            + resource.owner() + "#" + binding.method());
                }
            }
        }
    }

    private static String string(JsonObject json, String name) {
        var value = json.get(name);
        if (null == value
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Asset metadata requires string " + name);
        }
        return value.getAsString();
    }
}
