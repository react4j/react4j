package react4j.css.utility.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLexical;
import react4j.css.internal.CssSyntax;

final class RuleLibrary {
    private static final String IDENTIFIER = "[a-zA-Z][a-zA-Z0-9_-]*";
    private static final Pattern PARAMETER =
            Pattern.compile("^\\(\\s*(" + IDENTIFIER + ")\\s*:\\s*(.*?)\\s*\\)$", Pattern.DOTALL);
    private static final Pattern SOURCE = Pattern.compile("(tokens|scale|bare|arbitrary)\\(\\s*([^()]+?)\\s*\\)");

    record Tokens(String type, Map<String, String> values, int offset) {
        Tokens {
            values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        }
    }

    record Argument(String kind, String name, String type) {}

    record Rule(
            String name,
            String prefix,
            String parameter,
            List<Argument> arguments,
            String type,
            String interval,
            List<RuleSyntax.Node> body,
            int offset,
            int rank) {
        Rule {
            arguments = List.copyOf(arguments);
            body = List.copyOf(body);
        }

        boolean family() {
            return !parameter.isEmpty();
        }
    }

    record Match(Rule rule, String value, String source) {}

    final UtilityLanguage.Source source;
    final Map<String, String> scales = new LinkedHashMap<>();
    final Map<String, Tokens> tokens = new LinkedHashMap<>();
    final List<Rule> utilities = new ArrayList<>();
    final List<Rule> variants = new ArrayList<>();
    final List<RuleSyntax.Node> support = new ArrayList<>();

    RuleLibrary(UtilityLanguage.Source source) {
        this.source = source;
        var nodes = RuleSyntax.nodes(source.text());
        if (nodes.isEmpty()
                || nodes.get(0).block()
                || !"@utility-rules".equals(RuleSyntax.clean(nodes.get(0).head()))) {
            throw error("U_SYNTAX", "Expected @utility-rules; at the beginning", 0);
        }
        for (var node : nodes.subList(1, nodes.size())) {
            String head = RuleSyntax.clean(node.head());
            if (head.startsWith("@scale ")) {
                var pieces = head.substring(7).trim().split("\\s+", 2);
                if (node.block() || 2 != pieces.length || !pieces[0].matches(IDENTIFIER)) {
                    throw error("U_SOURCE_TYPE", "Expected @scale name positive-scalar;", node.offset());
                }
                RuleValues.Scalar scalar;
                try {
                    scalar = RuleValues.scalar(pieces[1]);
                } catch (IllegalArgumentException e) {
                    throw error("U_SOURCE_TYPE", String.valueOf(e.getMessage()), node.offset());
                }
                if (!Set.of("number", "percentage", "length", "angle", "time").contains(scalar.type())
                        || scalar.number().signum() <= 0
                        || null != scales.putIfAbsent(pieces[0], pieces[1])) {
                    throw error(
                            "U_AMBIGUOUS_RULE",
                            "Scale must be positive and declared once: " + pieces[0],
                            node.offset());
                }
            } else if (head.startsWith("@tokens ")) {
                var pieces = head.substring(8).trim().split("\\s+", 2);
                if (!node.block() || 2 != pieces.length || !pieces[0].matches(IDENTIFIER)) {
                    throw error("U_SOURCE_TYPE", "Expected @tokens name <type> { ... }", node.offset());
                }
                var type = RuleValues.type(pieces[1]);
                var values = new LinkedHashMap<String, String>();
                for (var token : node.children()) {
                    var parts = RuleSyntax.split(token.head(), ':');
                    if (token.block()
                            || 2 != parts.size()
                            || !parts.get(0).text().matches("[a-zA-Z0-9][a-zA-Z0-9_-]*")) {
                        throw error("U_VALUE_TYPE", "Expected token-key: value;", token.offset());
                    }
                    String value = parts.get(1).text();
                    checkValue(type, value, token.offset());
                    if (null != values.putIfAbsent(parts.get(0).text(), value)) {
                        throw error(
                                "U_AMBIGUOUS_RULE",
                                "Duplicate token " + parts.get(0).text(),
                                token.offset());
                    }
                }
                if (null != tokens.putIfAbsent(pieces[0], new Tokens(type, values, node.offset()))) {
                    throw error("U_AMBIGUOUS_RULE", "Duplicate token group " + pieces[0], node.offset());
                }
            } else if (head.startsWith("@utility ") || head.startsWith("@variant ")) {
                // Resolve heads after every scale/token declaration has been collected.
            } else if (head.startsWith("@keyframes ")
                    || head.startsWith("@property ")
                    || "@font-face".equals(head)
                    || ":root".equals(head)) {
                if (!node.block()) {
                    throw error("U_SYNTAX", "Supporting CSS requires a body", node.offset());
                }
                if (":root".equals(head)
                        && node.children().stream()
                                .anyMatch(
                                        child -> child.block() || !child.head().startsWith("--"))) {
                    throw error("U_SYNTAX", "Only custom-property initialization is allowed at :root", node.offset());
                }
                checkSupport(node);
                support.add(node);
            } else {
                throw error("U_SYNTAX", "Unsupported native definition " + head, node.offset());
            }
        }
        for (var node : nodes.subList(1, nodes.size())) {
            String head = RuleSyntax.clean(node.head());
            if (head.startsWith("@utility ") || head.startsWith("@variant ")) {
                boolean variant = head.startsWith("@variant ");
                var rules = variant ? variants : utilities;
                var rule = rule(head.substring(9).trim(), node, rules.size());
                int slots = template(rule.body(), rule, variant);
                if (variant && 1 != slots || !variant && 0 != slots) {
                    throw error(
                            "U_SLOT", "A variant requires exactly one @slot; a utility requires none", node.offset());
                }
                rules.add(rule);
            }
        }
        collisions(utilities);
        collisions(variants);
    }

