package react4j.css.utility.language;

import com.helger.css.parser.ParserCSS30Constants;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import react4j.css.internal.CssContainers;
import react4j.css.internal.CssLexical;

/** The deliberately bounded container-condition grammar; binding remains a shared CSS concern. */
final class RuleConditions {
    private RuleConditions() {}

    private enum Kind {
        SIZE,
        STYLE,
        SCROLL
    }

    static void check(String text, String type, int offset) {
        try {
            var parts = RuleSyntax.components(text);
            if (!parts.isEmpty() && identifier(parts.get(0)) && !"not".equalsIgnoreCase(parts.get(0))) {
                CssContainers.checkName(parts.get(0));
                text = text.substring(parts.get(0).length()).trim();
            }
            expression(text, Kind.SIZE, type, false);
        } catch (IllegalArgumentException e) {
            throw RuleLibrary.error("U_CONTAINER_CONDITION", String.valueOf(e.getMessage()), offset);
        }
    }

    private static void expression(String text, Kind kind, String type, boolean feature) {
        var parts = RuleSyntax.components(text);
        if (parts.isEmpty()) {
            throw invalid("Expected a container condition");
        }
        if ("not".equalsIgnoreCase(parts.get(0))) {
            if (2 != parts.size()) {
                throw invalid("not requires one parenthesized condition");
            }
            atom(parts.get(1), kind, type);
        } else if (parts.size() >= 3
                && Set.of("and", "or").contains(parts.get(1).toLowerCase(Locale.ROOT))) {
            if (0 == parts.size() % 2) {
                throw invalid("A boolean operator requires a condition on each side");
            }
            String operator = parts.get(1);
            for (int i = 0; i < parts.size(); i += 2) {
                if (0 < i && !operator.equalsIgnoreCase(parts.get(i - 1))) {
                    throw invalid("Group mixed and/or conditions with parentheses");
                }
                atom(parts.get(i), kind, type);
            }
        } else if (1 == parts.size() && (text.startsWith("(") || null != RuleSyntax.function(text))) {
            atom(text, kind, type);
        } else if (feature) {
            feature(text, kind, type);
        } else {
            throw invalid("Expected a parenthesized size condition, style() or scroll-state()");
        }
    }

    private static void atom(String text, Kind kind, String type) {
        if (text.startsWith("(") && text.endsWith(")")) {
            expression(text.substring(1, text.length() - 1).trim(), kind, type, true);
            return;
        }
        var function = RuleSyntax.function(text);
        if (Kind.SIZE == kind && null != function) {
            if ("style".equals(function.name())) {
                expression(function.arguments().trim(), Kind.STYLE, type, true);
                return;
            } else if ("scroll-state".equals(function.name())) {
                expression(function.arguments().trim(), Kind.SCROLL, type, true);
                return;
            }
        }
        throw invalid("Unsupported query function or ungrouped condition: " + text);
    }

