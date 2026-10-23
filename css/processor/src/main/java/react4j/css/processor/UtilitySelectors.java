package react4j.css.processor;

import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.Set;
import react4j.css.internal.CssSelectors;
import react4j.css.internal.CssSyntax;

/** Requires a managed class on every possible branch of an ordinary utility selector. */
final class UtilitySelectors {
    private UtilitySelectors() {}

    static void check(CascadingStyleSheet sheet, CssSyntax syntax, Set<String> managed) {
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onBeginStyleRule(CSSStyleRule rule) {
                if (rule.getAllDeclarations().stream()
                        .allMatch(declaration -> CSSParseHelper.unescapeURL(declaration.getProperty())
                                .startsWith("--"))) {
                    return;
                }
                for (var selector : rule.getAllSelectors()) {
                    if (!CssSelectors.requiresClass(selector, managed)) {
                        throw syntax.invalid(
                                "Ordinary utility rules require a managed class on every selector branch; put element"
                                        + " defaults in @GlobalCss",
                                selector);
                    }
                }
            }
        });
    }
}
