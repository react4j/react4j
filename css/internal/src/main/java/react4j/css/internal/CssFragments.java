package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** Generic checked CSS contributions; collection and linking never interpret a rule language. */
public record CssFragments(String requester, List<Fragment> fragments) {
    public record Fragment(String candidate, String orderKey, String css, String sourceMap, CssResource metadata) {
        public Fragment {
            if (candidate.isEmpty()
                    || candidate.chars().anyMatch(c -> " \t\n\r\f".indexOf(c) >= 0)
                    || orderKey.isEmpty()
                    || metadata.fingerprint().isEmpty()
                    || !metadata.bindings().isEmpty()
                    || !metadata.implementation().isEmpty()) {
                throw new IllegalArgumentException("Invalid utility expansion fragment");
            }
            var classes = metadata.symbols().stream()
                    .filter(symbol -> CssResource.SymbolKind.CLASS == symbol.kind())
                    .toList();
            if (1 != classes.size() || !candidate.equals(classes.get(0).name())) {
                throw new IllegalArgumentException("Utility fragment requires its canonical candidate class");
            }
            var map = JsonParser.parseString(sourceMap).getAsJsonObject();
            if (!map.has("version") || !"3".equals(map.get("version").toString()) || !map.has("file")) {
                throw new IllegalArgumentException("Invalid utility fragment source map");
            }
            SourceMap.project(css, map, List.of(new SourceMap.Span(0, css.length())));
            sourceMap = map.toString();
        }

        private JsonObject json() {
            var json = new JsonObject();
            json.addProperty("candidate", candidate);
            json.addProperty("orderKey", orderKey);
            json.addProperty("css", css);
            json.add("sourceMap", JsonParser.parseString(sourceMap));
            json.add("metadata", JsonParser.parseString(metadata.json()));
            return json;
        }
    }

    record Located(Fragment fragment, String origin) {}

    public CssFragments {
        CssResource.path(requester);
        fragments = List.copyOf(fragments);
        var identities = new HashSet<String>();
        for (var fragment : fragments) {
            if (!identities.add(fragment.metadata().owner() + ":" + fragment.candidate())) {
                throw new IllegalArgumentException("Duplicate utility fragment in " + requester);
            }
        }
    }

    public String json() {
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("requester", requester);
        var values = new JsonArray();
        fragments.stream()
                .sorted(Comparator.comparing(
                                (Fragment value) -> value.metadata().owner())
                        .thenComparing(Fragment::orderKey))
                .forEach(value -> values.add(value.json()));
        json.add("fragments", values);
        return json + "\n";
    }

    public static CssFragments read(String source, String expectedRequester) {
        try {
            var json = JsonParser.parseString(source).getAsJsonObject();
            if (!"1".equals(json.get("version").toString())
                    || !expectedRequester.equals(json.get("requester").getAsString())) {
                throw new IllegalArgumentException("Unsupported utility fragment version or requester");
            }
            var fragments = new ArrayList<Fragment>();
            for (var value : json.getAsJsonArray("fragments")) {
                var entry = value.getAsJsonObject();
                var metadata = entry.getAsJsonObject("metadata");
                fragments.add(new Fragment(
                        entry.get("candidate").getAsString(),
                        entry.get("orderKey").getAsString(),
                        entry.get("css").getAsString(),
                        entry.get("sourceMap").toString(),
                        CssResource.read(
                                metadata.toString(), metadata.get("owner").getAsString())));
            }
            return new CssFragments(expectedRequester, fragments);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Invalid utility fragment metadata for " + expectedRequester + ": " + e.getMessage(), e);
        }
    }

