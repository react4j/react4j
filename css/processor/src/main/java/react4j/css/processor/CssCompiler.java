package react4j.css.processor;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.AbstractHasTopLevelRules;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSNestedDeclarations;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorAttribute;
import com.helger.css.decl.CSSSelectorMemberFunctionLike;
import com.helger.css.decl.CSSSelectorMemberHost;
import com.helger.css.decl.CSSSelectorMemberHostContext;
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorMemberSlotted;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.ECSSSelectorCombinator;
import com.helger.css.writer.CSSWriterSettings;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** In-process CSS parsing, ownership checks and static output. */
final class CssCompiler {
    private static final String NUMBER = "[+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?";
    private static final Set<String> CSS_WIDE = Set.of("initial", "inherit", "unset", "revert", "revert-layer");
    private final CssSyntax syntax;
    private final String root;
    private final Exports exports;
    private Map<String, String> ids = Map.of();
    private final Map<String, String> animations = new LinkedHashMap<>();
    private final IntFunction<SourceMap.Location> locations;
    private final Set<String> found = new HashSet<>();
    private final Map<String, CSSKeyframesRule> keyframes = new LinkedHashMap<>();
    private final Map<String, Import> imports;
    private final Set<String> usedImports = new LinkedHashSet<>();
    private final CSSWriterSettings compact = new CSSWriterSettings(true);
    private final SourceMap.Writer output = new SourceMap.Writer();

    private CssCompiler(
            String source,
            int prefix,
            String root,
            Exports exports,
            IntFunction<SourceMap.Location> locations,
            Map<String, Import> imports) {
        syntax = new CssSyntax(source, prefix);
        this.root = root;
        this.exports = exports;
        this.locations = locations;
        this.imports = imports;
    }

    record Import(String owner, Kind kind, boolean stable) {
        String cssName() {
            return Names.root(owner) + (Kind.KEYFRAMES == kind ? "--keyframes" : "");
        }
    }

    record Exports(
            List<String> modifiers,
            Set<String> ids,
            Set<String> animations,
            Set<String> authoredIds,
            Set<String> authoredProperties) {}

    record Result(
            SourceMap.Writer output,
            Set<String> imports,
            Map<String, String> properties,
            Set<String> propertyImports,
            Map<String, String> ids,
            Map<String, String> animations) {}

    private final Map<String, String> properties = new LinkedHashMap<>();
    private final Set<String> usedProperties = new LinkedHashSet<>();

    private void properties(CascadingStyleSheet sheet, Map<String, CssProperties.Import> imported) {
        CssProperties.rewrite(sheet, syntax, name -> {
            var binding = imported.get(name);
            String resolved;
            if (null == binding) {
                resolved = exports.authoredProperties().contains(name)
                        ? "--" + name
                        : "--" + root + "--" + Names.encode(name);
            } else {
                usedProperties.add(name);
                resolved = binding.binding().cssName();
            }
            properties.put(name, resolved);
            return resolved;
        });
    }

    private Result result() {
        return new Result(
                output,
                Set.copyOf(usedImports),
                Map.copyOf(properties),
                Set.copyOf(usedProperties),
                ids,
                Map.copyOf(animations));
    }

