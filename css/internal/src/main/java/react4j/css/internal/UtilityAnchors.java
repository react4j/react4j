package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Provider-wide anchor ownership, independent of requested setup classes. */
final class UtilityAnchors {
    private UtilityAnchors() {}

    static Map<String, String> names(CascadingStyleSheet sheet, CssSyntax syntax, String root, Set<String> external) {
        external.forEach(CssAnchors::checkName);
        var names = new LinkedHashMap<String, String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                var value = CssAnchors.read(declaration);
                if (value.dynamic()) {
                    throw syntax.invalid("U_DYNAMIC_NAME: Anchor names require static --identifiers", declaration);
                }
                if ("anchor-name".equalsIgnoreCase(CSSParseHelper.unescapeURL(declaration.getProperty()))) {
                    for (var term : value.names()) {
                        String name = CSSParseHelper.unescapeURL(term.getValue());
                        if (!external.contains(name)) {
                            names.put(name, "--" + root + "--anchor-" + CssIdentity.encode(name.substring(2)));
                        }
                    }
                }
            }
        });
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                var references = new ArrayList<>(CssAnchors.read(declaration).names());
                references.addAll(CssAnchors.references(declaration.getExpression()));
                for (var term : references) {
                    String name = CSSParseHelper.unescapeURL(term.getValue());
                    if (!names.containsKey(name) && !external.contains(name)) {
                        throw syntax.invalid(
                                "U_ANCHOR_NAME: Unknown anchor; declare anchor-name or externalAnchors: " + name,
                                declaration);
                    }
                }
            }
        });
        return Map.copyOf(names);
    }
}
