package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import react4j.css.internal.CssResource.SymbolId;
import react4j.css.internal.CssResource.SymbolKind;

/** One immutable name assignment for a complete JavaScript link and all its CSS variants. */
public record CssNames(Policy policy, List<Entry> entries) {
    public static final String COMPRESS = "react4j.css.compress_names";
    public static final String PREFIX = "react4j.css.name_prefix";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Set<String> RESERVED = Set.of(
            "none",
            "initial",
            "inherit",
            "unset",
            "revert",
            "revert-layer",
            "default",
            "ease",
            "linear",
            "ease-in",
            "ease-out",
            "ease-in-out",
            "step-start",
            "step-end",
            "infinite",
            "normal",
            "reverse",
            "alternate",
            "alternate-reverse",
            "forwards",
            "backwards",
            "both",
            "running",
            "paused",
            "auto");

    public record Policy(boolean compressed, String prefix) {
        public Policy {
            if (!prefix.isEmpty() && !prefix.matches("[A-Za-z_][A-Za-z0-9_-]*")) {
                throw new IllegalArgumentException("CSS name prefix must be an unescaped identifier prefix");
            }
        }

        public static Policy defines(List<String> defines, boolean optimized) {
            boolean compress = true;
            String prefix = "";
            var seen = new HashSet<String>();
            for (var define : defines) {
                if (!define.startsWith("--define=")) {
                    continue;
                }
                String setting = define.substring(9);
                int equals = setting.indexOf('=');
                String key = equals < 0 ? setting : setting.substring(0, equals);
                if (!COMPRESS.equals(key) && !PREFIX.equals(key)) {
                    continue;
                }
                if (equals < 0 || !seen.add(key)) {
                    throw new IllegalArgumentException("Missing or repeated CSS define: " + key);
                }
                String value = setting.substring(equals + 1);
                if (COMPRESS.equals(key)) {
                    if (!Set.of("true", "false").contains(value)) {
                        throw new IllegalArgumentException(COMPRESS + " requires a boolean");
                    }
                    compress = Boolean.parseBoolean(value);
                } else {
                    if (value.length() < 2
                            || ('\'' != value.charAt(0) && '"' != value.charAt(0))
                            || value.charAt(value.length() - 1) != value.charAt(0)) {
                        throw new IllegalArgumentException(PREFIX + " requires a quoted string");
                    }
                    prefix = value.substring(1, value.length() - 1);
                }
            }
            return new Policy(optimized && compress, prefix);
        }

        public JsonObject json() {
            var json = new JsonObject();
            json.addProperty("compressed", compressed);
            json.addProperty("prefix", prefix);
            return json;
        }

        public static Policy read(JsonObject json) {
            var compressed = json.get("compressed");
            if (null == compressed
                    || !compressed.isJsonPrimitive()
                    || !compressed.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("CSS compression policy requires a boolean");
            }
            return new Policy(compressed.getAsBoolean(), string(json, "prefix"));
        }
    }

    public record Entry(SymbolId symbol, String readable, String compiled, boolean stable, List<String> getters) {
        public Entry {
            getters = List.copyOf(getters);
            if (readable.isBlank() || compiled.isBlank()) {
                throw new IllegalArgumentException("CSS names must not be empty");
            }
        }

        private JsonObject json() {
            var json = symbol.json();
            json.addProperty("readable", readable);
            json.addProperty("compiled", compiled);
            json.addProperty("stable", stable);
            var accessors = new JsonArray();
            getters.forEach(accessors::add);
            json.add("getters", accessors);
            return json;
        }
    }

    public CssNames {
        entries = List.copyOf(entries);
        var known = new HashSet<SymbolId>();
        for (var entry : entries) {
            if (!known.add(entry.symbol())) {
                throw new IllegalArgumentException(
                        "Duplicate canonical CSS name: " + entry.symbol().id());
            }
        }
    }

