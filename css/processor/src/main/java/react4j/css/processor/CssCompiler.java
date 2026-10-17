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
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.writer.CSSWriterSettings;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** In-process CSS parsing, ownership checks and static output. */
final class CssCompiler {
    private static final String NUMBER = "[+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)(?:[eE][+-]?[0-9]+)?";
    private static final Set<String> CSS_WIDE = Set.of("initial", "inherit", "unset", "revert", "revert-layer");
    private final CssSyntax syntax;
    private final String root;
    private final List<String> accessors;
    private final IntFunction<SourceMap.Location> locations;
    private final Set<String> found = new HashSet<>();
    private final Map<String, CSSKeyframesRule> keyframes = new LinkedHashMap<>();
    private final Map<String, String> imports;
    private final Set<String> usedImports = new LinkedHashSet<>();
    private final CSSWriterSettings compact = new CSSWriterSettings(true);
    private final SourceMap.Writer output = new SourceMap.Writer();

    private CssCompiler(
            String source,
            int prefix,
            String root,
            List<String> accessors,
            IntFunction<SourceMap.Location> locations,
            Map<String, String> imports) {
        syntax = new CssSyntax(source, prefix);
        this.root = root;
        this.accessors = accessors;
        this.locations = locations;
        this.imports = imports;
    }

    record Result(SourceMap.Writer output, Set<String> imports) {}

    static Result component(
            String css,
            String root,
            List<String> accessors,
            IntFunction<SourceMap.Location> locations,
            Map<String, String> imports) {
        String prefix = "." + root + " {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), root, accessors, locations, imports);
        var sheet = compiler.syntax.parse(true);
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
        for (var frames : compiler.keyframes.values()) {
            compiler.keyframes(frames);
        }
        compiler.style(sheet.getAllStyleRules().get(0), "." + root, true);
        for (var name : accessors) {
            if (!"style".equals(name) && !compiler.found.contains(name)) {
                throw new CssSyntax.InvalidCss("Accessor " + name + "() has no matching local selector", 0);
            }
        }
        return new Result(compiler.output, Set.copyOf(compiler.usedImports));
    }

    static Result keyframes(String css, String name, IntFunction<SourceMap.Location> locations) {
        String prefix = "@keyframes shared {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), "", List.of(), locations, Map.of());
        var sheet = compiler.syntax.parseKeyframes();
        var frames = (CSSKeyframesRule) sheet.getAllRules().get(0);
        compiler.writeKeyframes(frames, name);
        return new Result(compiler.output, Set.of());
    }

    static Result global(String css, IntFunction<SourceMap.Location> locations, Map<String, String> imports) {
        var compiler = new CssCompiler(css, 0, "", List.of(), locations, imports);
        for (var rule : compiler.syntax.parse(false).getAllRules()) {
            compiler.globalRule((ICSSSourceLocationAware) rule);
        }
        return new Result(compiler.output, Set.copyOf(compiler.usedImports));
    }

