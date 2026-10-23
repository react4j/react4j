package react4j.css.processor;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import react4j.css.internal.CssConditions;
import react4j.css.internal.CssContainers;
import react4j.css.internal.CssSyntax;

/** Binds library-local container declarations and checks explicitly external ancestor queries. */
final class UtilityContainers {
    private UtilityContainers() {}

    static Map<String, String> names(
            String css, CascadingStyleSheet sheet, CssSyntax syntax, String root, Set<String> external) {
        var names = new LinkedHashMap<String, String>();
        var seen = new HashSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                var value = CssContainers.read(declaration);
                if (value.dynamic()) {
                    throw syntax.invalid("Utility container names must be static identifiers", declaration);
                }
                for (var term : value.names()) {
                    String name = CSSParseHelper.unescapeURL(term.getValue());
                    seen.add(name);
                    if (!external.contains(name)) {
                        names.put(name, root + "--container-" + Names.encode(name));
                    }
                }
            }
        });
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                for (var reference : CssConditions.read(css, rule).references()) {
                    if (!reference.property()) {
                        CssContainers.checkName(reference.name());
                        seen.add(reference.name());
                        if (!names.containsKey(reference.name()) && !external.contains(reference.name())) {
                            throw syntax.invalid(
                                    "Unknown container name; declare it or list an external ancestor: "
                                            + reference.name(),
                                    rule);
                        }
                    }
                }
            }
        });
        for (var name : external) {
            if (!seen.contains(name)) {
                throw new IllegalArgumentException("Unknown external container: " + name);
            }
        }
        return Map.copyOf(names);
    }
}
