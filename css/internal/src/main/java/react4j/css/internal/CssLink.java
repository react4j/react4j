package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Versioned evidence between the Closure compilation and independently rooted CSS bundles. */
public record CssLink(String link, String digest, boolean optimized, List<Value> values, CssNames names) {
    public record Value(String owner, String implementation, CssResource.Binding binding) {
        public Value {
            CssResource.path(owner);
            CssResource.path(implementation);
        }

        public String id() {
            return owner + "#" + binding.method();
        }

        public String source() {
            return implementation.replace('.', '/') + ".impl.java.js";
        }
    }

    public CssLink {
        if (link.isBlank() || !digest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Invalid CSS link identity or catalogue digest");
        }
        if (!optimized && names.policy().compressed()) {
            throw new IllegalArgumentException("CSS compression requires an optimized link");
        }
        values = List.copyOf(values);
        var ids = new HashSet<String>();
        for (var value : values) {
            if (!ids.add(value.id())) {
                throw new IllegalArgumentException("Duplicate CSS binding identity: " + value.id());
            }
        }
    }

    public static CssLink create(CssCatalogue catalogue, String link, boolean optimized) {
        return create(catalogue, link, optimized, new CssNames.Policy(optimized, ""));
    }

    public static CssLink create(CssCatalogue catalogue, String link, boolean optimized, CssNames.Policy policy) {
        var values = new ArrayList<Value>();
        catalogue
                .resources()
                .values()
                .forEach(resource -> resource.metadata()
                        .bindings()
                        .forEach(binding -> values.add(new Value(
                                resource.metadata().owner(), resource.metadata().implementation(), binding))));
        return new CssLink(link, catalogue.digest(), optimized, values, CssNames.create(catalogue, policy));
    }

    public String json() {
        var json = header();
        var bindings = new JsonArray();
        for (var value : values) {
            var entry = value.binding().json();
            entry.addProperty("owner", value.owner());
            entry.addProperty("implementation", value.implementation());
            bindings.add(entry);
        }
        json.add("values", bindings);
        return json + "\n";
    }

    public static CssLink read(String source) {
        try {
            var json = document(source);
            var values = new ArrayList<Value>();
            for (var item : json.getAsJsonArray("values")) {
                var entry = item.getAsJsonObject();
                values.add(new Value(
                        string(entry, "owner"), string(entry, "implementation"), CssResource.Binding.read(entry)));
            }
            var optimized = json.get("optimized");
            if (!optimized.isJsonPrimitive() || !optimized.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("CSS link requires boolean optimized");
            }
            return new CssLink(
                    string(json, "link"),
                    string(json, "digest"),
                    optimized.getAsBoolean(),
                    values,
                    CssNames.read(CssNames.Policy.read(json.getAsJsonObject("policy")), json.getAsJsonArray("names")));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid CSS link configuration: " + e.getMessage(), e);
        }
    }

    public String report(Set<String> live, String javascriptDigest) {
        if (!javascriptDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Invalid linked JavaScript digest");
        }
        validateLive(live);
        var json = header();
        json.addProperty("javascriptDigest", javascriptDigest);
        var entries = new JsonArray();
        live.stream().sorted().forEach(entries::add);
        json.add("live", entries);
        return json + "\n";
    }

    public Set<String> readReport(String source, String javascriptDigest) {
        try {
            var json = document(source);
            if (!link.equals(string(json, "link"))
                    || !digest.equals(string(json, "digest"))
                    || !javascriptDigest.equals(string(json, "javascriptDigest"))
                    || !Boolean.toString(optimized).equals(json.get("optimized").toString())
                    || !names.equals(CssNames.read(
                            CssNames.Policy.read(json.getAsJsonObject("policy")), json.getAsJsonArray("names")))) {
                throw new IllegalArgumentException(
                        "CSS liveness report does not match this JavaScript link and catalogue");
            }
            var live = new HashSet<String>();
            for (var value : json.getAsJsonArray("live")) {
                if (!value.isJsonPrimitive()
                        || !value.getAsJsonPrimitive().isString()
                        || !live.add(value.getAsString())) {
                    throw new IllegalArgumentException("CSS liveness report requires distinct binding IDs");
                }
            }
            validateLive(live);
            return Set.copyOf(live);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid CSS liveness report: " + e.getMessage(), e);
        }
    }

    private void validateLive(Set<String> live) {
        var known = new HashSet<String>();
        values.forEach(value -> known.add(value.id()));
        if (!known.containsAll(live)) {
            throw new IllegalArgumentException("Unknown live CSS binding");
        }
    }

    private JsonObject header() {
        var json = new JsonObject();
        json.addProperty("version", 3);
        json.addProperty("link", link);
        json.addProperty("digest", digest);
        json.addProperty("optimized", optimized);
        json.add("policy", names.policy().json());
        json.add("names", names.json());
        return json;
    }

    private static JsonObject document(String source) {
        var json = JsonParser.parseString(source).getAsJsonObject();
        if (!json.has("version") || !"3".equals(json.get("version").toString())) {
            throw new IllegalArgumentException("Unsupported CSS link evidence version");
        }
        return json;
    }

    private static String string(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("CSS link requires a string " + key);
        }
        return value.getAsString();
    }

    public static String digest(Path javascript) throws IOException {
        return HexFormat.of().formatHex(CssCatalogue.sha256().digest(Files.readAllBytes(javascript)));
    }

    public static void main(String[] args) throws IOException {
        if (1 == args.length && args[0].startsWith("@")) {
            args = Files.readAllLines(Path.of(args[0].substring(1))).toArray(String[]::new);
        }
        var trees = new ArrayList<Path>();
        var options = new TreeMap<String, String>();
        var defines = new ArrayList<String>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 == args.length) {
                throw new IllegalArgumentException("Missing CSS catalogue option value");
            }
            if ("--define".equals(args[i])) {
                defines.add(args[i + 1]);
            } else if ("--input".equals(args[i])) {
                trees.add(Path.of(args[i + 1]));
            } else if (!Set.of("--link", "--mode", "--output").contains(args[i])
                    || null != options.putIfAbsent(args[i], args[i + 1])) {
                throw new IllegalArgumentException("Unknown or repeated CSS catalogue option: " + args[i]);
            }
        }
        if (3 != options.size() || !Set.of("optimized", "retain").contains(options.get("--mode"))) {
            throw new IllegalArgumentException("Expected --link, --mode (optimized or retain), --output");
        }
        Files.writeString(
                Path.of(options.get("--output")),
                create(
                                CssCatalogue.read(trees),
                                Objects.requireNonNull(options.get("--link")),
                                "optimized".equals(options.get("--mode")),
                                CssNames.Policy.defines(defines, "optimized".equals(options.get("--mode"))))
                        .json());
    }
}
