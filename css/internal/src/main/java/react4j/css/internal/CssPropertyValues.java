package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.List;

/** Custom-property targets in transition values, including variable fallbacks. */
public final class CssPropertyValues {
    private CssPropertyValues() {}

    public static List<CSSExpressionMemberTermSimple> names(CSSDeclaration declaration) {
        String property = CSSParseHelper.unescapeURL(declaration.getProperty());
        if (!"transition".equalsIgnoreCase(property) && !"transition-property".equalsIgnoreCase(property)) {
            return List.of();
        }
        var names = new ArrayList<CSSExpressionMemberTermSimple>();
        names(declaration.getExpression(), 0, names);
        return List.copyOf(names);
    }

    private static void names(CSSExpression expression, int first, List<CSSExpressionMemberTermSimple> names) {
        for (int index = first; index < expression.getMemberCount(); index++) {
            var member = expression.getMemberAtIndex(index);
            if (member instanceof CSSExpressionMemberTermSimple term
                    && CSSParseHelper.unescapeURL(term.getValue()).startsWith("--")) {
                names.add(term);
            } else if (member instanceof CSSExpressionMemberFunction function
                    && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                    && null != function.getExpression()) {
                // The first argument names the variable; only its fallback supplies transition targets.
                names(function.getExpression(), 2, names);
            }
        }
    }
}
