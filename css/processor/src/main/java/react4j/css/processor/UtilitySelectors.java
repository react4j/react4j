package react4j.css.processor;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.Set;
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
                    if (!guarded(selector, managed)) {
                        throw syntax.invalid(
                                "Ordinary utility rules require a managed class on every selector branch; put element"
                                        + " defaults in @GlobalCss",
                                selector);
                    }
                }
            }
        });
    }

    private static boolean guarded(CSSSelector selector, Set<String> managed) {
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelectorSimpleMember simple
                    && simple.isClass()
                    && managed.contains(
                            CSSParseHelper.unescapeURL(simple.getValue().substring(1)))) {
                return true;
            }
            if (member instanceof CSSSelector nested && guarded(nested, managed)
                    || member instanceof CSSSelectorMemberPseudoIs function
                            && function.getAllSelectors().stream().allMatch(child -> guarded(child, managed))
                    || member instanceof CSSSelectorMemberPseudoWhere function
                            && function.getAllSelectors().stream().allMatch(child -> guarded(child, managed))
                    || member instanceof CSSSelectorMemberPseudoHas function
                            && function.getAllSelectors().stream().allMatch(child -> guarded(child, managed))) {
                return true;
            }
        }
        return false;
    }
}
