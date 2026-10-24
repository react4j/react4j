package react4j.css.utility.language;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssIdentity;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.CssTokens;
import react4j.css.internal.CssUtilityBinding;
import react4j.css.internal.SourceMap;

/** Pure JVM rule preparation, checking and stateless expansion. No operation performs I/O. */
public final class UtilityLanguage {
    private static final int VERSION = 1;

    private UtilityLanguage() {}

    public record Source(String name, String text, String sourceMap) {
        public Source(String name, String text) {
            this(name, text, "");
        }
    }

    public record Origin(String source, int start, int end, int line, int column) {}

    public record Diagnostic(
            String code, String message, Origin origin, List<Origin> related, Map<String, String> facts) {
        public Diagnostic {
            related = List.copyOf(related);
            facts = Map.copyOf(facts);
        }

        public String render() {
            var text = new StringBuilder(origin.source() + ":" + (origin.line() + 1) + ":" + (origin.column() + 1)
                    + ": " + code + ": " + message);
            if (facts.containsKey("candidate")) {
                text.append("\n  Candidate: ").append(facts.get("candidate"));
            }
            for (var entry : facts.entrySet().stream()
                    .filter(entry -> Set.of(
                                    "rule",
                                    "expectedType",
                                    "sources",
                                    "allowedTokens",
                                    "suggestions",
                                    "suggestedTemplate",
                                    "payload")
                            .contains(entry.getKey()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList()) {
                text.append("\n  ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
            for (var definition : related) {
                text.append("\n  Defined at ")
                        .append(definition.source())
                        .append(":")
                        .append(definition.line() + 1)
                        .append(":")
                        .append(definition.column() + 1);
            }
            return text.toString();
        }
    }

    public static final class Failure extends IllegalArgumentException {
        private final Diagnostic diagnostic;

        private Failure(Diagnostic diagnostic) {
            super(diagnostic.render());
            this.diagnostic = diagnostic;
        }

        public Diagnostic diagnostic() {
            return diagnostic;
        }
    }

    public record Bindings(
            Set<String> externalClasses,
            CssUtilityBinding.Context context,
            Set<String> exportedIds,
            Set<String> exportedAnimations) {
        public Bindings(Set<String> externalClasses) {
            this(externalClasses, CssUtilityBinding.Context.empty(), Set.of(), Set.of());
        }

        public Bindings(Set<String> externalClasses, CssUtilityBinding.Context context) {
            this(externalClasses, context, Set.of(), Set.of());
        }

        public Bindings {
            externalClasses = Set.copyOf(externalClasses);
            exportedIds = Set.copyOf(exportedIds);
            exportedAnimations = Set.copyOf(exportedAnimations);
        }
    }

    public record Description(
            String name, String parameterType, List<String> sources, String interval, String template, Origin origin) {
        public Description {
            sources = List.copyOf(sources);
        }
    }

    public record TokenGroup(String name, String type, Map<String, String> values, Map<String, Origin> origins) {
        public TokenGroup {
            values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
            origins = Map.copyOf(origins);
        }
    }

    public record BoundCheck(String interval, String value, String status) {}

    public record Trace(
            String rule,
            String argument,
            String resolvedValue,
            Origin definition,
            List<Origin> related,
            List<BoundCheck> checks) {
        public Trace {
            related = List.copyOf(related);
            checks = List.copyOf(checks);
        }
    }

    public record Expansion(
            String candidate,
            String className,
            String orderKey,
            String css,
            String sourceMap,
            List<Trace> trace,
            CssResource metadata) {
        public Expansion {
            trace = List.copyOf(trace);
        }
    }

    public record Batch(List<Expansion> expansions, List<Diagnostic> diagnostics) {
        public Batch {
            expansions = List.copyOf(expansions);
            diagnostics = List.copyOf(diagnostics);
        }
    }

    public static final class Prepared {
        private final RuleLibrary rules;

        private Prepared(RuleLibrary rules) {
            this.rules = rules;
        }

        public Source source() {
            return rules.source;
        }

        public String json() {
            return envelope("prepared", rules.source).toString();
        }

        public Map<String, String> scales() {
            return Collections.unmodifiableMap(rules.scales);
        }

        public List<TokenGroup> tokens() {
            return rules.tokens.entrySet().stream()
                    .map(entry -> {
                        var origins = new LinkedHashMap<String, Origin>();
                        entry.getValue()
                                .offsets()
                                .forEach((key, offset) -> origins.put(key, origin(rules.source, offset)));
                        return new TokenGroup(
                                entry.getKey(),
                                entry.getValue().type(),
                                entry.getValue().values(),
                                origins);
                    })
                    .toList();
        }

        public List<Description> utilities() {
            return descriptions(rules, rules.utilities);
        }

        public List<Description> variants() {
            return descriptions(rules, rules.variants);
        }
    }

    public static final class Checked {
        private final Prepared prepared;
        private final String owner;
        private final Bindings bindings;
        private final CssUtilityBinding.Bound support;

        private Checked(Prepared prepared, String owner, Bindings bindings, CssUtilityBinding.Bound support) {
            this.prepared = prepared;
            this.owner = owner;
            this.bindings = bindings;
            this.support = support;
        }

        public CssUtilityBinding.Bound support() {
            return support;
        }

        public Bindings bindings() {
            return bindings;
        }

        public Prepared prepared() {
            return prepared;
        }

        public String owner() {
            return owner;
        }

        public String fingerprint() {
            var value = JsonParser.parseString(json()).getAsJsonObject();
            value.remove("sourceMap");
            return digest(value.toString());
        }

        public String json() {
            var json = envelope("checked", prepared.source());
            json.addProperty("owner", owner);
            json.add("bindings", JsonParser.parseString(bindings.context().json()));
            json.add("exportedIds", array(bindings.exportedIds()));
            json.add("exportedAnimations", array(bindings.exportedAnimations()));
            var classes = new JsonArray();
            bindings.externalClasses().stream().sorted().forEach(classes::add);
            json.add("externalClasses", classes);
            return json.toString();
        }
    }

    public static boolean nativeSource(String text) {
        int offset = 0;
        while (offset < text.length()) {
            if (Character.isWhitespace(text.charAt(offset))) {
                offset++;
            } else if (text.startsWith("/*", offset)) {
                int end = text.indexOf("*/", offset + 2);
                if (end < 0) {
                    return false;
                }
                offset = end + 2;
            } else {
                break;
            }
        }
        return text.substring(offset).matches("(?s)^@utility-rules(?:\\s|/\\*.*?\\*/)*;.*");
    }

    public static Prepared prepare(Source source) {
        try {
            var prepared = new Prepared(new RuleLibrary(source));
            validateTemplates(prepared);
            return prepared;
        } catch (RuleSyntax.Syntax e) {
            throw new Failure(new Diagnostic(
                    e.code,
                    String.valueOf(e.getMessage()),
                    origin(source, Math.min(e.offset, source.text().length())),
                    e.related.stream().map(offset -> origin(source, offset)).toList(),
                    Map.of()));
        } catch (CssSyntax.InvalidCss e) {
            String message = String.valueOf(e.getMessage());
            String code = message.startsWith("U_") && message.contains(":")
                    ? message.substring(0, message.indexOf(':'))
                    : "U_SYNTAX";
            throw failure(source, code, message, e.offset());
        }
    }

    public static Prepared readPrepared(String json) {
        try {
            return prepare(readSource(descriptor(json, "prepared")));
        } catch (Failure e) {
            throw e;
        } catch (RuntimeException e) {
            throw invalidDescriptor(e);
        }
    }

    public static Checked bind(Prepared prepared, String owner, Bindings bindings) {
        if (owner.isBlank()) {
            throw new IllegalArgumentException("Provider owner is required");
        }
        var rules = prepared.rules;
        var output = bindingOutput(prepared);
        try {
            RuleAnchors.check(output.css(), bindings.context().externalAnchors());
            int boundary = supportCss(prepared).length();
            var result = CssUtilityBinding.compile(
                    output,
                    CssIdentity.root(owner),
                    Set.of("probe"),
                    bindings.exportedIds(),
                    bindings.exportedAnimations(),
                    probeContext(prepared, bindings.context()),
                    true,
                    boundary);
            var resolved = CssUtilityBinding.resolve(owner, result, Map.of(), bindings.context());
            for (var group : rules.tokens.values()) {
                for (var value : group.values().values()) {
                    CssUtilityBinding.checkValue(value, resolved.metadata(), bindings.context());
                }
            }
            var checked = new Checked(prepared, owner, bindings, resolved.project(0, resolved.boundary()));
            for (var rule : rules.utilities) {
                templateHooks(checked, substitute(rule.body(), rule.parameter(), RuleLibrary.sample(rule.type())));
                hooks(checked, probe(rules, rule, false), "probe");
            }
            for (var rule : rules.variants) {
                templateHooks(checked, substitute(rule.body(), rule.parameter(), RuleLibrary.sample(rule.type())));
                hooks(checked, probe(rules, rule, true), "probe");
            }
            return checked;
        } catch (RuleSyntax.Syntax e) {
            throw failure(rules.source, e.code, String.valueOf(e.getMessage()), e.offset);
        } catch (CssSyntax.InvalidCss e) {
            throw mappedFailure(rules.source, output, e);
        } catch (IllegalArgumentException e) {
            throw bindingFailure(rules.source, e);
        }
    }

    public static Checked readChecked(String json) {
        try {
            return checkedDescriptor(json);
        } catch (Failure e) {
            throw e;
        } catch (RuntimeException e) {
            throw invalidDescriptor(e);
        }
    }

    private static Checked checkedDescriptor(String json) {
        var value = descriptor(json, "checked");
        var classes = new HashSet<String>();
        value.getAsJsonArray("externalClasses").forEach(item -> classes.add(item.getAsString()));
        return bind(
                prepare(readSource(value)),
                value.get("owner").getAsString(),
                new Bindings(
                        classes,
                        CssUtilityBinding.Context.read(value.get("bindings").toString()),
                        strings(value, "exportedIds"),
                        strings(value, "exportedAnimations")));
    }

    public static List<String> candidates(String text) {
        var result = List.of(text.split("[ \\t\\n\\r\\f]+", -1));
        return result.stream().filter(candidate -> !candidate.isEmpty()).toList();
    }

    public static Batch expand(Checked checked, List<String> candidates) {
        var output = new ArrayList<Expansion>();
        var problems = new ArrayList<Diagnostic>();
        var seen = new HashSet<String>();
        for (var candidate : candidates) {
            try {
                if (candidate.isEmpty() || candidate.matches(".*[ \\t\\n\\r\\f].*") || !seen.add(candidate)) {
                    throw RuleLibrary.error(
                            "U_CANDIDATE_SYNTAX",
                            "Empty, duplicate or whitespace-containing candidate " + candidate,
                            0);
                }
                if (RuleSyntax.split(candidate, '!').size() > 1) {
                    throw RuleLibrary.error(
                            "U_IMPORTANT", "Important utility modifiers are unsupported", candidate.indexOf('!'));
                }
                output.add(expansion(checked, candidate));
            } catch (Failure e) {
                problems.add(e.diagnostic());
            } catch (RuleSyntax.Syntax e) {
                var related = origin(
                        checked.prepared.source(),
                        Math.min(e.offset, checked.prepared.source().text().length()));
                problems.add(new Diagnostic(
                        e.code,
                        String.valueOf(e.getMessage()),
                        origin(
                                new Source("<candidate>", candidate),
                                Math.max(0, Math.min(e.offset, candidate.length()))),
                        e.offset > candidate.length() ? List.of(related) : List.of(),
                        Map.of("candidate", candidate, "provider", checked.owner)));
            } catch (CssSyntax.InvalidCss e) {
                problems.add(new Diagnostic(
                        "U_VALUE_TYPE",
                        String.valueOf(e.getMessage()),
                        origin(new Source("<candidate>", candidate), 0),
                        List.of(),
                        Map.of("candidate", candidate)));
            }
        }
        output.sort(Comparator.comparing(Expansion::orderKey));
        return new Batch(output, problems);
    }

    /** All templates, with representative typed holes, for provider-wide CSS binding checks. */
    public static String validationCss(Prepared prepared) {
        return validationOutput(prepared).css();
    }

    private static SourceMap.Writer validationOutput(Prepared prepared) {
        return prototypes(prepared, false);
    }

    private static String probeProperty(Prepared prepared) {
        return "react4j-utility-" + digest(prepared.source().text());
    }

    private static CssUtilityBinding.Context probeContext(Prepared prepared, CssUtilityBinding.Context context) {
        var external = new HashSet<>(context.externalProperties());
        external.add(probeProperty(prepared));
        return new CssUtilityBinding.Context(
                external,
                context.externalAnimations(),
                context.externalFonts(),
                context.externalContainers(),
                context.externalAnchors(),
                context.imports(),
                context.properties(),
                context.ids(),
                context.assets(),
                context.fonts(),
                context.includes());
    }

    private static SourceMap.Writer bindingOutput(Prepared prepared) {
        return prototypes(prepared, true);
    }

    private static SourceMap.Writer prototypes(Prepared prepared, boolean binding) {
        var writer = new SourceMap.Writer();
        var rules = prepared.rules;
        raw(rules, rules.support, writer);
        for (boolean variant : List.of(false, true)) {
            for (var rule : variant ? rules.variants : rules.utilities) {
                var body = binding
                        ? browserHoles(prepared, rule, rule.body())
                        : substitute(rule.body(), rule.parameter(), RuleLibrary.sample(rule.type()));
                probeBody(rules, rule, body, variant, writer);
                for (var argument : rule.arguments()) {
                    if ("tokens".equals(argument.kind())) {
                        var group = Objects.requireNonNull(rules.tokens.get(argument.name()));
                        for (var token : group.values().entrySet()) {
                            String resolved = "an+b".equals(rule.type())
                                    ? token.getValue().toLowerCase(Locale.ROOT).replaceAll("\\s+", "")
                                    : token.getValue();
                            var resolvedBody = substitute(rule.body(), rule.parameter(), resolved);
                            probeBody(
                                    rules,
                                    rule,
                                    at(
                                            resolvedBody,
                                            Objects.requireNonNull(
                                                    group.offsets().get(token.getKey()))),
                                    variant,
                                    writer);
                        }
                    }
                }
            }
        }
        return writer;
    }

    private static List<RuleSyntax.Node> at(List<RuleSyntax.Node> nodes, int offset) {
        return nodes.stream()
                .map(node -> new RuleSyntax.Node(node.head(), at(node.children(), offset), offset, node.block()))
                .toList();
    }

    private static List<RuleSyntax.Node> browserHoles(
            Prepared prepared, RuleLibrary.Rule rule, List<RuleSyntax.Node> nodes) {
        var result = new ArrayList<RuleSyntax.Node>();
        for (var node : nodes) {
            String value = "an+b".equals(rule.type()) ? "1" : "var(--" + probeProperty(prepared) + ")";
            if ("string".equals(rule.type())
                    && RuleSyntax.functions(node.head()).stream()
                            .anyMatch(function -> "url".equals(function.name())
                                    && RuleSyntax.functions(function.arguments()).stream()
                                            .anyMatch(hole -> "param".equals(hole.name())))) {
                value = "'https://react4j.invalid/utility-probe'";
            }
            result.add(new RuleSyntax.Node(
                    RuleSyntax.substitute(node.head(), rule.parameter(), value),
                    browserHoles(prepared, rule, node.children()),
                    node.offset(),
                    node.block()));
        }
        return List.copyOf(result);
    }

    private static void probeBody(
            RuleLibrary rules,
            RuleLibrary.Rule rule,
            List<RuleSyntax.Node> body,
            boolean variant,
            SourceMap.Writer writer) {
        render(
                rules,
                variant ? slot(body, List.of(new RuleSyntax.Node("x: 0", List.of(), rule.offset(), false))) : body,
                ".probe",
                writer);
    }

    public static String supportCss(Prepared prepared) {
        var writer = new SourceMap.Writer();
        raw(prepared.rules, prepared.rules.support, writer);
        return writer.css();
    }

    private static Expansion expansion(Checked checked, String candidate) {
        var rules = checked.prepared.rules;
        var pieces = RuleSyntax.split(candidate, ':');
        if (pieces.stream().anyMatch(part -> part.text().isEmpty())) {
            throw RuleLibrary.error("U_CANDIDATE_SYNTAX", "Empty candidate segment", 0);
        }
        var utility = match(checked, candidate, pieces.get(pieces.size() - 1), false);
        var body = resolvedBody(checked, candidate, pieces.get(pieces.size() - 1), utility, false);
        var trace = new ArrayList<Trace>();
        trace.add(trace(rules, utility));
        var ranks = new ArrayList<String>();
        for (int i = pieces.size() - 2; i >= 0; i--) {
            String piece = pieces.get(i).text();
            if (piece.startsWith("[") && piece.endsWith("]")) {
                String selector = RuleSyntax.decode(piece.substring(1, piece.length() - 1));
                try {
                    RuleLibrary.selector(selector, pieces.get(i).offset());
                    hooks(checked, RuleSyntax.guard(selector, ":root") + " {x:0;}", "");
                } catch (RuleSyntax.Syntax e) {
                    throw candidateFailure(checked, candidate, pieces.get(i), e, null, true);
                }
                body = List.of(
                        new RuleSyntax.Node(selector, body, utility.rule().offset(), true));
                ranks.add(0, rank(rules.variants.size()) + ":" + spellingKey(selector) + "\u0000");
            } else {
                var variant = match(checked, candidate, pieces.get(i), true);
                body = slot(resolvedBody(checked, candidate, pieces.get(i), variant, true), body);
                trace.add(0, trace(rules, variant));
                ranks.add(0, rank(variant.rule().rank()));
            }
        }
        String className = CssIdentity.root(checked.owner) + "--class-" + CssIdentity.encode(candidate);
        var writer = bindingOutput(checked.prepared);
        int boundary = writer.css().length();
        render(rules, body, "." + className, writer);
        hooks(checked, writer.css().substring(boundary), className);
        CssUtilityBinding.Bound fragment;
        try {
            RuleAnchors.check(writer.css(), checked.bindings.context().externalAnchors());
            var compiled = CssUtilityBinding.compile(
                    writer,
                    CssIdentity.root(checked.owner),
                    Set.of("probe", className),
                    checked.bindings.exportedIds(),
                    checked.bindings.exportedAnimations(),
                    probeContext(checked.prepared, checked.bindings.context()),
                    true,
                    boundary);
            var resolved = CssUtilityBinding.resolve(
                    checked.owner, compiled, Map.of(candidate, className), checked.bindings.context());
            fragment = resolved.project(resolved.boundary(), resolved.css().length());
        } catch (IllegalArgumentException e) {
            var failure = bindingFailure(rules.source, e);
            throw candidateFailure(
                    checked,
                    candidate,
                    pieces.get(pieces.size() - 1),
                    RuleLibrary.error(
                            failure.diagnostic().code(),
                            failure.diagnostic().message(),
                            utility.rule().offset()),
                    utility.rule(),
                    false);
        }
        String order = rank(pieces.size() - 1)
                + String.join("", ranks)
                + rank(utility.rule().rank())
                + spellingKey(candidate);
        return new Expansion(
                candidate,
                className,
                order,
                fragment.css(),
                fragment.sourceMap(),
                trace,
                fragment.metadata().withFingerprint(checked.fingerprint()));
    }

    private static List<RuleSyntax.Node> resolvedBody(
            Checked checked, String candidate, RuleSyntax.Part part, RuleLibrary.Match match, boolean variant) {
        var body = substitute(match.rule().body(), match.rule().parameter(), match.value());
        try {
            conditions(body);
        } catch (RuleSyntax.Syntax e) {
            throw candidateFailure(checked, candidate, part, e, match.rule(), variant);
        }
        return body;
    }

    private static void conditions(List<RuleSyntax.Node> nodes) {
        for (var node : nodes) {
            if (node.head().startsWith("@container ")) {
                RuleConditions.check(node.head().substring(11).trim(), "", node.offset());
            }
            conditions(node.children());
        }
    }

    private static RuleLibrary.Match match(Checked checked, String candidate, RuleSyntax.Part part, boolean variant) {
        var rules = checked.prepared.rules;
        var definitions = variant ? rules.variants : rules.utilities;
        try {
            return rules.match(definitions, part.text(), variant);
        } catch (RuleSyntax.Syntax e) {
            throw candidateFailure(checked, candidate, part, e, RuleLibrary.longest(definitions, part.text()), variant);
        }
    }

    private static Failure candidateFailure(
            Checked checked,
            String candidate,
            RuleSyntax.Part part,
            RuleSyntax.Syntax error,
            RuleLibrary.@Nullable Rule rule,
            boolean variant) {
        var facts = new LinkedHashMap<String, String>();
        facts.put("candidate", candidate);
        facts.put("provider", checked.owner);
        facts.put("requestOffset", String.valueOf(part.offset()));
        int first = part.offset();
        int end = Math.min(candidate.length(), first + part.text().length());
        var related = new ArrayList<Origin>();
        if (null != rule) {
            facts.put("rule", rule.name());
            facts.put("expectedType", "<" + rule.type() + ">");
            facts.put(
                    "sources",
                    rule.arguments().stream()
                            .map(argument -> argument.kind() + "(" + argument.name() + ")")
                            .collect(Collectors.joining(" | ")));
            facts.put("interval", rule.interval());
            related.add(origin(checked.prepared.source(), rule.offset()));
            first += rule.prefix().length();
            String payload = part.text().substring(rule.prefix().length());
            if (payload.startsWith("[") && payload.endsWith("]")) {
                first++;
                end--;
                payload = RuleSyntax.decode(payload.substring(1, payload.length() - 1));
            }
            facts.put("payload", payload);
            var keys = rule.arguments().stream()
                    .filter(argument -> "tokens".equals(argument.kind()))
                    .flatMap(
                            argument -> Objects.requireNonNull(checked.prepared.rules.tokens.get(argument.name()))
                                    .values()
                                    .keySet()
                                    .stream())
                    .toList();
            if (!keys.isEmpty()) {
                facts.put("allowedTokens", String.join(", ", keys));
            }
        } else if (part.text().startsWith("[") && part.text().endsWith("]")) {
            first++;
            end--;
            facts.put(
                    "payload",
                    RuleSyntax.decode(part.text().substring(1, part.text().length() - 1)));
        } else {
            var definitions = variant ? checked.prepared.rules.variants : checked.prepared.rules.utilities;
            var suggestions = definitions.stream()
                    .filter(definition ->
                            distance(part.text(), definition.family() ? definition.prefix() : definition.name()) <= 2)
                    .sorted(Comparator.comparingInt(definition ->
                            distance(part.text(), definition.family() ? definition.prefix() : definition.name())))
                    .limit(3)
                    .toList();
            if (!suggestions.isEmpty()) {
                facts.put(
                        "suggestions",
                        suggestions.stream().map(RuleLibrary.Rule::name).collect(Collectors.joining(", ")));
                facts.put("suggestedTemplate", template(checked.prepared.rules, suggestions.get(0)));
            }
        }
        facts.put("payloadOffset", String.valueOf(first));
        facts.put("payloadEnd", String.valueOf(end));
        var point = origin(new Source("<candidate>", candidate), first);
        return new Failure(new Diagnostic(
                error.code,
                String.valueOf(error.getMessage()),
                new Origin(point.source(), point.start(), Math.max(point.start(), end), point.line(), point.column()),
                related,
                facts));
    }

    private static int distance(String left, String right) {
        if (Math.abs(left.length() - right.length()) > 2) {
            return 3;
        }
        int[] previous = new int[right.length() + 1];
        for (int j = 0; j < previous.length; j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= left.length(); i++) {
            int[] next = new int[previous.length];
            next[0] = i;
            for (int j = 1; j < next.length; j++) {
                next[j] = Math.min(
                        Math.min(next[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + (left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1));
            }
            previous = next;
        }
        return previous[right.length()];
    }

    private static List<Origin> tokenOrigins(RuleLibrary rules, RuleLibrary.Match match) {
        if (!match.source().startsWith("token:")) {
            return List.of();
        }
        String[] parts = match.source().split(":");
        return List.of(origin(
                rules.source,
                Objects.requireNonNull(Objects.requireNonNull(rules.tokens.get(parts[1]))
                        .offsets()
                        .get(parts[2]))));
    }

    private static String template(RuleLibrary rules, RuleLibrary.Rule rule) {
        var writer = new SourceMap.Writer();
        raw(rules, rule.body(), writer);
        return writer.css();
    }

    private static String spellingKey(String text) {
        return text.codePoints()
                .mapToObj(value -> String.format(Locale.ROOT, "%06x", value))
                .collect(Collectors.joining());
    }

    private static String rank(int value) {
        return String.format(Locale.ROOT, "%08x", value);
    }

    private static Trace trace(RuleLibrary rules, RuleLibrary.Match match) {
        return new Trace(
                match.rule().name(),
                match.source(),
                match.value(),
                origin(rules.source, match.rule().offset()),
                tokenOrigins(rules, match),
                RuleBounds.check(match.rule().type(), match.rule().interval(), match.value()));
    }

    private static List<RuleSyntax.Node> substitute(List<RuleSyntax.Node> nodes, String parameter, String value) {
        return nodes.stream()
                .map(node -> new RuleSyntax.Node(
                        RuleSyntax.substitute(node.head(), parameter, value),
                        substitute(node.children(), parameter, value),
                        node.offset(),
                        node.block()))
                .toList();
    }

    private static List<RuleSyntax.Node> slot(List<RuleSyntax.Node> nodes, List<RuleSyntax.Node> value) {
        var result = new ArrayList<RuleSyntax.Node>();
        for (var node : nodes) {
            if (!node.block() && "@slot".equals(RuleSyntax.clean(node.head()))) {
                result.addAll(value);
            } else {
                result.add(new RuleSyntax.Node(node.head(), slot(node.children(), value), node.offset(), node.block()));
            }
        }
        return List.copyOf(result);
    }

    private static void render(
            RuleLibrary rules, List<RuleSyntax.Node> nodes, String selector, SourceMap.Writer writer) {
        int index = 0;
        while (index < nodes.size()) {
            var node = nodes.get(index);
            var location = location(rules.source, node.offset());
            if (node.block()) {
                if (node.head().startsWith("@")) {
                    writer.append(node.head() + " {\n", location);
                    render(rules, node.children(), selector, writer);
                    writer.append("}\n", location);
                } else {
                    render(rules, node.children(), RuleSyntax.guard(node.head(), selector), writer);
                }
                index++;
            } else {
                writer.append(selector + " {\n", location);
                do {
                    var declaration = nodes.get(index++);
                    writer.append(declaration.head() + ";\n", location(rules.source, declaration.offset()));
                } while (index < nodes.size() && !nodes.get(index).block());
                writer.append("}\n", location);
            }
        }
    }

    private static void raw(RuleLibrary rules, List<RuleSyntax.Node> nodes, SourceMap.Writer writer) {
        for (var node : nodes) {
            writer.append(node.head() + (node.block() ? " {\n" : ";\n"), location(rules.source, node.offset()));
            if (node.block()) {
                raw(rules, node.children(), writer);
                writer.append("}\n", location(rules.source, node.offset()));
            }
        }
    }

    private static void validateTemplates(Prepared prepared) {
        var writer = validationOutput(prepared);
        try {
            RuleAnchors.check(writer.css(), Set.of());
        } catch (CssSyntax.InvalidCss e) {
            throw mappedFailure(prepared.source(), writer, e);
        }
    }

    private static String probe(RuleLibrary rules, RuleLibrary.Rule rule, boolean variant) {
        var body = substitute(rule.body(), rule.parameter(), RuleLibrary.sample(rule.type()));
        if (variant) {
            body = slot(body, List.of(new RuleSyntax.Node("x: 0", List.of(), rule.offset(), false)));
        }
        var writer = new SourceMap.Writer();
        render(rules, body, ".probe", writer);
        return writer.css();
    }

    private static void templateHooks(Checked checked, List<RuleSyntax.Node> nodes) {
        for (var node : nodes) {
            if (node.block() && !node.head().startsWith("@")) {
                hooks(checked, RuleSyntax.guard(node.head(), ":root") + " {x:0;}", "");
            }
            templateHooks(checked, node.children());
        }
    }

    private static void hooks(Checked checked, String css, String guard) {
        for (var token : CssTokens.read(css)) {
            if (CssResource.SymbolKind.CLASS == token.kind()) {
                String name = token.name();
                if (!guard.equals(name) && !checked.bindings.externalClasses().contains(name)) {
                    throw RuleLibrary.error("U_EXTERNAL_CLASS", "Declare externalClasses hook " + name, 0);
                }
            }
        }
    }

    private static List<Description> descriptions(RuleLibrary rules, List<RuleLibrary.Rule> definitions) {
        return definitions.stream()
                .map(rule -> new Description(
                        rule.name(),
                        rule.type(),
                        rule.arguments().stream()
                                .map(argument -> argument.kind() + "(" + argument.name() + ")")
                                .toList(),
                        rule.interval(),
                        template(rules, rule),
                        origin(rules.source, rule.offset())))
                .toList();
    }

    private static JsonObject envelope(String kind, Source source) {
        var json = new JsonObject();
        json.addProperty("version", VERSION);
        json.addProperty("kind", kind);
        json.addProperty("source", source.name());
        json.addProperty("text", source.text());
        json.addProperty("sourceMap", source.sourceMap());
        return json;
    }

    private static JsonArray array(Set<String> values) {
        var array = new JsonArray();
        values.stream().sorted().forEach(array::add);
        return array;
    }

    private static Set<String> strings(JsonObject value, String key) {
        var values = new HashSet<String>();
        value.getAsJsonArray(key).forEach(item -> values.add(item.getAsString()));
        return Set.copyOf(values);
    }

    private static Source readSource(JsonObject value) {
        return new Source(
                value.get("source").getAsString(),
                value.get("text").getAsString(),
                value.get("sourceMap").getAsString());
    }

    private static Failure mappedFailure(Source source, SourceMap.Writer writer, CssSyntax.InvalidCss error) {
        var location = SourceMap.location(
                writer.css(),
                JsonParser.parseString(writer.map("utility.css")).getAsJsonObject(),
                Math.min(writer.css().length(), error.offset()));
        var failure = bindingFailure(source, error);
        if (null == location) {
            return failure;
        }
        int offset = originalOffset(location);
        return new Failure(new Diagnostic(
                failure.diagnostic().code(),
                failure.diagnostic().message(),
                new Origin(
                        location.source(),
                        offset,
                        Math.min(offset + 1, location.content().length()),
                        location.line(),
                        location.column()),
                List.of(),
                Map.of()));
    }

    private static Failure bindingFailure(Source source, IllegalArgumentException error) {
        String message = String.valueOf(error.getMessage());
        String code = message.startsWith("U_") && message.contains(":")
                ? message.substring(0, message.indexOf(':'))
                : message.contains("asset") || message.contains("URL")
                        ? "U_ASSET_REFERENCE"
                        : message.contains("container") ? "U_CONTAINER_NAME" : "U_BINDING";
        if (message.startsWith(code + ":")) {
            message = message.substring(code.length() + 1).stripLeading();
        }
        return failure(source, code, message, 0);
    }

    private static JsonObject descriptor(String json, String kind) {
        var value = JsonParser.parseString(json).getAsJsonObject();
        var version = value.get("version");
        if (null == version
                || !version.isJsonPrimitive()
                || !version.getAsJsonPrimitive().isNumber()
                || 0 != version.getAsBigDecimal().compareTo(BigDecimal.valueOf(VERSION))) {
            throw new IllegalArgumentException("Unsupported utility language descriptor version");
        }
        for (String field : List.of("kind", "source", "text", "sourceMap")) {
            stringField(value, field);
        }
        if (!kind.equals(value.get("kind").getAsString())) {
            throw new IllegalArgumentException("Unsupported utility language descriptor kind; expected " + kind);
        }
        if ("checked".equals(kind)) {
            stringField(value, "owner");
            for (String field : List.of("externalClasses", "exportedIds", "exportedAnimations")) {
                arrayField(value, field);
            }
            var bindings = value.get("bindings");
            if (null == bindings || !bindings.isJsonObject()) {
                throw new IllegalArgumentException("Missing bindings object");
            }
            var context = bindings.getAsJsonObject();
            for (String field : List.of(
                    "externalProperties",
                    "externalAnimations",
                    "externalFonts",
                    "externalContainers",
                    "externalAnchors",
                    "includes")) {
                arrayField(context, field);
            }
            for (String field : List.of("imports", "properties", "ids", "assets", "fonts")) {
                if (!context.has(field) || !context.get(field).isJsonObject()) {
                    throw new IllegalArgumentException("Expected binding object " + field);
                }
            }
        }
        return value;
    }

    private static void stringField(JsonObject value, String field) {
        if (!value.has(field)
                || !value.get(field).isJsonPrimitive()
                || !value.get(field).getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Expected string field " + field);
        }
    }

    private static void arrayField(JsonObject value, String field) {
        if (!value.has(field)
                || !value.get(field).isJsonArray()
                || value.getAsJsonArray(field).asList().stream()
                        .anyMatch(item -> !item.isJsonPrimitive()
                                || !item.getAsJsonPrimitive().isString())) {
            throw new IllegalArgumentException("Expected string array " + field);
        }
    }

    private static IllegalArgumentException invalidDescriptor(RuntimeException error) {
        return new IllegalArgumentException(
                "Invalid utility language descriptor: " + String.valueOf(error.getMessage()), error);
    }

    private static String digest(String value) {
        try {
            return HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private static Failure failure(Source source, String code, String message, int offset) {
        return new Failure(new Diagnostic(
                code,
                message,
                origin(source, Math.max(0, Math.min(offset, source.text().length()))),
                List.of(),
                Map.of()));
    }

    private static Origin origin(Source source, int offset) {
        var location = location(source, offset);
        return new Origin(
                location.source(),
                originalOffset(location),
                Math.min(originalOffset(location) + 1, location.content().length()),
                location.line(),
                location.column());
    }

    private static int originalOffset(SourceMap.Location location) {
        int start = 0;
        for (int line = 0; line < location.line() && start < location.content().length(); line++) {
            int next = location.content().indexOf('\n', start);
            start = next < 0 ? location.content().length() : next + 1;
        }
        return Math.min(location.content().length(), start + location.column());
    }

    private static SourceMap.Location location(Source source, int offset) {
        if (!source.sourceMap().isEmpty()) {
            var location = SourceMap.location(
                    source.text(), JsonParser.parseString(source.sourceMap()).getAsJsonObject(), offset);
            if (null != location) {
                return location;
            }
        }
        int line = 0;
        int start = 0;
        for (int i = 0; i < offset; i++) {
            if ('\n' == source.text().charAt(i)) {
                line++;
                start = i + 1;
            }
        }
        return new SourceMap.Location(source.name(), source.text(), line, offset - start);
    }
}