    private static void checkSupport(RuleSyntax.Node node) {
        RuleSyntax.substitute(node.head(), "", "");
        node.children().forEach(RuleLibrary::checkSupport);
    }

    private Rule rule(String head, RuleSyntax.Node node, int rank) {
        if (!node.block()) {
            throw error("U_SYNTAX", "Rule requires a body", node.offset());
        }
        String name;
        String tail;
        boolean quoted = head.startsWith("\"") || head.startsWith("'");
        if (quoted) {
            int end = head.indexOf(head.charAt(0), 1);
            if (end < 0) {
                throw error("U_SYNTAX", "Unterminated head", node.offset());
            }
            name = head.substring(1, end);
            tail = head.substring(end + 1).trim();
        } else {
            int end = head.indexOf(' ');
            name = end < 0 ? head : head.substring(0, end);
            tail = end < 0 ? "" : head.substring(end).trim();
            if (!name.matches("-?[a-zA-Z_][a-zA-Z0-9_-]*")) {
                throw error("U_SYNTAX", "Quote this utility or variant name", node.offset());
            }
        }
        int capture = name.indexOf('{');
        String prefix = capture < 0 ? name : name.substring(0, capture);
        if (prefix.isEmpty() || prefix.matches(".*[\\s:\\[\\]\\\"'\\\\{}!].*") || name.indexOf('!') >= 0) {
            throw error("U_SYNTAX", "Invalid rule name " + name, node.offset());
        }
        if (capture < 0) {
            if (!tail.isEmpty()) {
                throw error("U_PARAMETER", "Literal rules have no parameter", node.offset());
            }
            return new Rule(name, prefix, "", List.of(), "", "", node.children(), node.offset(), rank);
        }
        if (!quoted || !name.endsWith("}") || !(prefix.endsWith("-") || prefix.endsWith("/"))) {
            throw error("U_PARAMETER", "A pattern ends with one {parameter} after - or /", node.offset());
        }
        String parameter = name.substring(capture + 1, name.length() - 1);
        var match = PARAMETER.matcher(tail);
        if (!parameter.matches(IDENTIFIER) || !match.matches() || !parameter.equals(match.group(1))) {
            throw error("U_PARAMETER", "Pattern and parameter declaration must agree", node.offset());
        }
        String sources = match.group(2);
        String interval = "";
        var within = Pattern.compile("\\s+within\\s+").matcher(sources);
        if (within.find()) {
            interval = sources.substring(within.end()).trim();
            sources = sources.substring(0, within.start());
        }
        var arguments = new ArrayList<Argument>();
        String type = "";
        for (var part : RuleSyntax.split(sources, '|')) {
            var parsed = SOURCE.matcher(part.text());
            if (!parsed.matches()) {
                throw error("U_SOURCE_TYPE", "Expected tokens, scale, bare or arbitrary source", node.offset());
            }
            String kind = parsed.group(1);
            String argument = parsed.group(2);
            String argumentType;
            if ("tokens".equals(kind)) {
                var group = tokens.get(argument);
                if (null == group) {
                    throw error("U_SOURCE_TYPE", "Unknown token group " + argument, node.offset());
                }
                argumentType = group.type();
            } else if ("scale".equals(kind)) {
                var step = scales.get(argument);
                if (null == step) {
                    throw error("U_SOURCE_TYPE", "Unknown scale " + argument, node.offset());
                }
                argumentType = RuleValues.scalar(step).type();
            } else {
                argumentType = RuleValues.type(argument);
                if ("bare".equals(kind)
                        && !Set.of("integer", "number", "percentage", "length", "angle", "time", "ratio")
                                .contains(argumentType)) {
                    throw error("U_SOURCE_TYPE", "This type requires brackets", node.offset());
                }
            }
            type = type.isEmpty() ? argumentType : RuleValues.common(type, argumentType);
            arguments.add(new Argument(kind, argument, argumentType));
        }
        var rule = new Rule(name, prefix, parameter, arguments, type, interval, node.children(), node.offset(), rank);
        disjoint(rule);
        try {
            RuleBounds.validate(rule.type(), rule.interval());
            for (var argument : rule.arguments()) {
                if ("tokens".equals(argument.kind())) {
                    for (var value : Objects.requireNonNull(tokens.get(argument.name()))
                            .values()
                            .values()) {
                        RuleBounds.check(rule.type(), rule.interval(), value);
                    }
                }
            }
        } catch (RuleSyntax.Syntax e) {
            throw error(e.code, String.valueOf(e.getMessage()), rule.offset());
        }
        return rule;
    }

