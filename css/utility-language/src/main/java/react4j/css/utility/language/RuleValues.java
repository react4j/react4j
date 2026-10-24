package react4j.css.utility.language;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import react4j.css.internal.CssLexical;
import react4j.css.internal.CssSyntax;

final class RuleValues {
    private RuleValues() {}

    private static final Pattern NUMERIC =
            Pattern.compile("([+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?)([%a-zA-Z]*)");
    private static final Set<String> LENGTHS = Set.of(
            "px", "cm", "mm", "q", "in", "pt", "pc", "em", "ex", "cap", "ch", "ic", "rem", "rex", "rcap", "rch", "ric",
            "lh", "rlh", "vw", "vh", "vi", "vb", "vmin", "vmax", "svw", "svh", "svi", "svb", "svmin", "svmax", "lvw",
            "lvh", "lvi", "lvb", "lvmin", "lvmax", "dvw", "dvh", "dvi", "dvb", "dvmin", "dvmax", "cqw", "cqh", "cqi",
            "cqb", "cqmin", "cqmax");
    private static final Set<String> TYPES = Set.of(
            "integer",
            "number",
            "percentage",
            "length",
            "length-percentage",
            "angle",
            "time",
            "ratio",
            "string",
            "color",
            "box-shadow",
            "image",
            "position",
            "grid-tracks",
            "an+b");
    private static final Set<String> COLORS = Set.of(
            "transparent",
            "currentcolor",
            "black",
            "silver",
            "gray",
            "white",
            "maroon",
            "red",
            "purple",
            "fuchsia",
            "green",
            "lime",
            "olive",
            "yellow",
            "navy",
            "blue",
            "teal",
            "aqua",
            "orange",
            "rebeccapurple");
    private static final Set<String> COLOR_FUNCTIONS =
            Set.of("rgb", "rgba", "hsl", "hsla", "hwb", "lab", "lch", "oklab", "oklch", "color");

    record Scalar(BigDecimal number, String unit, String type) {}

    static String type(String text) {
        if (!text.startsWith("<") || !text.endsWith(">") || !TYPES.contains(text.substring(1, text.length() - 1))) {
            throw new RuleSyntax.Syntax("U_SOURCE_TYPE", "Unknown value type " + text, 0);
        }
        return text.substring(1, text.length() - 1);
    }

    static Scalar scalar(String value) {
        var match = NUMERIC.matcher(value);
        if (!match.matches()) {
            throw new IllegalArgumentException("Expected a numeric literal: " + value);
        }
        var unit = match.group(2).toLowerCase(Locale.ROOT);
        String type = unit.isEmpty()
                ? "number"
                : "%".equals(unit)
                        ? "percentage"
                        : LENGTHS.contains(unit)
                                ? "length"
                                : Set.of("deg", "grad", "rad", "turn").contains(unit)
                                        ? "angle"
                                        : Set.of("s", "ms").contains(unit) ? "time" : "unknown";
        if ("unknown".equals(type)) {
            throw new IllegalArgumentException("Unknown unit " + unit);
        }
        return new Scalar(new BigDecimal(match.group(1)), unit, type);
    }

    static String multiply(String step, String factor) {
        var scalar = scalar(step);
        return decimal(scalar.number().multiply(new BigDecimal(factor))) + scalar.unit();
    }

    static String decimal(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    static String common(String a, String b) {
        if (a.equals(b)) {
            return a;
        }
        if (Set.of("integer", "number").contains(a)
                && Set.of("integer", "number").contains(b)) {
            return "number";
        }
        if (Set.of("length", "percentage", "length-percentage").contains(a)
                && Set.of("length", "percentage", "length-percentage").contains(b)) {
            return "length-percentage";
        }
        throw new RuleSyntax.Syntax("U_SOURCE_TYPE", "Incompatible parameter types <" + a + "> and <" + b + ">", 0);
    }

    static void check(String type, String raw) {
        String value = RuleSyntax.clean(raw);
        if (value.isEmpty()
                || RuleSyntax.split(value, ';').size() > 1
                || RuleSyntax.split(value, '!').size() > 1) {
            bad(type, value);
        }
        new CssSyntax(".value { x: " + value + "; }", 0).parse(CssSyntax.Mode.UTILITY);
        if (!"an+b".equals(type)
                && value.startsWith("var(")
                && RuleSyntax.split(value, ',').size() == 1
                && value.endsWith(")")) {
            return;
        }
        boolean valid;
        switch (type) {
            case "integer" -> valid = value.matches("[+-]?[0-9]+");
            case "number", "percentage", "length", "length-percentage", "angle", "time" -> {
                try {
                    var scalar = scalar(value);
                    valid = type.equals(scalar.type())
                            || "length-percentage".equals(type)
                                    && Set.of("length", "percentage").contains(scalar.type())
                            || ("length".equals(type) || "length-percentage".equals(type))
                                    && scalar.unit().isEmpty()
                                    && 0 == scalar.number().signum();
                } catch (IllegalArgumentException e) {
                    valid = false;
                }
            }
            case "an+b" ->
                valid = value.toLowerCase(Locale.ROOT)
                        .matches("(?:odd|even|[+-]?[0-9]+|[+-]?(?:[0-9]+)?n(?:\\s*[+-]\\s*[0-9]+)?)");
            case "ratio" ->
                valid = value.matches("[+]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:\\s*/\\s*[+]?(?:[0-9]*\\.[0-9]+|[0-9]+))?");
            case "string" ->
                valid = (value.startsWith("\"") || value.startsWith("'"))
                        && 1 == CssLexical.tokens(value, 0, value.length()).size();
            case "color" -> {
                int open = value.indexOf('(');
                valid = COLORS.contains(value.toLowerCase(Locale.ROOT))
                        || value.matches("#(?:[0-9a-fA-F]{3,4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})")
                        || open > 0
                                && COLOR_FUNCTIONS.contains(
                                        value.substring(0, open).toLowerCase(Locale.ROOT))
                                && value.endsWith(")");
            }
            default -> valid = false;
        }
        if (!valid) {
            bad(type, value);
        }
    }

    private static void bad(String type, String value) {
        throw new RuleSyntax.Syntax("U_VALUE_TYPE", "Expected <" + type + ">; received " + value, 0);
    }
}