    public static CssNames create(CssCatalogue catalogue, Policy policy) {
        var symbols = new TreeMap<SymbolId, CssResource.Symbol>(Comparator.comparing(SymbolId::id));
        var getters = new TreeMap<SymbolId, List<String>>(Comparator.comparing(SymbolId::id));
        for (var resource : catalogue.resources().values()) {
            var metadata = resource.metadata();
            for (var symbol : metadata.symbols()) {
                symbols.put(new SymbolId(metadata.owner(), symbol.kind(), symbol.name()), symbol);
            }
        }
        for (var resource : catalogue.resources().values()) {
            var metadata = resource.metadata();
            for (var binding : metadata.bindings()) {
                String getter = metadata.owner() + "#" + binding.method();
                var readable = new ArrayList<String>();
                for (var id : binding.symbols()) {
                    var symbol = symbols.get(id);
                    if (null == symbol) {
                        throw new IllegalArgumentException("Unknown canonical CSS binding symbol: " + id.id());
                    }
                    readable.add(symbol.value());
                    getters.computeIfAbsent(id, ignored -> new ArrayList<>()).add(getter);
                }
                if (!binding.value().equals(String.join(" ", readable))) {
                    throw new IllegalArgumentException(
                            "CSS binding value differs from its canonical composition: " + binding.method());
                }
            }
        }
        for (var resource : catalogue.resources().values()) {
            for (var occurrence : resource.metadata().occurrences()) {
                var symbol = symbols.get(occurrence.symbol());
                if (null == symbol
                        || occurrence.end() > resource.css().length()
                        || !resource.css()
                                .substring(occurrence.start(), occurrence.end())
                                .equals(symbol.value())) {
                    throw new IllegalArgumentException("CSS occurrence differs from its canonical catalogue: "
                            + occurrence.symbol().id());
                }
            }
        }
        for (var resource : catalogue.resources().values()) {
            for (var use : resource.metadata().fontUses()) {
                if (use.end() > resource.css().length() || !symbols.keySet().containsAll(use.families())) {
                    throw new IllegalArgumentException("Dynamic CSS font dependency differs from its catalogue");
                }
            }
        }
        var reserved = new HashSet<>(RESERVED);
        reserved.addAll(CssFontValues.RESERVED);
        var insensitive = new HashSet<String>();
        var parsed = new TreeMap<String, CssTokens.Parsed>();
        symbols.values().forEach(symbol -> reserved.add(symbol.value()));
        // Names outside the generated inventory are intentional authored names.
        catalogue.resources().forEach((owner, resource) -> {
            var references = CssTokens.parse(resource.css());
            parsed.put(owner, references);
            references.tokens().forEach(token -> {
                reserved.add(token.name());
                if (SymbolKind.FONT == token.kind()) {
                    insensitive.add(CssTokens.asciiLower(token.name()));
                }
            });
            for (var attribute : references.attributes()) {
                attribute.names().forEach(name -> {
                    if (attribute.insensitive()) {
                        insensitive.add(CssTokens.asciiLower(name));
                    } else {
                        reserved.add(name);
                    }
                });
            }
        });
        var entries = new ArrayList<Entry>();
        int next = 0;
        for (var item : symbols.entrySet()) {
            var id = item.getKey();
            var symbol = item.getValue();
            String compiled = symbol.value();
            if (policy.compressed() && !symbol.stable()) {
                do {
                    compiled = (SymbolKind.PROPERTY == symbol.kind() ? "--" : "") + policy.prefix() + shortName(next++);
                } while (reserved.contains(compiled)
                        || insensitive.contains(CssTokens.asciiLower(compiled))
                        || (SymbolKind.ANIMATION == symbol.kind() || SymbolKind.FONT == symbol.kind())
                                && RESERVED.contains(compiled.toLowerCase(Locale.ROOT)));
                reserved.add(compiled);
                if (SymbolKind.FONT == symbol.kind()) {
                    insensitive.add(CssTokens.asciiLower(compiled));
                }
            }
            entries.add(new Entry(id, symbol.value(), compiled, symbol.stable(), getters.getOrDefault(id, List.of())));
        }
        var table = new CssNames(policy, entries);
        var checker = new CssRenamer(table);
        parsed.forEach((owner, references) -> checker.checkNames(references, owner));
        return table;
    }

    private static String shortName(int ordinal) {
        var result = new StringBuilder();
        do {
            result.append(ALPHABET.charAt(ordinal % ALPHABET.length()));
            ordinal = ordinal / ALPHABET.length() - 1;
        } while (ordinal >= 0);
        return result.reverse().toString();
    }

    public JsonArray json() {
        var json = new JsonArray();
        entries.forEach(entry -> json.add(entry.json()));
        return json;
    }

    public static CssNames read(Policy policy, JsonArray json) {
        var entries = new ArrayList<Entry>();
        for (var item : json) {
            var entry = item.getAsJsonObject();
            var stable = entry.get("stable");
            if (null == stable
                    || !stable.isJsonPrimitive()
                    || !stable.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("CSS name stability requires a boolean");
            }
            var getters = new ArrayList<String>();
            for (var getter : entry.getAsJsonArray("getters")) {
                if (!getter.isJsonPrimitive() || !getter.getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException("CSS name getters require strings");
                }
                getters.add(getter.getAsString());
            }
            entries.add(new Entry(
                    SymbolId.read(entry),
                    string(entry, "readable"),
                    string(entry, "compiled"),
                    stable.getAsBoolean(),
                    getters));
        }
        return new CssNames(policy, entries);
    }

    private static String string(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("CSS name table requires a string " + key);
        }
        return value.getAsString();
    }
}