    private int template(List<RuleSyntax.Node> nodes, Rule rule, boolean variant) {
        int slots = 0;
        for (var node : nodes) {
            String head = RuleSyntax.clean(node.head());
            String substituted;
            try {
                substituted = RuleSyntax.substitute(head, rule.parameter(), sample(rule.type()));
            } catch (RuleSyntax.Syntax e) {
                throw error(e.code, String.valueOf(e.getMessage()), node.offset() + e.offset);
            }
            if (node.block()) {
                if (head.startsWith("@media ") || head.startsWith("@supports ")) {
                    if (!head.equals(substituted)) {
                        throw error("U_PARAMETER", "Media/supports conditions must be static", node.offset());
                    }
                } else if (head.startsWith("@container ")) {
                    RuleConditions.check(head.substring(11).trim(), rule.type(), node.offset());
                } else if (head.startsWith("@starting-style")) {
                    if (!"@starting-style".equals(head)) {
                        throw error("U_STARTING_STYLE", "@starting-style takes no condition", node.offset());
                    }
                } else {
                    if (!head.equals(substituted) && (!"an+b".equals(rule.type()) || !RuleSyntax.selectorHoles(head))) {
                        throw error(
                                "U_PARAMETER",
                                "Selector holes are allowed only in :nth-child for <an+b>",
                                node.offset());
                    }
                    selector(substituted, node.offset());
                }
                slots += template(node.children(), rule, variant);
            } else if ("@slot".equals(head)) {
                slots++;
            } else if (variant) {
                throw error("U_SLOT", "A variant may contain only wrappers and @slot", node.offset());
            } else {
                var parts = RuleSyntax.split(head, ':');
                if (parts.size() < 2 || !parts.get(0).text().matches("(?:--)?[a-zA-Z_-][a-zA-Z0-9_-]*")) {
                    throw error("U_PARAMETER", "Expected a static property name and value", node.offset());
                }
                RuleAnchors.template(
                        parts.get(0).text().toLowerCase(Locale.ROOT),
                        head.substring(head.indexOf(':') + 1),
                        node.offset());
                if (RuleSyntax.split(head, '!').size() > 1) {
                    throw error("U_IMPORTANT", "Important utility declarations are unsupported", node.offset());
                }
                if ("an+b".equals(rule.type()) && !head.equals(substituted)) {
                    throw error("U_PARAMETER", "<an+b> is a selector-only type", node.offset());
                }
            }
        }
        return slots;
    }

