package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberMath;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/** Distinguishes animation names from the other fields of the animation grammar. */
public final class CssAnimationValues {
    private static final String NUMBER = "[+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?";
    public static final Set<String> CSS_WIDE = Set.of("initial", "inherit", "unset", "revert", "revert-layer");

    public enum Mode {
        COMPONENT,
        UTILITY
    }

    public record Name(CSSExpressionMemberTermSimple term, String name) {}

    public record Value(List<Name> names, List<Name> ambiguous) {}

    private CssAnimationValues() {}

    public static String name(CSSExpressionMemberTermSimple term) {
        return name(term.getValue());
    }

    public static String name(String value) {
        return CSSParseHelper.unescapeURL(
                value.startsWith("'") || value.startsWith("\"")
                        ? Objects.requireNonNull(CSSParseHelper.extractStringValue(value))
                        : value);
    }

    public static Value read(CSSDeclaration declaration, CssSyntax syntax, Mode mode) {
        return read(declaration, syntax, mode, name -> {});
    }

    public static Value read(CSSDeclaration declaration, CssSyntax syntax, Mode mode, Consumer<Name> consumer) {
        boolean utility = Mode.UTILITY == mode;
        boolean shorthand = "animation".equalsIgnoreCase(CSSParseHelper.unescapeURL(declaration.getProperty()));
        var members = declaration.getExpression().getAllMembers();
        if (1 == members.size()
                && members.get(0) instanceof CSSExpressionMemberTermSimple simple
                && !simple.isStringLiteral()
                && CSS_WIDE.contains(simple.getValue().toLowerCase(Locale.ROOT))) {
            return new Value(List.of(), List.of());
        }
        var names = new ArrayList<Name>();
        var ambiguous = new ArrayList<Name>();
        var used = new HashSet<String>();
        boolean named = false;
        boolean uncertain = false;
        for (var member : members) {
            if (ECSSExpressionOperator.COMMA == member) {
                used.clear();
                named = false;
                uncertain = false;
                continue;
            }
            if (utility
                    && (member instanceof CSSExpressionMemberMath
                            || member instanceof CSSExpressionMemberFunction function
                                    && Set.of("var", "env")
                                            .contains(CSSParseHelper.unescapeURL(function.getFunctionName())
                                                    .toLowerCase(Locale.ROOT)))) {
                uncertain = true;
                continue;
            }
            if (shorthand && member instanceof CSSExpressionMemberFunction function) {
                if (Set.of("steps", "cubic-bezier", "linear")
                                .contains(function.getFunctionName().toLowerCase(Locale.ROOT))
                        && used.add("timing")) {
                    continue;
                }
                throw syntax.invalid(
                        "Unsupported animation shorthand function; use animation-name and separate longhands for"
                                + " dynamic values",
                        declaration);
            }
            if (!(member instanceof CSSExpressionMemberTermSimple simple)) {
                throw syntax.invalid("Animation names must be static identifiers", declaration);
            }
            String raw = simple.getValue();
            if (!utility && (raw.contains("\\") || simple.isStringLiteral())) {
                throw syntax.invalid("Animation names must be unescaped, unquoted identifiers", simple);
            }
            String name = utility ? name(simple) : raw;
            String lower = name.toLowerCase(Locale.ROOT);
            if (!simple.isStringLiteral() && CSS_WIDE.contains(lower)) {
                throw syntax.invalid("CSS-wide animation keywords must stand alone", simple);
            }
            if (shorthand && !simple.isStringLiteral()) {
                String category =
                        switch (lower) {
                            case "ease", "linear", "ease-in", "ease-out", "ease-in-out", "step-start", "step-end" ->
                                "timing";
                            case "infinite" -> "iteration";
                            case "normal", "reverse", "alternate", "alternate-reverse" -> "direction";
                            case "none", "forwards", "backwards", "both" -> "fill";
                            case "running", "paused" -> "play";
                            default -> "";
                        };
                if (!category.isEmpty() && uncertain && !named) {
                    ambiguous.add(new Name(simple, name));
                }
                if (!category.isEmpty() && used.add(category)) {
                    continue;
                }
                if (lower.matches(NUMBER + "(?:ms|s)")) {
                    if (used.add("duration")) {
                        if (Double.parseDouble(lower.substring(
                                        0, lower.endsWith("ms") ? lower.length() - 2 : lower.length() - 1))
                                < 0) {
                            throw syntax.invalid("Animation duration must not be negative", simple);
                        }
                    } else if (!used.add("delay")) {
                        throw syntax.invalid("Animation shorthand accepts at most two times", simple);
                    }
                    continue;
                }
                if (lower.matches(NUMBER)) {
                    if (!used.add("iteration") || Double.parseDouble(lower) < 0) {
                        throw syntax.invalid("Invalid animation iteration count", simple);
                    }
                    continue;
                }
            }
            if (named) {
                throw syntax.invalid("Each animation accepts one name", simple);
            }
            if (simple.isStringLiteral() || !"none".equals(lower)) {
                var selected = new Name(simple, name);
                names.add(selected);
                consumer.accept(selected);
            }
            named = true;
        }
        return new Value(List.copyOf(names), List.copyOf(ambiguous));
    }
}