    private void globalRule(ICSSSourceLocationAware rule) {
        if (rule instanceof CSSStyleRule style) {
            output.append(style.getSelectorsAsCSSString(compact, 0) + "{", location(style));
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

    static void checkAlias(String name) {
        if (!name.matches("(?:--|-?(?:[a-zA-Z_]|[^\\x00-\\x7f]))(?:[a-zA-Z0-9_-]|[^\\x00-\\x7f])*")
                || "none".equalsIgnoreCase(name)
                || "default".equalsIgnoreCase(name)
                || CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Invalid imported animation name: " + name);
        }
    }

    private void style(CSSStyleRule rule, String parent, boolean implicit) {
        String selector = implicit
                ? parent
                : rule.getAllSelectors().stream()
                        .map(s -> selector(s, parent, true))
                        .collect(Collectors.joining(","));
        declarations(rule.getAllDeclarations(), selector, rule);
        String nestedParent = rule.getSelectorCount() > 1 ? ":is(" + selector + ")" : selector;
        for (var nested : rule.getAllRules()) {
            if (nested instanceof CSSStyleRule child) {
                style(child, nestedParent, false);
            } else if (nested instanceof CSSNestedDeclarations declarations) {
                declarations(declarations.getAllDeclarations(), selector, declarations);
            } else if (nested instanceof CSSMediaRule media) {
                conditional(media, nestedParent);
            } else if (nested instanceof CSSSupportsRule supports) {
                conditional(supports, nestedParent);
            } else {
                throw syntax.invalid("Unsupported component rule", (ICSSSourceLocationAware) nested);
            }
        }
    }

    private void conditional(AbstractHasTopLevelRules rule, String parent) {
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

    private void declarations(List<CSSDeclaration> declarations, String selector, ICSSSourceLocationAware rule) {
        if (declarations.isEmpty()) {
            return;
        }
        output.append(selector + "{", location(rule));
        writeDeclarations(declarations);
        output.append("}");
    }

    private String animationName(String name) {
        return root + "--keyframes-" + Names.encode(name);
    }

    private void checkAnimationName(String name, ICSSSourceLocationAware node) {
        if (name.contains("\\") || name.startsWith("\"") || name.startsWith("'")) {
            throw syntax.invalid("Animation names must be unescaped, unquoted identifiers", node);
        }
    }

    private void keyframes(CSSKeyframesRule rule) {
        writeKeyframes(rule, animationName(rule.getAnimationName()));
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
                    usedImports.add(name);
                    simple.setValue(imports.get(name));
                } else if (keyframes.containsKey(name)) {
                    simple.setValue(animationName(name));
                } else {
                    throw syntax.invalid("Unknown local animation name: " + name, simple);
                }
            }
            named = true;
        }
    }

    private String selector(CSSSelector selector, String parent, boolean nested) {
        var result = new StringBuilder();
        int index = 0;
        for (var member : selector.getAllMembers()) {
            if (nested && 0 == index && !(member instanceof CSSSelectorSimpleMember simple && simple.isNesting())) {
                throw syntax.invalid("Component selectors must start with &", selector);
            }
            if (member instanceof CSSSelectorSimpleMember simple) {
                String value = simple.getValue();
                if (value.contains("\\")) {
                    throw syntax.invalid("Escaped selector identifiers are outside this proof", simple);
                }
                if (simple.isNesting() && nested && 0 == index) {
                    result.append(parent);
                } else if (simple.isClass()) {
                    String name = value.substring(1);
                    if ("style".equals(name) || !accessors.contains(name)) {
                        throw syntax.invalid("Local selector ." + name + " has no matching modifier accessor", simple);
                    }
                    found.add(name);
                    result.append('.').append(root).append("--").append(Names.encode(name));
                } else if (simple.isPseudo()
                        && !Set.of(":global", ":local", ":root", ":scope", ":host")
                                .contains(value.toLowerCase(Locale.ROOT))) {
                    result.append(value);
                } else {
                    throw syntax.invalid("Component selectors must target the same owned element", simple);
                }
            } else if (member instanceof CSSSelectorMemberNot not) {
                result.append(function(":not", not.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberPseudoIs is) {
                result.append(function(":is", is.getAllSelectors()));
            } else if (member instanceof CSSSelectorMemberPseudoWhere where) {
                result.append(function(":where", where.getAllSelectors()));
            } else if (member instanceof CSSSelectorAttribute attribute) {
                result.append(attribute.getAsCSSString(compact, 0));
            } else {
                throw syntax.invalid(
                        "Component selectors must target the same owned element",
                        member instanceof ICSSSourceLocationAware located ? located : selector);
            }
            index++;
        }
        return result.toString();
    }

    private String function(String name, List<CSSSelector> selectors) {
        return name + "(" + selectors.stream().map(s -> selector(s, "", false)).collect(Collectors.joining(",")) + ")";
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