    static void selector(String text, int offset) {
        int ampersands = 0;
        int first = -1;
        for (var token : CssLexical.tokens(text, 0, text.length())) {
            if ("&".equals(token.value())) {
                ampersands++;
                first = token.start();
            }
        }
        if (1 != ampersands
                || first < 0
                || text.substring(0, first).matches(".*[\\s>+~(,].*")
                || 1 != RuleSyntax.split(text, ',').size()) {
            throw error("U_SELECTOR_GUARD", "Selector requires one positive & in its first compound", offset);
        }
        // Parsing the complete selector catches invalid combinators and function syntax.
        new CssSyntax(RuleSyntax.guard(text, ".guard") + "{x:0;}", 0).parse(CssSyntax.Mode.UTILITY);
    }

    private void disjoint(Rule rule) {
        var seen = new HashSet<String>();
        boolean scale = false;
        boolean bare = false;
        var tokenKeys = new HashSet<String>();
        for (var argument : rule.arguments()) {
            if (!"tokens".equals(argument.kind()) && !seen.add(argument.kind())) {
                throw error("U_AMBIGUOUS_RULE", "Repeated argument source " + argument.kind(), rule.offset());
            }
            if ("scale".equals(argument.kind())) {
                scale = true;
            }
            if ("bare".equals(argument.kind())) {
                bare = true;
            }
            if ("tokens".equals(argument.kind())) {
                for (var key : Objects.requireNonNull(tokens.get(argument.name()))
                        .values()
                        .keySet()) {
                    if (!tokenKeys.add(key)) {
                        throw error("U_AMBIGUOUS_RULE", "Overlapping token " + key, rule.offset());
                    }
                }
            }
        }
        if (scale
                && bare
                && rule.arguments().stream()
                        .anyMatch(a -> "bare".equals(a.kind())
                                && Set.of("integer", "number", "ratio").contains(a.type()))) {
            throw error("U_AMBIGUOUS_RULE", "Scale and bare arguments overlap", rule.offset());
        }
        for (var key : tokenKeys) {
            if (scale && key.matches("[0-9]+(?:\\.[0-9]+)?")
                    || bare && rule.arguments().stream().anyMatch(a -> "bare".equals(a.kind()) && accepts(a, key))) {
                throw error("U_AMBIGUOUS_RULE", "Token overlaps numeric source: " + key, rule.offset());
            }
        }
    }

    private void collisions(List<Rule> rules) {
        var literals = new HashSet<String>();
        var prefixes = new HashSet<String>();
        for (var rule : rules) {
            if (!(rule.family() ? prefixes : literals).add(rule.prefix())) {
                throw error("U_AMBIGUOUS_RULE", "Duplicate rule " + rule.name(), rule.offset());
            }
        }
        for (var literal : rules) {
            if (!literal.family()) {
                var family = longest(rules, literal.name());
                if (null != family
                        && resolve(
                                        family,
                                        literal.name().substring(family.prefix().length()))
                                != null) {
                    throw new RuleSyntax.Syntax(
                            "U_AMBIGUOUS_RULE",
                            "Literal overlaps family: " + literal.name(),
                            literal.offset(),
                            List.of(family.offset()));
                }
            } else {
                for (var argument : literal.arguments()) {
                    if ("tokens".equals(argument.kind())) {
                        for (var key : Objects.requireNonNull(tokens.get(argument.name()))
                                .values()
                                .keySet()) {
                            String candidate = literal.prefix() + key;
                            if (longest(rules, candidate) != literal) {
                                throw error(
                                        "U_AMBIGUOUS_RULE",
                                        "Token hidden by longer prefix: " + candidate,
                                        literal.offset());
                            }
                        }
                    }
                }
            }
        }
    }

