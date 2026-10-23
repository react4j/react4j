package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** The name field of container-name and the container shorthand, excluding its type field. */
public final class CssContainers {
    private CssContainers() {}

    public record Value(List<CSSExpressionMemberTermSimple> names, boolean dynamic) {}

    public static void checkName(String name) {
        if (name.isEmpty()
                || name.codePoints().anyMatch(c -> Character.isWhitespace(c) || Character.isISOControl(c))
                || Set.of("none", "and", "or", "not", "default").contains(name.toLowerCase(Locale.ROOT))
                || CssAnimationValues.CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Invalid container name: " + name);
        }
    }

    public static Value read(CSSDeclaration declaration) {
        String property = CSSParseHelper.unescapeURL(declaration.getProperty()).toLowerCase(Locale.ROOT);
        if (!"container".equals(property) && !"container-name".equals(property)) {
            return new Value(List.of(), false);
        }
        var members = declaration.getExpression().getAllMembers();
        if (1 == members.size()
                && members.get(0) instanceof CSSExpressionMemberTermSimple term
                && !term.isStringLiteral()
                && ("none".equalsIgnoreCase(CSSParseHelper.unescapeURL(term.getValue()))
                        || CssAnimationValues.CSS_WIDE.contains(
                                CSSParseHelper.unescapeURL(term.getValue()).toLowerCase(Locale.ROOT)))) {
            return new Value(List.of(), false);
        }
        var names = new ArrayList<CSSExpressionMemberTermSimple>();
        for (var member : members) {
            if ("container".equals(property) && ECSSExpressionOperator.SLASH == member) {
                break;
            }
            if (!(member instanceof CSSExpressionMemberTermSimple term) || term.isStringLiteral()) {
                return new Value(List.copyOf(names), true);
            }
            String raw = term.getValue();
            var tokens = CssLexical.tokens(raw, 0, raw.length());
            if (1 != tokens.size()
                    || (ParserCSS30Constants.IDENT != tokens.get(0).kind()
                            && ParserCSS30Constants.DASHED_IDENT
                                    != tokens.get(0).kind())) {
                return new Value(List.copyOf(names), true);
            }
            String name = CSSParseHelper.unescapeURL(raw);
            if ("none".equalsIgnoreCase(name) && names.isEmpty()) {
                continue;
            }
            try {
                checkName(name);
            } catch (IllegalArgumentException e) {
                return new Value(List.copyOf(names), true);
            }
            names.add(term);
        }
        return new Value(List.copyOf(names), false);
    }
}
