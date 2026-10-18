package react4j.css.bundler;

import com.google.gson.JsonParser;
import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.AbstractHasTopLevelRules;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.SourceMap;

/** Proves impossible selectors and removes their complete byte spans. */
final class CssPruner {
    private final String css;
    private final Set<String> known;
    private final Set<String> live;
    private final List<SourceMap.Span> removed = new ArrayList<>();

    private CssPruner(String css, Set<String> known, Set<String> live) {
        this.css = css;
        this.known = known;
        this.live = live;
    }

    static Resource prune(Resource resource, Set<String> known, Set<String> live) {
        var pruner = new CssPruner(resource.css(), known, live);
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                resource.css(), new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        pruner.rules(sheet.getAllRules());
        var spans = new ArrayList<SourceMap.Span>();
        int offset = 0;
        for (var span : pruner.removed.stream()
                .sorted(Comparator.comparingInt(SourceMap.Span::start))
                .toList()) {
            if (span.start() > offset) {
                spans.add(new SourceMap.Span(offset, span.start()));
            }
            offset = Math.max(offset, span.end());
        }
        spans.add(new SourceMap.Span(offset, resource.css().length()));
        var output = SourceMap.project(resource.css(), resource.map(), spans);
        return new Resource(
                resource.metadata(),
                output.css(),
                JsonParser.parseString(output.map(resource.map().get("file").getAsString()))
                        .getAsJsonObject());
    }

    private void rules(List<?> rules) {
        for (var rule : rules) {
            if (rule instanceof CSSStyleRule style) {
                if (style.getAllSelectors().stream().noneMatch(this::possible)) {
                    remove(style);
                } else {
                    rules(style.getAllRules());
                }
            } else if (rule instanceof AbstractHasTopLevelRules nested) {
                rules(nested.getAllRules());
            }
        }
    }

    private boolean possible(CSSSelector selector) {
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelectorSimpleMember simple && (simple.isClass() || simple.isHash())) {
                String name = simple.getValue();
                if (known.contains(name) && !live.contains(name)) {
                    return false;
                }
            } else if (member instanceof CSSSelector nested && !possible(nested)) {
                return false;
            } else if (member instanceof CSSSelectorMemberPseudoIs function
                    && function.getAllSelectors().stream().noneMatch(this::possible)) {
                return false;
            } else if (member instanceof CSSSelectorMemberPseudoWhere function
                    && function.getAllSelectors().stream().noneMatch(this::possible)) {
                return false;
            }
            // Negation and unrecognized functions are uncertain. Preserve their spelling and specificity.
        }
        return true;
    }

    private void remove(ICSSSourceLocationAware rule) {
        var location = Objects.requireNonNull(rule.getSourceLocation());
        removed.add(new SourceMap.Span(
                offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber()),
                offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1));
    }

    private int offset(int line, int column) {
        int result = 0;
        for (int i = 1; i < line; i++) {
            result = css.indexOf('\n', result) + 1;
        }
        return result + column - 1;
    }
}