    Match match(List<Rule> rules, String candidate, boolean variant) {
        for (var rule : rules) {
            if (!rule.family() && candidate.equals(rule.name())) {
                return new Match(rule, "", "literal");
            }
        }
        var family = longest(rules, candidate);
        if (null == family) {
            throw error(
                    variant ? "U_UNKNOWN_VARIANT" : "U_UNKNOWN_UTILITY",
                    "Unknown " + (variant ? "variant " : "utility ") + candidate,
                    0);
        }
        var resolved = resolve(family, candidate.substring(family.prefix().length()));
        if (null == resolved) {
            throw error(
                    "U_ARGUMENT", "No argument source accepts " + candidate + " for " + family.name(), family.offset());
        }
        checkValue(family.type(), resolved.value(), family.offset());
        if ("an+b".equals(family.type())) {
            resolved = new Match(
                    family, resolved.value().toLowerCase(Locale.ROOT).replaceAll("\\s+", ""), resolved.source());
        }
        RuleBounds.check(family.type(), family.interval(), resolved.value());
        return resolved;
    }

    private static @Nullable Rule longest(List<Rule> rules, String candidate) {
        Rule result = null;
        for (var rule : rules) {
            if (rule.family()
                    && candidate.startsWith(rule.prefix())
                    && (null == result
                            || rule.prefix().length() > result.prefix().length())) {
                result = rule;
            }
        }
        return result;
    }

    private @Nullable Match resolve(Rule rule, String argument) {
        for (var source : rule.arguments()) {
            if ("tokens".equals(source.kind())) {
                var value = Objects.requireNonNull(tokens.get(source.name()))
                        .values()
                        .get(argument);
                if (null != value) {
                    return new Match(rule, value, "token:" + source.name() + ":" + argument);
                }
            } else if ("scale".equals(source.kind()) && argument.matches("[0-9]+(?:\\.[0-9]+)?")) {
                return new Match(
                        rule,
                        RuleValues.multiply(Objects.requireNonNull(scales.get(source.name())), argument),
                        "scale:" + source.name());
            } else if ("bare".equals(source.kind()) && accepts(source, argument)) {
                return new Match(rule, argument, "bare");
            } else if ("arbitrary".equals(source.kind()) && argument.startsWith("[") && argument.endsWith("]")) {
                return new Match(rule, RuleSyntax.decode(argument.substring(1, argument.length() - 1)), "arbitrary");
            }
        }
        return null;
    }

    private static boolean accepts(Argument source, String argument) {
        if (argument.contains("(") || argument.contains("[")) {
            return false;
        }
        try {
            RuleValues.check(source.type(), argument);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void checkValue(String type, String value, int offset) {
        try {
            RuleValues.check(type, value);
        } catch (RuleSyntax.Syntax e) {
            throw error(e.code, String.valueOf(e.getMessage()), offset);
        } catch (IllegalArgumentException e) {
            throw error("U_VALUE_TYPE", String.valueOf(e.getMessage()), offset);
        }
    }

    static String sample(String type) {
        return switch (type) {
            case "color" -> "red";
            case "length", "length-percentage" -> "1px";
            case "percentage" -> "1%";
            case "angle" -> "1deg";
            case "time" -> "1ms";
            case "string" -> "\"sample\"";
            case "image" -> "linear-gradient(red,blue)";
            case "position" -> "center";
            case "box-shadow" -> "0 0 1px red";
            case "grid-tracks" -> "1fr";
            default -> "1";
        };
    }

    static RuleSyntax.Syntax error(String code, String message, int offset) {
        return new RuleSyntax.Syntax(code, message, offset);
    }
}