    static Map<String, CssCatalogue.Resource> merge(
            Map<String, CssCatalogue.Resource> definitions, List<Located> fragments) {
        var grouped = new TreeMap<String, Map<String, Located>>();
        for (var located : fragments) {
            var fragment = located.fragment();
            String owner = fragment.metadata().owner();
            var provider = definitions.get(owner);
            if (null == provider) {
                throw new IllegalArgumentException("Missing utility provider " + owner + " for " + located.origin());
            }
            if (!provider.metadata().fingerprint().equals(fragment.metadata().fingerprint())) {
                throw new IllegalArgumentException("Utility provider fingerprint mismatch for " + owner + " between "
                        + CssResource.path(owner) + ".json and " + located.origin());
            }
            var candidates = grouped.computeIfAbsent(owner, ignored -> new TreeMap<>());
            var previous = candidates.putIfAbsent(fragment.candidate(), located);
            if (null != previous) {
                var prior = previous.fragment();
                if (!prior.css().equals(fragment.css())
                        || !prior.orderKey().equals(fragment.orderKey())
                        || !prior.metadata().equals(fragment.metadata())) {
                    throw new IllegalArgumentException("Conflicting utility expansion " + owner + ":"
                            + fragment.candidate() + " between " + previous.origin() + " and " + located.origin());
                }
                if (prior.sourceMap().compareTo(fragment.sourceMap()) > 0) {
                    candidates.put(fragment.candidate(), located);
                }
            }
        }
        var merged = new TreeMap<>(definitions);
        for (var group : grouped.entrySet()) {
            var provider = definitions.get(group.getKey());
            if (null == provider) {
                throw new AssertionError(group.getKey());
            }
            var metadata = provider.metadata();
            var writer = new SourceMap.Writer().appendMapped(provider.css(), provider.map());
            var dependencies = new TreeSet<>(metadata.dependencies());
            var symbols = new TreeMap<String, CssResource.Symbol>();
            metadata.symbols().forEach(symbol -> symbols.put(symbol.kind() + ":" + symbol.name(), symbol));
            var occurrences = new ArrayList<>(metadata.occurrences());
            var fonts = new ArrayList<>(metadata.fontUses());
            for (var fragment : group.getValue().values().stream()
                    .map(Located::fragment)
                    .sorted(Comparator.comparing(Fragment::orderKey))
                    .toList()) {
                int offset = writer.css().length();
                writer.appendMapped(
                        fragment.css(),
                        JsonParser.parseString(fragment.sourceMap()).getAsJsonObject(),
                        provider.map());
                dependencies.addAll(fragment.metadata().dependencies());
                if (!metadata.includes().equals(fragment.metadata().includes())) {
                    throw new IllegalArgumentException(
                            "Utility fragment changes provider inclusions: " + group.getKey());
                }
                for (var symbol : fragment.metadata().symbols()) {
                    if (CssResource.SymbolKind.CLASS != symbol.kind()
                            && !symbols.containsKey(symbol.kind() + ":" + symbol.name())) {
                        throw new IllegalArgumentException(
                                "Utility fragment introduces an unregistered owned name: " + symbol.name());
                    }
                    var previous = symbols.putIfAbsent(symbol.kind() + ":" + symbol.name(), symbol);
                    if (null != previous && !previous.equals(symbol)) {
                        throw new IllegalArgumentException(
                                "Conflicting utility symbol " + symbol.name() + " in " + group.getKey());
                    }
                }
                fragment.metadata()
                        .occurrences()
                        .forEach(value -> occurrences.add(new CssResource.Occurrence(
                                value.symbol(), offset + value.start(), offset + value.end(), value.requires())));
                fragment.metadata()
                        .fontUses()
                        .forEach(value -> fonts.add(new CssResource.FontUse(
                                offset + value.start(), offset + value.end(), value.families())));
            }
            var combined = new CssResource(
                    metadata.owner(),
                    metadata.kind(),
                    List.copyOf(dependencies),
                    metadata.includes(),
                    metadata.implementation(),
                    metadata.bindings(),
                    List.copyOf(symbols.values()),
                    occurrences,
                    fonts,
                    metadata.fingerprint());
            String file = CssResource.path(metadata.owner());
            file = file.substring(file.lastIndexOf('/') + 1);
            merged.put(
                    metadata.owner(),
                    new CssCatalogue.Resource(
                            combined,
                            writer.css(),
                            JsonParser.parseString(writer.map(file)).getAsJsonObject()));
        }
        for (var resource : merged.values()) {
            for (var binding : resource.metadata().bindings()) {
                for (var id : binding.symbols()) {
                    var provider = merged.get(id.owner());
                    if (null != provider
                            && !provider.metadata().fingerprint().isEmpty()
                            && CssResource.SymbolKind.CLASS == id.kind()
                            && provider.metadata().symbols().stream()
                                    .noneMatch(symbol -> CssResource.SymbolKind.CLASS == symbol.kind()
                                            && id.name().equals(symbol.name()))) {
                        throw new IllegalArgumentException("Missing utility expansion fragment " + id.id()
                                + " requested by " + resource.metadata().owner() + "#" + binding.method());
                    }
                }
            }
        }
        return merged;
    }
}
