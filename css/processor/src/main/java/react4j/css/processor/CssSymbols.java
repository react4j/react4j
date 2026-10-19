package react4j.css.processor;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssTokens;
import react4j.css.internal.SourceMap;

/** Resolves per-alias compiler names while preserving each emitted token's canonical identity. */
final class CssSymbols {
    private CssSymbols() {}

    record Key(CssResource.SymbolKind kind, String value) {}

    record Definition(CssResource.SymbolId symbol, String readable, List<String> requires) {}

    record Result(SourceMap.Writer output, List<CssResource.Occurrence> occurrences) {}

    static Result resolve(SourceMap.Writer input, Map<Key, Definition> definitions) {
        var edits = new ArrayList<SourceMap.Edit>();
        var occurrences = new ArrayList<CssResource.Occurrence>();
        int shift = 0;
        for (var token : CssTokens.read(input.css())) {
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
        var map = JsonParser.parseString(input.map("intermediate.css")).getAsJsonObject();
        return new Result(
                edits.isEmpty() ? input : SourceMap.replace(input.css(), map, edits), List.copyOf(occurrences));
    }
}
