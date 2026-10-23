package react4j.css.internal;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssResource.SymbolId;
import react4j.css.internal.CssResource.SymbolKind;

/** Applies one link's canonical assignments to recorded, retained generated identifiers. */
public final class CssRenamer {
    private final Map<SymbolId, CssNames.Entry> names;
    private final Map<String, CssNames.Entry> compressed;
    private final List<AttributeValue> attributeValues = new ArrayList<>();

    private record AttributeValue(SymbolKind kind, String readable, String compiled, String origin) {}

    public CssRenamer(CssNames table, List<CssResource> resources) {
        names = table.entries().stream().collect(Collectors.toMap(CssNames.Entry::symbol, entry -> entry));
        compressed = table.entries().stream()
                .filter(entry -> !entry.readable().equals(entry.compiled()))
                .collect(Collectors.toMap(CssNames.Entry::compiled, entry -> entry));
        compressed
                .values()
                .forEach(entry -> attributeValues.add(new AttributeValue(
                        entry.symbol().kind(),
                        entry.readable(),
                        entry.compiled(),
                        entry.symbol().id())));
        for (var resource : resources) {
            for (var binding : resource.bindings()) {
                if (SymbolKind.CLASS == binding.kind() && binding.symbols().size() > 1) {
                    String compiled = binding.symbols().stream()
                            .map(id -> {
                                var entry = names.get(id);
                                if (null == entry) {
                                    throw new IllegalArgumentException("Unknown class binding symbol: " + id.id());
                                }
                                return entry.compiled();
                            })
                            .collect(Collectors.joining(" "));
                    if (!binding.value().equals(compiled)) {
                        attributeValues.add(new AttributeValue(
                                SymbolKind.CLASS,
                                binding.value(),
                                compiled,
                                resource.owner() + "#" + binding.method()));
                    }
                }
            }
        }
    }

    public Resource rename(Resource resource) {
        var edits = new ArrayList<SourceMap.Edit>();
        for (var occurrence : resource.metadata().occurrences()) {
            var entry = names.get(occurrence.symbol());
            if (null == entry
                    || occurrence.end() > resource.css().length()
                    || !resource.css()
                            .substring(occurrence.start(), occurrence.end())
                            .equals(entry.readable())) {
                throw new IllegalArgumentException("CSS occurrence differs from its canonical symbol: "
                        + occurrence.symbol().id());
            }
            if (!entry.readable().equals(entry.compiled())) {
                edits.add(new SourceMap.Edit(occurrence.start(), occurrence.end(), entry.compiled()));
            }
        }
        if (edits.isEmpty()) {
            return resource;
        }
        var writer = SourceMap.replace(resource.css(), resource.map(), edits);
        return new Resource(
                resource.metadata(),
                writer.css(),
                JsonParser.parseString(writer.map(resource.map().get("file").getAsString()))
                        .getAsJsonObject());
    }

    /** Raw variant names must not acquire the meaning of a compressed generated symbol. */
    public void checkShared(String css, String path) {
        checkNames(CssTokens.parse(css), path);
    }

    void checkNames(CssTokens.Parsed parsed, String path) {
        for (var token : parsed.tokens()) {
            String raw = token.name();
            var entry = SymbolKind.FONT == token.kind()
                    ? compressed.values().stream()
                            .filter(candidate ->
                                    SymbolKind.FONT == candidate.symbol().kind()
                                            && CssFontValues.canonical(candidate.compiled())
                                                    .equals(raw))
                            .findFirst()
                            .orElse(null)
                    : compressed.get(raw);
            if (null != entry && token.kind() == entry.symbol().kind()) {
                throw new IllegalArgumentException(path + ": raw CSS name conflicts with a compressed symbol: " + raw
                        + " (" + entry.symbol().id() + "); mark the symbol stable or use a distinct link prefix");
            }
        }
        for (var attribute : parsed.attributes()) {
            for (var entry : attributeValues) {
                if (attribute.kind() == entry.kind()
                        && (attribute.references(entry.compiled())
                                || attribute.gains(entry.readable(), entry.compiled()))) {
                    throw new IllegalArgumentException(
                            path + ": raw CSS attribute selector conflicts with a compressed symbol: "
                                    + attribute.value() + " (" + entry.origin()
                                    + "); mark the symbol stable, change the link prefix or disable compression");
                }
            }
        }
    }
}
