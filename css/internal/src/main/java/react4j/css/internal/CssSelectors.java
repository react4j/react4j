package react4j.css.internal;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.parser.CSSParseHelper;
import java.util.Set;

/** Proves a positive managed-class requirement across selector-function branches. */
public final class CssSelectors {
    private CssSelectors() {}

    public static boolean requiresClass(CSSSelector selector, Set<String> managed) {
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelectorSimpleMember simple
                    && simple.isClass()
                    && managed.contains(
                            CSSParseHelper.unescapeURL(simple.getValue().substring(1)))) {
                return true;
            }
            if (member instanceof CSSSelector nested && requiresClass(nested, managed)
                    || member instanceof CSSSelectorMemberPseudoIs function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))
                    || member instanceof CSSSelectorMemberPseudoWhere function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))
                    || member instanceof CSSSelectorMemberPseudoHas function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))) {
                return true;
            }
        }
        return false;
    }
}
