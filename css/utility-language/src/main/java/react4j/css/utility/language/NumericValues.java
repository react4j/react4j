package react4j.css.utility.language;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import react4j.css.internal.CssLexical;

/** Dimensional arithmetic, including unknown browser substitutions and percent hints. */
final class NumericValues {
    private NumericValues() {}

    record Dimension(Map<String, Integer> powers, boolean unknown) {
        Dimension {
            powers = Map.copyOf(powers);
        }

        static Dimension number() {
            return new Dimension(Map.of(), false);
        }

        static Dimension unknownValue() {
            return new Dimension(Map.of(), true);
        }

        static Dimension of(String type) {
            return new Dimension(Map.of(type, 1), false);
        }

        Dimension product(Dimension other, int sign) {
            if (unknown || other.unknown) {
                return unknownValue();
            }
            var values = new HashMap<>(powers);
            other.powers.forEach((key, value) -> values.merge(key, sign * value, Integer::sum));
            values.values().removeIf(value -> 0 == value);
            return new Dimension(values, false);
        }

        Dimension sum(Dimension other) {
            if (unknown) {
                return other;
            }
            if (other.unknown) {
                return this;
            }
            if (!powers.equals(other.powers)) {
                throw error("Incompatible dimensions in numeric sum or comparison");
            }
            return this;
        }
    }

    static void check(String type, String value) {
        var dimension = infer(value, type.endsWith("-percentage") ? type.substring(0, type.indexOf('-')) : "");
        var expected = Set.of("integer", "number", "number-percentage").contains(type)
                ? Dimension.number()
                : Dimension.of(type.endsWith("-percentage") ? type.substring(0, type.indexOf('-')) : type);
        if (!dimension.unknown() && !dimension.powers().equals(expected.powers())) {
            throw error("Expected <" + type + ">; numeric value has dimensions " + dimension.powers());
        }
        if ("integer".equals(type) && null == RuleSyntax.function(value) && !value.matches("[+-]?[0-9]+")) {
            throw error("An integer literal requires signed decimal digits");
        }
    }

    private static Dimension infer(String text, String percentageHint) {
        var function = RuleSyntax.function(text);
        if (null == function) {
            var scalar = RuleValues.scalar(text);
            return "number".equals(scalar.type())
                            || "number".equals(percentageHint) && "percentage".equals(scalar.type())
                    ? Dimension.number()
                    : Dimension.of(
                            !percentageHint.isEmpty() && "percentage".equals(scalar.type())
                                    ? percentageHint
                                    : scalar.type());
        }
        return function(function, percentageHint);
    }

    private static Dimension function(RuleSyntax.Function function, String percentageHint) {
        if ("var".equals(function.name())) {
            return Dimension.unknownValue();
        }
        if ("anchor".equals(function.name()) || "anchor-size".equals(function.name())) {
            return Dimension.of("length");
        }
        if (!Set.of("calc", "min", "max", "clamp").contains(function.name())) {
            throw error("Unsupported numeric function " + function.name());
        }
        var arguments = RuleSyntax.split(function.arguments(), ',');
        if ("calc".equals(function.name()) && 1 != arguments.size()
                || "clamp".equals(function.name()) && 3 != arguments.size()) {
            throw error("Wrong number of numeric function arguments");
        }
        Dimension result = Dimension.unknownValue();
        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i).text();
            if ("clamp".equals(function.name()) && 1 != i && "none".equalsIgnoreCase(argument)) {
                continue;
            }
            result = result.sum(new Arithmetic(argument, percentageHint).read());
        }
        return result;
    }

    private static final class Arithmetic {
        private final String source;
        private final List<CssLexical.Token> tokens;
        private final String percentageHint;
        private int index;

        Arithmetic(String source, String percentageHint) {
            this.source = source;
            this.percentageHint = percentageHint;
            tokens = CssLexical.tokens(source, 0, source.length()).stream()
                    .filter(token -> !token.value().isBlank())
                    .toList();
        }

        Dimension read() {
            var value = sum();
            if (index != tokens.size()) {
                throw error("Unexpected numeric component " + tokens.get(index).value());
            }
            return value;
        }

        Dimension sum() {
            var value = product();
            while (take("+") || take("-")) {
                value = value.sum(product());
            }
            return value;
        }

        Dimension product() {
            var value = atom();
            while (index < tokens.size()) {
                if (take("*")) {
                    value = value.product(atom(), 1);
                } else if (take("/")) {
                    value = value.product(atom(), -1);
                } else {
                    break;
                }
            }
            return value;
        }

        Dimension atom() {
            if (take("+") || take("-")) {
                return atom();
            }
            if (take("(")) {
                var value = sum();
                if (!take(")")) {
                    throw error("Unclosed numeric parentheses");
                }
                return value;
            }
            if (index == tokens.size()) {
                throw error("Missing numeric operand");
            }
            var token = tokens.get(index++);
            if (token.value().endsWith("(")) {
                var function = RuleSyntax.functions(source).stream()
                        .filter(value -> value.start() == token.start())
                        .findFirst()
                        .orElseThrow();
                while (index < tokens.size() && tokens.get(index).start() < function.end()) {
                    index++;
                }
                return function(function, percentageHint);
            }
            if (Set.of("e", "pi", "infinity", "-infinity", "nan")
                    .contains(token.name().toLowerCase(Locale.ROOT))) {
                return Dimension.number();
            }
            return infer(token.value(), percentageHint);
        }

        boolean take(String value) {
            if (index < tokens.size() && value.equals(tokens.get(index).value())) {
                index++;
                return true;
            }
            return false;
        }
    }

    private static RuleSyntax.Syntax error(String message) {
        return new RuleSyntax.Syntax("U_VALUE_TYPE", message, 0);
    }
}