    static Result component(
            String css,
            String root,
            Exports exports,
            IntFunction<SourceMap.Location> locations,
            Map<String, Import> imports,
            Map<String, CssProperties.Import> properties) {
        String prefix = "." + root + " {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), root, exports, locations, imports);
        var sheet = compiler.syntax.parse(true);
        compiler.properties(sheet, properties);
        compiler.ids = CssIds.rewrite(sheet, compiler.syntax, compiler.root, exports.ids(), exports.authoredIds());
        for (var rule : sheet.getAllRules()) {
            if (rule instanceof CSSKeyframesRule frames) {
                String name = frames.getAnimationName();
                if (!"@keyframes".equalsIgnoreCase(frames.getDeclaration())) {
                    throw compiler.syntax.invalid("Only unprefixed @keyframes definitions are supported", frames);
                }
                compiler.checkAnimationName(name, frames);
                if (imports.containsKey(name)) {
                    throw compiler.syntax.invalid(
                            "Local keyframe name conflicts with imported animation: " + name, frames);
                }
                if ("none".equalsIgnoreCase(name)
                        || "default".equalsIgnoreCase(name)
                        || CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))) {
                    throw compiler.syntax.invalid("Reserved keyframe name: " + name, frames);
                }
                if (null != compiler.keyframes.putIfAbsent(name, frames)) {
                    throw compiler.syntax.invalid("Duplicate local keyframe name: " + name, frames);
                }
            }
        }
        compiler.exports.animations().forEach(compiler::exportAnimation);
        for (var frames : compiler.keyframes.values()) {
            compiler.animations.put(frames.getAnimationName(), compiler.animationName(frames.getAnimationName()));
            compiler.writeKeyframes(frames, compiler.animationName(frames.getAnimationName()));
        }
        compiler.style(sheet.getAllStyleRules().get(0), new Scope("." + root, true), true);
        for (var name : exports.modifiers()) {
            if (!"style".equals(name) && !compiler.found.contains(name)) {
                throw new CssSyntax.InvalidCss("Accessor " + name + "() has no matching local selector", 0);
            }
        }
        return compiler.result();
    }

    static Result keyframes(
            String css,
            String root,
            Exports exports,
            IntFunction<SourceMap.Location> locations,
            Map<String, CssProperties.Import> properties) {
        String prefix = "@keyframes shared {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), root, exports, locations, Map.of());
        var sheet = compiler.syntax.parseKeyframes();
        compiler.properties(sheet, properties);
        compiler.ids = CssIds.rewrite(sheet, compiler.syntax, compiler.root, exports.ids(), exports.authoredIds());
        var frames = (CSSKeyframesRule) sheet.getAllRules().get(0);
        compiler.animations.put("", root + "--keyframes");
        compiler.writeKeyframes(frames, root + "--keyframes");
        return compiler.result();
    }

    static Result global(
            String css,
            String root,
            Exports exports,
            IntFunction<SourceMap.Location> locations,
            Map<String, Import> imports,
            Map<String, CssProperties.Import> properties) {
        var compiler = new CssCompiler(css, 0, root, exports, locations, imports);
        var sheet = compiler.syntax.parse(false);
        compiler.properties(sheet, properties);
        compiler.ids = CssIds.rewrite(sheet, compiler.syntax, compiler.root, exports.ids(), exports.authoredIds());
        exports.animations().forEach(compiler::exportAnimation);
        for (var rule : sheet.getAllRules()) {
            compiler.globalRule((ICSSSourceLocationAware) rule);
        }
        return compiler.result();
    }

    private void globalRule(ICSSSourceLocationAware rule) {
        if (rule instanceof CSSStyleRule style) {
            for (int i = 0; i < style.getSelectorCount(); i++) {
                if (0 != i) {
                    output.append(",");
                }
                var selector = Objects.requireNonNull(style.getSelectorAtIndex(i));
                output.append(globalSelector(selector), location(selector));
            }
            output.append("{");
            writeDeclarations(style.getAllDeclarations());
            for (var child : style.getAllRules()) {
                globalRule((ICSSSourceLocationAware) child);
            }
            output.append("}");
        } else if (rule instanceof CSSNestedDeclarations declarations) {
            writeDeclarations(declarations.getAllDeclarations());
        } else if (rule instanceof CSSMediaRule media) {
            globalConditional(media);
        } else if (rule instanceof CSSSupportsRule supports) {
            globalConditional(supports);
        } else {
            throw syntax.invalid("Unsupported global rule", rule);
        }
    }

    private void globalConditional(AbstractHasTopLevelRules rule) {
        output.append(conditionalHeader(rule) + "{", location((ICSSSourceLocationAware) rule));
        for (var child : rule.getAllRules()) {
            globalRule((ICSSSourceLocationAware) child);
        }
        output.append("}");
    }

    private void writeDeclarations(List<CSSDeclaration> declarations) {
        for (var declaration : declarations) {
            animation(declaration);
            output.append(declaration.getAsCSSString(compact, 0) + ";", location(declaration));
        }
    }

    static void checkAlias(String name, Kind kind) {
        if (!name.matches("(?:--|-?(?:[a-zA-Z_]|[^\\x00-\\x7f]))(?:[a-zA-Z0-9_-]|[^\\x00-\\x7f])*")
                || "--".equals(name)
                || (Kind.KEYFRAMES == kind
                        && ("none".equalsIgnoreCase(name)
                                || "default".equalsIgnoreCase(name)
                                || CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))))) {
            throw new IllegalArgumentException("Invalid imported CSS name: " + name);
        }
    }

    private record Scope(String selector, boolean owned) {}

    private void style(CSSStyleRule rule, Scope parent, boolean implicit) {
        var selectors = implicit
                ? List.of(parent)
                : rule.getAllSelectors().stream()
                        .map(s -> selector(s, parent, true))
                        .toList();
        String selector = selectors.stream().map(Scope::selector).collect(Collectors.joining(","));
        declarations(rule.getAllDeclarations(), selectors, rule);
        var nestedParent = new Scope(
                rule.getSelectorCount() > 1 ? ":is(" + selector + ")" : selector,
                selectors.stream().allMatch(Scope::owned));
        for (var nested : rule.getAllRules()) {
            if (nested instanceof CSSStyleRule child) {
                style(child, nestedParent, false);
            } else if (nested instanceof CSSNestedDeclarations declarations) {
                declarations(declarations.getAllDeclarations(), selectors, rule);
            } else if (nested instanceof CSSMediaRule media) {
                conditional(media, nestedParent);
            } else if (nested instanceof CSSSupportsRule supports) {
                conditional(supports, nestedParent);
            } else {
                throw syntax.invalid("Unsupported component rule", (ICSSSourceLocationAware) nested);
            }
        }
    }

    private void conditional(AbstractHasTopLevelRules rule, Scope parent) {
        output.append(conditionalHeader(rule) + "{", location((ICSSSourceLocationAware) rule));
        for (var child : rule.getAllRules()) {
            if (child instanceof CSSStyleRule style) {
                style(style, parent, false);
            } else if (child instanceof CSSMediaRule media) {
                conditional(media, parent);
            } else if (child instanceof CSSSupportsRule supports) {
                conditional(supports, parent);
            } else {
                throw syntax.invalid("Unsupported component rule", (ICSSSourceLocationAware) child);
            }
        }
        output.append("}");
    }

    private String conditionalHeader(AbstractHasTopLevelRules rule) {
        String header;
        if (rule instanceof CSSMediaRule media) {
            header = "@media "
                    + media.getAllMediaQueries().stream()
                            .map(q -> q.getAsCSSString(compact, 0))
                            .collect(Collectors.joining(","));
        } else {
            var supports = (CSSSupportsRule) rule;
            header = "@supports "
                    + supports.getAllSupportConditionMembers().stream()
                            .map(q -> q.getAsCSSString(compact, 0))
                            .collect(Collectors.joining(" "));
        }
        return header;
    }

    private void declarations(List<CSSDeclaration> declarations, List<Scope> selectors, CSSStyleRule rule) {
        if (declarations.isEmpty()) {
            return;
        }
        for (int i = 0; i < selectors.size(); i++) {
            if (0 != i) {
                output.append(",");
            }
            output.append(selectors.get(i).selector(), location(Objects.requireNonNull(rule.getSelectorAtIndex(i))));
        }
        output.append("{");
        writeDeclarations(declarations);
        output.append("}");
    }

    private String animationName(String name) {
        return root + "--keyframes-" + Names.encode(name);
    }

    private void exportAnimation(String name) {
        var imported = imports.get(name);
        if (null != imported) {
            if (Kind.KEYFRAMES != imported.kind()) {
                throw new IllegalArgumentException("Animation accessor must reference imported @Keyframes: " + name);
            }
            usedImports.add(name);
            animations.put(name, imported.cssName());
        } else if (keyframes.containsKey(name)) {
            animations.put(name, animationName(name));
        } else {
            throw new IllegalArgumentException("Unknown animation accessor name: " + name);
        }
    }

    private void checkAnimationName(String name, ICSSSourceLocationAware node) {
        if (name.contains("\\") || name.startsWith("\"") || name.startsWith("'")) {
            throw syntax.invalid("Animation names must be unescaped, unquoted identifiers", node);
        }
    }

    private void writeKeyframes(CSSKeyframesRule rule, String name) {
        output.append("@keyframes " + name + "{", location(rule));
        for (var block : rule.getAllBlocks()) {
            for (var selector : block.getAllKeyframesSelectors()) {
                if (selector.endsWith("%") && Double.parseDouble(selector.substring(0, selector.length() - 1)) > 100) {
                    throw syntax.invalid("Keyframe percentages must be from 0% to 100%", block);
                }
            }
            output.append(String.join(",", block.getAllKeyframesSelectors()) + "{", location(block));
            for (var declaration : block.getAllDeclarations()) {
                if (animationBinding(declaration)) {
                    throw syntax.invalid("Animation bindings are not allowed inside keyframes", declaration);
                }
                output.append(declaration.getAsCSSString(compact, 0) + ";", location(declaration));
            }
            output.append("}");
        }
        output.append("}");
    }

    private boolean animationBinding(CSSDeclaration declaration) {
        String property = declaration.getProperty().toLowerCase(Locale.ROOT);
        if (property.contains("\\")) {
            throw syntax.invalid("Escaped component property identifiers are unsupported", declaration);
        }
        if (property.startsWith("-")
                && !property.startsWith("--")
                && (property.endsWith("-animation") || property.endsWith("-animation-name"))) {
            throw syntax.invalid("Use unprefixed animation or animation-name for local keyframes", declaration);
        }
        return "animation".equals(property) || "animation-name".equals(property);
    }

    private void animation(CSSDeclaration declaration) {
        if (!animationBinding(declaration)) {
            return;
        }
        boolean shorthand = "animation".equalsIgnoreCase(declaration.getProperty());
        var members = declaration.getExpression().getAllMembers();
        if (1 == members.size()
                && members.get(0) instanceof CSSExpressionMemberTermSimple simple
                && CSS_WIDE.contains(simple.getValue().toLowerCase(Locale.ROOT))) {
            return;
        }
        var used = new HashSet<String>();
        boolean named = false;
        for (var member : members) {
            if (ECSSExpressionOperator.COMMA == member) {
                used.clear();
                named = false;
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
            String name = simple.getValue();
            checkAnimationName(name, simple);
            String lower = name.toLowerCase(Locale.ROOT);
            if (CSS_WIDE.contains(lower)) {
                throw syntax.invalid("CSS-wide animation keywords must stand alone", simple);
            }
            if (shorthand) {
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
            if (!"none".equals(lower)) {
                if (imports.containsKey(name)) {
                    var binding = imports.get(name);
                    if (Kind.KEYFRAMES != binding.kind()) {
                        throw syntax.invalid("Animation name must reference imported @Keyframes: " + name, simple);
                    }
                    usedImports.add(name);
                    simple.setValue(binding.cssName());
                } else if (keyframes.containsKey(name)) {
                    simple.setValue(animationName(name));
                } else {
                    throw syntax.invalid("Unknown local animation name: " + name, simple);
                }
            }
            named = true;
        }
    }

    private Scope selector(CSSSelector selector, Scope parent, boolean nested) {
        var result = new StringBuilder();
        int index = 0;
        boolean owned = parent.owned();
        boolean needsPeer = false;
        for (var member : selector.getAllMembers()) {
            if (nested && 0 == index && !(member instanceof CSSSelectorSimpleMember simple && simple.isNesting())) {
                throw syntax.invalid("Component selectors must start with &", selector);
            }
            if (member instanceof ECSSSelectorCombinator combinator && nested && !needsPeer) {
                result.append(combinator.getAsCSSString(compact, 0));
                needsPeer = true;
                owned = false;
            } else if (member instanceof CSSSelectorMemberFunctionLike function && isReference(function)) {
                if (!nested || !needsPeer) {
                    throw syntax.invalid(
                            "Component peer references must start a compound after a combinator", function);
                }
                result.append(reference(function));
                needsPeer = false;
            } else if (needsPeer) {
                throw syntax.invalid("Component combinators must target an imported :css(name) root", selector);
            } else if (member instanceof CSSSelectorSimpleMember simple) {
                String value = simple.getValue();
                if (value.contains("\\")) {
                    throw syntax.invalid("Escaped selector identifiers are unsupported", simple);
                }
                if (simple.isNesting() && nested && 0 == index) {
                    result.append(parent.selector());
                } else if (simple.isHash() && owned && ids.containsKey(value.substring(1))) {
                    result.append('#').append(ids.get(value.substring(1)));
                } else if (simple.isClass()) {
                    String name = value.substring(1);
                    if (!owned) {
                        throw syntax.invalid("Local modifiers cannot qualify imported peers", simple);
                    }
                    if ("style".equals(name) || !exports.modifiers().contains(name)) {
                        throw syntax.invalid("Local selector ." + name + " has no matching modifier accessor", simple);
                    }
                    found.add(name);
                    result.append('.').append(root).append("--").append(Names.encode(name));
                } else if (simple.isPseudo()
                        && !Set.of(":global", ":local", ":root", ":scope", ":host", ":css", ":css()")
                                .contains(value.toLowerCase(Locale.ROOT))) {
                    result.append(value);
                } else {
                    throw syntax.invalid("Component selectors must target the same owned element", simple);
                }
            } else if (member instanceof CSSSelectorMemberNot not) {
                result.append(function(":not", not.getAllSelectors(), owned));
            } else if (member instanceof CSSSelectorMemberPseudoIs is) {
                result.append(function(":is", is.getAllSelectors(), owned));
            } else if (member instanceof CSSSelectorMemberPseudoWhere where) {
                result.append(function(":where", where.getAllSelectors(), owned));
            } else if (member instanceof CSSSelectorAttribute attribute) {
                result.append(attribute.getAsCSSString(compact, 0));
            } else {
                throw syntax.invalid(
                        "Component selectors must target the same owned element",
                        member instanceof ICSSSourceLocationAware located ? located : selector);
            }
            index++;
        }
        if (needsPeer) {
            throw syntax.invalid("Component combinators must target an imported :css(name) root", selector);
        }
        return new Scope(result.toString(), owned);
    }

    private String function(String name, List<CSSSelector> selectors, boolean owned) {
        return name + "("
                + selectors.stream()
                        .map(s -> selector(s, new Scope("", owned), false).selector())
                        .collect(Collectors.joining(","))
                + ")";
    }

    private static boolean isReference(CSSSelectorMemberFunctionLike function) {
        return ":css(".equalsIgnoreCase(function.getFunctionName());
    }

    private String reference(CSSSelectorMemberFunctionLike function) {
        var arguments = function.getParameterExpression().getAllMembers();
        if (1 != arguments.size() || !(arguments.get(0) instanceof CSSExpressionMemberTermSimple simple)) {
            throw syntax.invalid(":css() requires one imported component name", function);
        }
        String alias = simple.getValue();
        try {
            checkAlias(alias, Kind.COMPONENT);
        } catch (IllegalArgumentException e) {
            throw syntax.invalid(":css() requires an unescaped, unquoted imported component name", function);
        }
        var binding = imports.get(alias);
        if (null == binding || Kind.COMPONENT != binding.kind()) {
            throw syntax.invalid("Unknown imported component name: " + alias, function);
        }
        usedImports.add(alias);
        return "." + binding.cssName();
    }

    private String globalSelector(CSSSelector selector) {
        var result = new StringBuilder();
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelector nested) {
                result.append(globalSelector(nested));
            } else if (member instanceof CSSSelectorMemberFunctionLike function
                    && function.getFunctionName().contains("\\")) {
                throw syntax.invalid("Escaped selector identifiers are unsupported", function);
            } else if (member instanceof CSSSelectorMemberFunctionLike function && isReference(function)) {
                result.append(reference(function));
            } else if (member instanceof CSSSelectorMemberNot not) {
                result.append(globalFunction(":not", not.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberPseudoIs is) {
                result.append(globalFunction(":is", is.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberPseudoWhere where) {
                result.append(globalFunction(":where", where.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberPseudoHas has) {
                result.append(globalFunction(":has", has.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberHost host) {
                result.append(globalFunction(":host", List.of(host.getSelector())));
            } else if (member instanceof CSSSelectorMemberHostContext host) {
                result.append(globalFunction(":host-context", List.of(host.getSelector())));
            } else if (member instanceof CSSSelectorMemberSlotted slotted) {
                result.append(globalFunction("::slotted", List.of(slotted.getSelector())));
            } else if (member instanceof CSSSelectorSimpleMember simple) {
                if (simple.getValue().contains("\\")) {
                    throw syntax.invalid("Escaped selector identifiers are unsupported", simple);
                }
                if (Set.of(":css", ":css()").contains(simple.getValue().toLowerCase(Locale.ROOT))) {
                    throw syntax.invalid(":css() requires one imported component name", simple);
                }
                result.append(
                        simple.isHash() && ids.containsKey(simple.getValue().substring(1))
                                ? "#" + ids.get(simple.getValue().substring(1))
                                : simple.getAsCSSString(compact, 0));
            } else {
                result.append(member.getAsCSSString(compact, 0));
            }
        }
        return result.toString();
    }

    private String globalFunction(String name, List<CSSSelector> selectors) {
        return name + "(" + selectors.stream().map(this::globalSelector).collect(Collectors.joining(",")) + ")";
    }

    private SourceMap.Location location(ICSSSourceLocationAware node) {
        var location = node.getSourceLocation();
        return locations.apply(
                null == location
                        ? 0
                        : syntax.offset(
                                location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber()));
    }
}
