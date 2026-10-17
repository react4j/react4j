package react4j.css.processor;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.AbstractHasTopLevelRules;
import com.helger.css.decl.CSSDeclaration;
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
import com.helger.css.writer.CSSWriterSettings;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** In-process CSS parsing, ownership checks and static output. */
final class CssCompiler {
    private final CssSyntax syntax;
    private final String root;
    private final List<String> accessors;
    private final IntFunction<SourceMap.Location> locations;
    private final Set<String> found = new HashSet<>();
    private final CSSWriterSettings compact = new CSSWriterSettings(true);
    private final SourceMap.Writer output = new SourceMap.Writer();

    private CssCompiler(
            String source, int prefix, String root, List<String> accessors, IntFunction<SourceMap.Location> locations) {
        syntax = new CssSyntax(source, prefix);
        this.root = root;
        this.accessors = accessors;
        this.locations = locations;
    }

    static SourceMap.Writer component(
            String css, String root, List<String> accessors, IntFunction<SourceMap.Location> locations) {
        String prefix = "." + root + " {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), root, accessors, locations);
        var sheet = compiler.syntax.parse(true);
        if (1 != sheet.getRuleCount() || !(sheet.getRuleAtIndex(0) instanceof CSSStyleRule style)) {
            throw new CssSyntax.InvalidCss("Component CSS must remain inside its implicit root", 0);
        }
        compiler.style(style, "." + root, true);
        for (var name : accessors) {
            if (!"style".equals(name) && !compiler.found.contains(name)) {
                throw new CssSyntax.InvalidCss("Accessor " + name + "() has no matching local selector", 0);
            }
        }
        return compiler.output;
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
        output.append(header + "{", location((ICSSSourceLocationAware) rule));
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

    private void declarations(List<CSSDeclaration> declarations, String selector, ICSSSourceLocationAware rule) {
        if (declarations.isEmpty()) {
            return;
        }
        output.append(selector + "{", location(rule));
        for (var declaration : declarations) {
            if (Set.of("animation", "animation-name")
                    .contains(declaration.getProperty().toLowerCase(Locale.ROOT))) {
                throw syntax.invalid("Component keyframe bindings are outside this proof", declaration);
            }
            output.append(declaration.getAsCSSString(compact, 0) + ";", location(declaration));
        }
        output.append("}");
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
