package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Static anchor identifiers, distinct from identically spelled custom properties. */
public final class CssAnchors {
    private CssAnchors() {}

    public record Value(List<CSSExpressionMemberTermSimple> names, boolean dynamic) {}

    public static void checkName(String name) {
        var tokens = CssLexical.tokens(name, 0, name.length());
        if (1 != tokens.size()
                || (ParserCSS30Constants.DASHED_IDENT != tokens.get(0).kind()
                        && ParserCSS30Constants.IDENT != tokens.get(0).kind())
                || !name.startsWith("--")
                || 2 == name.length()) {
            throw new IllegalArgumentException("U_ANCHOR_NAME: Expected a full static --anchor name: " + name);
        }
    }

    public static Value read(CSSDeclaration declaration) {
        String property = CSSParseHelper.unescapeURL(declaration.getProperty()).toLowerCase(Locale.ROOT);
        if (!Set.of("anchor-name", "position-anchor", "anchor-scope").contains(property)) {
            return new Value(List.of(), false);
        }
        var members = declaration.getExpression().getAllMembers();
        if (1 == members.size()
                && members.get(0) instanceof CSSExpressionMemberTermSimple term
                && !term.isStringLiteral()) {
            String value = CSSParseHelper.unescapeURL(term.getValue()).toLowerCase(Locale.ROOT);
            var keywords = "position-anchor".equals(property)
                    ? Set.of("auto", "none", "normal", "match-parent")
                    : "anchor-scope".equals(property) ? Set.of("none", "all") : Set.of("none");
            if (keywords.contains(value) || CssAnimationValues.CSS_WIDE.contains(value)) {
                return new Value(List.of(), false);
            }
        }
        var names = new ArrayList<CSSExpressionMemberTermSimple>();
        boolean name = true;
        for (var member : members) {
            if (!name && ECSSExpressionOperator.COMMA == member && !"position-anchor".equals(property)) {
                name = true;
            } else if (name && member instanceof CSSExpressionMemberTermSimple term && !term.isStringLiteral()) {
                try {
                    checkName(term.getValue());
                } catch (IllegalArgumentException e) {
                    return new Value(List.copyOf(names), true);
                }
                names.add(term);
                name = false;
            } else {
                return new Value(List.copyOf(names), true);
            }
        }
        return new Value(List.copyOf(names), name);
    }

    public static List<CSSExpressionMemberTermSimple> references(CSSExpression expression) {
        var names = new ArrayList<CSSExpressionMemberTermSimple>();
        CssExpressions.visit(expression, member -> {
            if (member instanceof CSSExpressionMemberFunction function
                    && Set.of("anchor", "anchor-size")
                            .contains(CSSParseHelper.unescapeURL(function.getFunctionName())
                                    .toLowerCase(Locale.ROOT))) {
                var arguments = function.getExpression();
                if (null != arguments) {
                    for (var part : arguments.getAllMembers()) {
                        if (ECSSExpressionOperator.COMMA == part) {
                            break;
                        }
                        if (part instanceof CSSExpressionMemberTermSimple term
                                && !term.isStringLiteral()
                                && CSSParseHelper.unescapeURL(term.getValue()).startsWith("--")) {
                            names.add(term);
                        }
                    }
                }
            }
        });
        return List.copyOf(names);
    }
}
