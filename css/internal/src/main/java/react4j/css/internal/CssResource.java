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
public record CssResource(
        String owner,
        Kind kind,
        List<String> dependencies,
        List<String> includes,
        String implementation,
        List<Binding> bindings,
        List<Symbol> symbols,
        List<Occurrence> occurrences,
        List<FontUse> fontUses) {
    private static final Pattern OWNER = Pattern.compile(
            "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(?:\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");

    public enum Kind {
        COMPONENT,
        GLOBAL,
        KEYFRAMES,
        UTILITY
    }

    public enum SymbolKind {
        CLASS,
        ID,
        ANIMATION,
        PROPERTY,
        FONT,
        CONTAINER
    }

    public record SymbolId(String owner, SymbolKind kind, String name) {
        public SymbolId {
            path(owner);
            if ((SymbolKind.FONT != kind && name.chars().anyMatch(Character::isWhitespace))
                    || (name.isEmpty() && SymbolKind.CLASS != kind && SymbolKind.ANIMATION != kind)) {
                throw new IllegalArgumentException("Invalid canonical CSS symbol: " + name);
            }
        }

        public String id() {
            return owner + "#" + kind + ":" + name;
        }

        public JsonObject json() {
            var json = new JsonObject();
            json.addProperty("owner", owner);
            json.addProperty("kind", kind.name());
            json.addProperty("name", name);
            return json;
        }

        public static SymbolId read(JsonObject json) {
            return new SymbolId(string(json, "owner"), SymbolKind.valueOf(string(json, "kind")), string(json, "name"));
        }
    }

    public record Binding(String method, SymbolKind kind, String value, List<String> requires, List<SymbolId> symbols) {
        public Binding {
            if (!OWNER.matcher(method).matches() || method.contains(".") || value.isBlank()) {
                throw new IllegalArgumentException("Invalid CSS binding: " + method);
            }
            requires = List.copyOf(requires);
            if (new HashSet<>(requires).size() != requires.size()) {
                throw new IllegalArgumentException("CSS binding providers must be distinct");
            }
            requires.forEach(CssResource::path);
            symbols = List.copyOf(symbols);
            if (symbols.isEmpty()
                    || symbols.stream().anyMatch(symbol -> kind != symbol.kind())
                    || (SymbolKind.CLASS != kind && 1 != symbols.size())
                    || new HashSet<>(symbols).size() != symbols.size()) {
                throw new IllegalArgumentException("Invalid CSS binding composition: " + method);
            }
        }

        public JsonObject json() {
            var json = new JsonObject();
            json.addProperty("method", method);
            json.addProperty("kind", kind.name());
            json.addProperty("value", value);
            var owners = new JsonArray();
            requires.forEach(owners::add);
            json.add("requires", owners);
            var references = new JsonArray();
            symbols.forEach(symbol -> references.add(symbol.json()));
            json.add("symbols", references);
            return json;
        }

        public static Binding read(JsonObject json) {
            return new Binding(
                    string(json, "method"),
                    SymbolKind.valueOf(string(json, "kind")),
                    string(json, "value"),
                    names(json, "requires"),
                    json.getAsJsonArray("symbols").asList().stream()
                            .map(item -> SymbolId.read(item.getAsJsonObject()))
                            .toList());
        }
    }

    public record Symbol(SymbolKind kind, String name, String value, boolean stable) {
        public Symbol {
            if ((SymbolKind.FONT != kind && name.chars().anyMatch(Character::isWhitespace))
                    || value.isBlank()
                    || value.chars().anyMatch(Character::isWhitespace)
                    || (name.isEmpty() && SymbolKind.CLASS != kind && SymbolKind.ANIMATION != kind)) {
                throw new IllegalArgumentException("Invalid CSS symbol: " + name);
            }
        }
    }

    public record Occurrence(SymbolId symbol, int start, int end, List<String> requires) {
        public Occurrence {
            if (start < 0 || end <= start) {
                throw new IllegalArgumentException("Invalid CSS symbol occurrence span");
            }
            requires = List.copyOf(requires);
            if (new HashSet<>(requires).size() != requires.size()
                    || ((SymbolKind.ID == symbol.kind() || SymbolKind.CLASS == symbol.kind()) && !requires.isEmpty())) {
                throw new IllegalArgumentException("Invalid CSS occurrence provider requirements");
            }
            requires.forEach(CssResource::path);
        }

        public JsonObject json() {
            var json = new JsonObject();
            json.add("symbol", symbol.json());
            json.addProperty("start", start);
            json.addProperty("end", end);
            var providers = new JsonArray();
            requires.forEach(providers::add);
            json.add("requires", providers);
            return json;
        }

        public static Occurrence read(JsonObject json) {
            return new Occurrence(
                    SymbolId.read(json.getAsJsonObject("symbol")),
                    integer(json, "start"),
                    integer(json, "end"),
                    names(json, "requires"));
        }
    }

    /** Families reachable through a surviving runtime-dependent font declaration. */
    public record FontUse(int start, int end, List<SymbolId> families) {
        public FontUse {
            families = List.copyOf(families);
            if (start < 0
                    || end <= start
                    || families.isEmpty()
                    || new HashSet<>(families).size() != families.size()
                    || families.stream().anyMatch(id -> SymbolKind.FONT != id.kind())) {
                throw new IllegalArgumentException("Invalid dynamic font dependency");
            }
        }

        JsonObject json() {
            var json = new JsonObject();
            json.addProperty("start", start);
            json.addProperty("end", end);
            var ids = new JsonArray();
            families.forEach(id -> ids.add(id.json()));
            json.add("families", ids);
            return json;
        }

        static FontUse read(JsonObject json) {
            return new FontUse(
                    integer(json, "start"),
                    integer(json, "end"),
                    json.getAsJsonArray("families").asList().stream()
                            .map(id -> SymbolId.read(id.getAsJsonObject()))
                            .toList());
        }
    }

    public CssResource withOccurrences(List<Occurrence> values) {
        return new CssResource(
                owner, kind, dependencies, includes, implementation, bindings, symbols, values, fontUses);
    }

    public CssResource project(List<SourceMap.Span> spans) {
        var projected = new ArrayList<Occurrence>();
        var uses = new ArrayList<FontUse>();
        int offset = 0;
        for (var span : spans) {
            for (var occurrence : occurrences) {
                if (occurrence.start() >= span.start() && occurrence.end() <= span.end()) {
                    projected.add(new Occurrence(
                            occurrence.symbol(),
                            offset + occurrence.start() - span.start(),
                            offset + occurrence.end() - span.start(),
                            occurrence.requires()));
                } else if (occurrence.start() < span.end() && occurrence.end() > span.start()) {
                    throw new IllegalArgumentException("CSS pruning split a managed identifier");
                }
            }
            for (var use : fontUses) {
                if (use.start() >= span.start() && use.end() <= span.end()) {
                    uses.add(new FontUse(
                            offset + use.start() - span.start(), offset + use.end() - span.start(), use.families()));
                } else if (use.start() < span.end() && use.end() > span.start()) {
                    throw new IllegalArgumentException("CSS pruning split a font dependency");
                }
            }
            offset += span.end() - span.start();
        }
        return new CssResource(owner, kind, dependencies, includes, implementation, bindings, symbols, projected, uses);
    }

    private static int integer(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value || !value.toString().matches("0|[1-9][0-9]*")) {
            throw new IllegalArgumentException("CSS occurrence requires a nonnegative integer " + key);
        }
        return Integer.parseInt(value.toString());
    }

    public CssResource {
        path(owner);
        dependencies = List.copyOf(dependencies);
        includes = List.copyOf(includes);
        bindings = List.copyOf(bindings);
        symbols = List.copyOf(symbols);
        occurrences = List.copyOf(occurrences);
        fontUses = List.copyOf(fontUses);
        int previousUse = 0;
        for (var use : fontUses) {
            if (use.start() < previousUse) {
                throw new IllegalArgumentException("Dynamic font uses require ordered spans and declared dependencies");
            }
            for (var id : use.families()) {
                if (!owner.equals(id.owner()) && !dependencies.contains(id.owner())) {
                    throw new IllegalArgumentException("Dynamic font uses require declared dependencies");
                }
            }
            previousUse = use.end();
        }
        int previousEnd = 0;
        for (var occurrence : occurrences) {
            if (occurrence.start() < previousEnd || !dependencies.containsAll(occurrence.requires())) {
                throw new IllegalArgumentException("CSS occurrences require ordered spans and declared dependencies");
            }
            previousEnd = occurrence.end();
        }
        var identities = new HashSet<String>();
        for (var symbol : symbols) {
            if (!identities.add(symbol.kind() + ":" + symbol.name())) {
                throw new IllegalArgumentException("Duplicate owned CSS symbol: " + symbol.name());
            }
        }
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
            if (!dependencies.containsAll(binding.requires())) {
                throw new IllegalArgumentException("CSS binding providers must be dependencies");
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
        json.addProperty("version", 10);
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
            methods.add(binding.json());
        }
        json.add("bindings", methods);
        var names = new JsonArray();
        for (var symbol : symbols) {
            var name = new JsonObject();
            name.addProperty("kind", symbol.kind().name());
            name.addProperty("name", symbol.name());
            name.addProperty("value", symbol.value());
            name.addProperty("stable", symbol.stable());
            names.add(name);
        }
        json.add("symbols", names);
        var uses = new JsonArray();
        occurrences.forEach(occurrence -> uses.add(occurrence.json()));
        json.add("occurrences", uses);
        var fonts = new JsonArray();
        fontUses.forEach(use -> fonts.add(use.json()));
        json.add("fontUses", fonts);
        return json + "\n";
    }

    public static CssResource read(String source, String expectedOwner) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!json.has("version") || !"10".equals(json.get("version").toString())) {
                throw new IllegalArgumentException("Unsupported CSS metadata version");
            }
            String owner = string(json, "owner");
            if (!owner.equals(expectedOwner)) {
                throw new IllegalArgumentException("CSS metadata owner differs from its path: " + expectedOwner);
            }
            var bindings = new ArrayList<Binding>();
            for (var entry : json.getAsJsonArray("bindings")) {
                var method = entry.getAsJsonObject();
                bindings.add(Binding.read(method));
            }
            var symbols = new ArrayList<Symbol>();
            for (var item : json.getAsJsonArray("symbols")) {
                var symbol = item.getAsJsonObject();
                var stable = symbol.get("stable");
                if (null == stable
                        || !stable.isJsonPrimitive()
                        || !stable.getAsJsonPrimitive().isBoolean()) {
                    throw new IllegalArgumentException("CSS symbol requires boolean stable");
                }
                symbols.add(new Symbol(
                        SymbolKind.valueOf(string(symbol, "kind")),
                        string(symbol, "name"),
                        string(symbol, "value"),
                        stable.getAsBoolean()));
            }
            return new CssResource(
                    owner,
                    Kind.valueOf(string(json, "kind")),
                    names(json, "dependencies"),
                    names(json, "includes"),
                    string(json, "implementation"),
                    bindings,
                    symbols,
                    json.getAsJsonArray("occurrences").asList().stream()
                            .map(item -> Occurrence.read(item.getAsJsonObject()))
                            .toList(),
                    json.getAsJsonArray("fontUses").asList().stream()
                            .map(item -> FontUse.read(item.getAsJsonObject()))
                            .toList());
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
