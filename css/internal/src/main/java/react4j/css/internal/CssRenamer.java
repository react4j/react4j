package react4j.css.internal;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssResource.SymbolId;

/** Applies one link's canonical assignments to recorded, retained generated identifiers. */
public final class CssRenamer {
    private final Map<SymbolId, CssNames.Entry> names;
    private final Map<String, CssNames.Entry> compressed;

    public CssRenamer(CssNames table) {
        names = table.entries().stream().collect(Collectors.toMap(CssNames.Entry::symbol, entry -> entry));
        compressed = table.entries().stream()
                .filter(entry -> !entry.readable().equals(entry.compiled()))
                .collect(Collectors.toMap(CssNames.Entry::compiled, entry -> entry));
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
        for (var token : CssTokens.read(css)) {
            String raw = token.name();
            var entry = compressed.get(raw);
            if (null != entry && token.kind() == entry.symbol().kind()) {
                throw new IllegalArgumentException(path + ": raw CSS name conflicts with a compressed symbol: " + raw
                        + " (" + entry.symbol().id() + "); mark the symbol stable or use a distinct link prefix");
            }
        }
    }
}
