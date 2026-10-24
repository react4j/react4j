package react4j.css.internal;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Resolves per-alias compiler names while preserving each emitted token's canonical identity. */
public final class CssSymbols {
    private CssSymbols() {}

    public record Key(CssResource.SymbolKind kind, String value) {}

    public record Definition(CssResource.SymbolId symbol, String readable, List<String> requires) {}

    public record Result(SourceMap.Writer output, List<CssResource.Occurrence> occurrences) {}

    public static Result resolve(
            SourceMap.Writer input, Map<Key, Definition> definitions, List<CssTokens.Token> tokens) {
        var edits = new ArrayList<SourceMap.Edit>();
        var occurrences = new ArrayList<CssResource.Occurrence>();
        int shift = 0;
        for (var token : tokens) {
            var definition = definitions.get(new Key(token.kind(), token.value()));
            if (null != definition) {
                if (!token.value().equals(definition.readable())) {
                    edits.add(new SourceMap.Edit(token.start(), token.end(), definition.readable()));
                }
                int start = token.start() + shift;
                occurrences.add(new CssResource.Occurrence(
                        definition.symbol(),
                        start,
                        start + definition.readable().length(),
                        definition.requires()));
                shift += definition.readable().length() - (token.end() - token.start());
            }
        }
        return new Result(
                edits.isEmpty()
                        ? input
                        : SourceMap.replace(
                                input.css(),
                                JsonParser.parseString(input.map("intermediate.css"))
                                        .getAsJsonObject(),
                                edits),
                List.copyOf(occurrences));
    }
}
