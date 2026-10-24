package react4j.css.utility.language;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/** Inclusive input ranges with exact rational unit conversion and explicit deferred comparisons. */
final class RuleBounds {
    private RuleBounds() {}

    private record Factor(BigDecimal numerator, BigDecimal denominator) {
        Factor(String numerator, String denominator) {
            this(new BigDecimal(numerator), new BigDecimal(denominator));
        }
    }

    private static final Map<String, Factor> FACTORS = Map.ofEntries(
            Map.entry("px", new Factor("1", "1")),
            Map.entry("in", new Factor("96", "1")),
            Map.entry("cm", new Factor("4800", "127")),
            Map.entry("mm", new Factor("480", "127")),
            Map.entry("q", new Factor("120", "127")),
            Map.entry("pt", new Factor("4", "3")),
            Map.entry("pc", new Factor("16", "1")),
            Map.entry("s", new Factor("1000", "1")),
            Map.entry("ms", new Factor("1", "1")),
            Map.entry("turn", new Factor("360", "1")),
            Map.entry("grad", new Factor("9", "10")),
            Map.entry("deg", new Factor("1", "1")));
    private static final BigDecimal PI_LOWER = new BigDecimal("3.14159265358979323846264338327950288419716939937510");
    private static final BigDecimal PI_UPPER =
            PI_LOWER.add(new BigDecimal("0.00000000000000000000000000000000000000000000000001"));

    private record Interval(String lower, String upper) {}

    static void validate(String type, String interval) {
        if (interval.isEmpty()) {
            return;
        }
        if (!Set.of("integer", "number", "percentage", "length", "length-percentage", "angle", "time")
                .contains(type)) {
            throw error("U_RANGE_BOUND", "This type has no numeric interval: " + type);
        }
        var bounds = read(interval);
        var lower = bound(type, bounds.lower());
        var upper = bound(type, bounds.upper());
        if (null == lower && null == upper) {
            throw error("U_RANGE_BOUND", "At least one interval endpoint must be bounded");
        }
        if (null != lower && null != upper) {
            var order = compare(lower, upper);
            if (null != order && order > 0) {
                throw error("U_RANGE_BOUND", "Inverted interval " + interval);
            }
        }
    }

    static List<UtilityLanguage.BoundCheck> check(String type, String interval, String value) {
        if (interval.isEmpty()) {
            return List.of();
        }
        var bounds = read(interval);
        RuleValues.Scalar scalar;
        try {
            scalar = RuleValues.scalar(RuleSyntax.clean(value));
        } catch (IllegalArgumentException e) {
            return List.of(new UtilityLanguage.BoundCheck(interval, value, "browser-dependent"));
        }
        boolean deferred = false;
        for (boolean lower : List.of(true, false)) {
            var endpoint = bound(type, lower ? bounds.lower() : bounds.upper());
            if (null != endpoint) {
                var order = compare(scalar, endpoint);
                if (null == order) {
                    deferred = true;
                } else if (lower ? order < 0 : order > 0) {
                    throw error("U_VALUE_RANGE", "Value " + value + " is outside " + interval);
                }
            }
        }
        return List.of(new UtilityLanguage.BoundCheck(interval, value, deferred ? "browser-dependent" : "checked"));
    }

    private static Interval read(String interval) {
        if (!interval.startsWith("[") || !interval.endsWith("]")) {
            throw error("U_RANGE_BOUND", "Expected within [lower, upper]");
        }
        var parts = RuleSyntax.split(interval.substring(1, interval.length() - 1), ',');
        if (2 != parts.size()) {
            throw error("U_RANGE_BOUND", "An interval requires two endpoints");
        }
        return new Interval(parts.get(0).text(), parts.get(1).text());
    }

    private static RuleValues.@Nullable Scalar bound(String type, String text) {
        if ("*".equals(text)) {
            return null;
        }
        try {
            var scalar = RuleValues.scalar(text);
            if (scalar.unit().isEmpty() && 0 == scalar.number().signum()) {
                return scalar;
            }
            RuleValues.check(type, text);
            return scalar;
        } catch (IllegalArgumentException e) {
            throw error("U_RANGE_BOUND", "Endpoint " + text + " is not a literal <" + type + "> bound");
        }
    }

    private static @Nullable Integer compare(RuleValues.Scalar a, RuleValues.Scalar b) {
        if (a.unit().equals(b.unit())) {
            return a.number().compareTo(b.number());
        }
        if (0 == a.number().signum() || 0 == b.number().signum()) {
            return Integer.compare(a.number().signum(), b.number().signum());
        }
        if (!a.type().equals(b.type())) {
            return null;
        }
        if ("angle".equals(a.type()) && ("rad".equals(a.unit()) || "rad".equals(b.unit()))) {
            boolean first = "rad".equals(a.unit());
            var radians = first ? a : b;
            var degrees = first ? b : a;
            var factor = FACTORS.get(degrees.unit());
            if (null == factor) {
                return null;
            }
            var lhs = radians.number().multiply(new BigDecimal("180")).multiply(factor.denominator());
            var rhs = degrees.number().multiply(factor.numerator());
            var low = rhs.multiply(PI_LOWER).min(rhs.multiply(PI_UPPER));
            var high = rhs.multiply(PI_LOWER).max(rhs.multiply(PI_UPPER));
            int order = lhs.compareTo(low) < 0 ? -1 : lhs.compareTo(high) > 0 ? 1 : 0;
            return 0 == order ? null : first ? order : -order;
        }
        var left = FACTORS.get(a.unit());
        var right = FACTORS.get(b.unit());
        if (null == left || null == right) {
            return null;
        }
        return a.number()
                .multiply(left.numerator())
                .multiply(right.denominator())
                .compareTo(b.number().multiply(right.numerator()).multiply(left.denominator()));
    }

    private static RuleSyntax.Syntax error(String code, String message) {
        return new RuleSyntax.Syntax(code, message, 0);
    }
}