    private static void feature(String text, Kind kind, String type) {
        if (Kind.SIZE != kind && RuleSyntax.functions(text).stream().anyMatch(f -> "param".equals(f.name()))) {
            throw invalid("Style and scroll-state conditions must be static");
        }
        var colon = RuleSyntax.split(text, ':');
        if (Kind.STYLE == kind) {
            if (colon.size() > 2
                    || !identifier(colon.get(0).text())
                    || !colon.get(0).text().startsWith("--")
                    || 2 == colon.size() && colon.get(1).text().isEmpty()
                    || RuleSyntax.split(text, ';').size() > 1) {
                throw invalid("A style test requires a static custom property and optional value");
            }
            return;
        }
        if (Kind.SCROLL == kind) {
            String name = colon.get(0).text().toLowerCase(Locale.ROOT);
            Set<String> values =
                    switch (name) {
                        case "stuck" ->
                            Set.of(
                                    "none",
                                    "top",
                                    "right",
                                    "bottom",
                                    "left",
                                    "block-start",
                                    "inline-start",
                                    "block-end",
                                    "inline-end");
                        case "snapped" -> Set.of("none", "x", "y", "block", "inline", "both");
                        case "scrollable", "scrolled" ->
                            Set.of(
                                    "none",
                                    "top",
                                    "right",
                                    "bottom",
                                    "left",
                                    "block-start",
                                    "inline-start",
                                    "block-end",
                                    "inline-end",
                                    "x",
                                    "y",
                                    "block",
                                    "inline");
                        default -> throw invalid("Unknown scroll-state feature: " + name);
                    };
            if (colon.size() > 2
                    || 2 == colon.size() && !values.contains(colon.get(1).text().toLowerCase(Locale.ROOT))) {
                throw invalid("Invalid " + name + " value");
            }
            return;
        }
        if (2 == colon.size()) {
            String name = colon.get(0).text().toLowerCase(Locale.ROOT);
            if (name.startsWith("min-") || name.startsWith("max-")) {
                name = name.substring(4);
                if ("orientation".equals(name)) {
                    throw invalid("Orientation has no minimum/maximum form");
                }
            }
            value(name, colon.get(1).text(), type);
            return;
        }
        if (1 != colon.size()) {
            throw invalid("Invalid size-feature comparison");
        }
        var operands = new ArrayList<String>();
        var operators = new ArrayList<String>();
        int depth = 0;
        int first = 0;
        for (var token : CssLexical.tokens(text, 0, text.length())) {
            if (token.value().endsWith("(")) {
                depth++;
            } else if (")".equals(token.value())) {
                depth--;
            } else if (0 == depth && token.value().matches("[<>=]+")) {
                if (token.start() == first && !operators.isEmpty() && "=".equals(token.value())) {
                    int last = operators.size() - 1;
                    operators.set(last, operators.get(last) + "=");
                } else {
                    operands.add(text.substring(first, token.start()).trim());
                    operators.add(token.value());
                }
                first = token.end();
            }
        }
        operands.add(text.substring(first).trim());
        if (operators.isEmpty()) {
            sizeType(text.toLowerCase(Locale.ROOT));
        } else if (1 == operators.size()) {
            String left = operands.get(0).toLowerCase(Locale.ROOT);
            String right = operands.get(1).toLowerCase(Locale.ROOT);
            if ("orientation".equals(left) || "orientation".equals(right)) {
                throw invalid("Orientation requires a colon test");
            }
            if (sizeName(left)) {
                value(left, operands.get(1), type);
            } else {
                value(right, operands.get(0), type);
            }
        } else if (2 == operators.size()
                        && operators.get(0).matches("<=?")
                        && operators.get(1).matches("<=?")
                || 2 == operators.size()
                        && operators.get(0).matches(">=?")
                        && operators.get(1).matches(">=?")) {
            String name = operands.get(1).toLowerCase(Locale.ROOT);
            if ("orientation".equals(name)) {
                throw invalid("Orientation requires a colon test");
            }
            value(name, operands.get(0), type);
            value(name, operands.get(2), type);
        } else {
            throw invalid("Invalid size-feature range");
        }
        if (operators.stream()
                .anyMatch(operator -> !Set.of("<", "<=", ">", ">=", "=").contains(operator))) {
            throw invalid("Invalid comparison operator");
        }
    }

    private static boolean sizeName(String name) {
        return Set.of("width", "height", "inline-size", "block-size", "aspect-ratio", "orientation")
                .contains(name);
    }

    private static String sizeType(String name) {
        if (!sizeName(name)) {
            throw invalid("Unknown size feature: " + name);
        }
        return "aspect-ratio".equals(name) ? "ratio" : "orientation".equals(name) ? "orientation" : "length";
    }

    private static void value(String name, String value, String type) {
        String expected = sizeType(name);
        var holes = RuleSyntax.functions(value).stream()
                .filter(f -> "param".equals(f.name()))
                .toList();
        if (!holes.isEmpty()) {
            if (1 != holes.size()
                    || 0 != holes.get(0).start()
                    || value.length() != holes.get(0).end()
                    || !expected.equals(type)) {
                throw invalid("A complete " + expected + " threshold is required for " + name);
            }
        } else if ("orientation".equals(expected)) {
            if (!Set.of("portrait", "landscape").contains(value.toLowerCase(Locale.ROOT))) {
                throw invalid("Expected portrait or landscape");
            }
        } else {
            RuleValues.check(expected, value);
        }
    }

    private static boolean identifier(String text) {
        var tokens = CssLexical.tokens(text, 0, text.length());
        return 1 == tokens.size()
                && (ParserCSS30Constants.IDENT == tokens.get(0).kind()
                        || ParserCSS30Constants.DASHED_IDENT == tokens.get(0).kind());
    }

    private static IllegalArgumentException invalid(String message) {
        return new IllegalArgumentException(message);
    }
}
