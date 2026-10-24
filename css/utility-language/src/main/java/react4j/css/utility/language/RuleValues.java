package react4j.css.utility.language;

import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import react4j.css.internal.CssLexical;
import react4j.css.internal.CssSyntax;

final class RuleValues {
    private RuleValues() {}

    private static final Pattern NUMERIC = Pattern.compile("([+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?)(.*)");
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
        var lexical = CssLexical.tokens(value, 0, value.length()).stream()
                .filter(token -> !token.value().isBlank())
                .toList();
        int first =
                lexical.size() > 1 && Set.of("+", "-").contains(lexical.get(0).value()) ? 1 : 0;
        if (lexical.size() != first + 1) {
            throw new IllegalArgumentException("Expected one numeric token: " + value);
        }
        int kind = lexical.get(first).kind();
        var unit = CSSParseHelper.unescapeURL(match.group(2)).toLowerCase(Locale.ROOT);
        if ("%".equals(unit) && ParserCSS30Constants.PERCENTAGE != kind
                || unit.isEmpty() && ParserCSS30Constants.NUMBER != kind
                || !unit.isEmpty() && !"%".equals(unit) && ParserCSS30Constants.DIMENSION != kind) {
            throw new IllegalArgumentException("Expected a numeric dimension: " + value);
        }
        String type = unit.isEmpty()
                ? "number"
                : "%".equals(unit)
                        ? "percentage"
                        : LENGTHS.contains(unit)
                                ? "length"
                                : Set.of("deg", "grad", "rad", "turn").contains(unit)
                                        ? "angle"
                                        : Set.of("s", "ms").contains(unit)
                                                ? "time"
                                                : Set.of("x", "dpi", "dpcm", "dppx")
                                                                .contains(unit)
                                                        ? "resolution"
                                                        : "unknown";
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
        var functions = RuleSyntax.functions(value);
        if (functions.stream().anyMatch(function -> "param".equals(function.name()))) {
            throw new RuleSyntax.Syntax("U_PARAMETER", "param() belongs in a template", 0);
        }
        for (var function : functions) {
            if ("var".equals(function.name())) {
                String name = RuleSyntax.split(function.arguments(), ',').get(0).text();
                var tokens = CssLexical.tokens(name, 0, name.length()).stream()
                        .filter(token -> !token.value().isBlank())
                        .toList();
                if (1 != tokens.size()
                        || ParserCSS30Constants.DASHED_IDENT != tokens.get(0).kind()
                                && ParserCSS30Constants.IDENT != tokens.get(0).kind()
                        || !tokens.get(0).name().startsWith("--")
                        || tokens.get(0).name().length() <= 2) {
                    bad(type, value);
                }
            }
        }
        if ("an+b".equals(type)) {
            if (!value.toLowerCase(Locale.ROOT)
                    .matches("(?:odd|even|[+-]?[0-9]+|[+-]?(?:[0-9]+)?n(?:\\s*[+-]\\s*[0-9]+)?)")) {
                bad(type, value);
            }
            new CssSyntax(".value:nth-child(" + value.toLowerCase(Locale.ROOT).replaceAll("\\s+", "") + "){x:0;}", 0)
                    .parse(CssSyntax.Mode.UTILITY);
            return;
        }
        new CssSyntax(".value { x: " + value + "; }", 0).parse(CssSyntax.Mode.UTILITY);
        if (CompoundValues.variable(value)) {
            return;
        }
        boolean valid;
        switch (type) {
            case "integer", "number", "percentage", "length", "length-percentage", "angle", "time" -> {
                if ("length".equals(type) || "length-percentage".equals(type)) {
                    try {
                        var scalar = scalar(value);
                        if (scalar.unit().isEmpty() && 0 == scalar.number().signum()) {
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        /* A numeric function is checked below. */
                    }
                }
                NumericValues.check(type, value);
                return;
            }
            case "ratio" -> {
                var parts = RuleSyntax.split(value, '/');
                valid = parts.size() <= 2
                        && parts.stream()
                                .allMatch(part -> CompoundValues.variable(part.text())
                                        || part.text().matches("[+]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?"));
            }
            case "string" ->
                valid = (value.startsWith("\"") || value.startsWith("'"))
                        && 1 == CssLexical.tokens(value, 0, value.length()).size();
            case "color" -> valid = CompoundValues.color(value);
            case "box-shadow" -> valid = CompoundValues.shadow(value);
            case "position" -> valid = CompoundValues.position(value);
            case "image" -> valid = CompoundValues.image(value, false);
            case "grid-tracks" -> valid = CompoundValues.tracks(value, false);
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
