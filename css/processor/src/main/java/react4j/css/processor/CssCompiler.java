package react4j.css.processor;

import com.helger.css.CSSSourceLocation;
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
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.handler.CSSHandler;
import com.helger.css.handler.ECSSNodeType;
import com.helger.css.parser.CSSCharStream;
import com.helger.css.parser.CSSNode;
import com.helger.css.parser.ParseException;
import com.helger.css.parser.ParserCSS30;
import com.helger.css.parser.ParserCSS30TokenManager;
import com.helger.css.parser.Token;
import com.helger.css.parser.TokenMgrException;
import com.helger.css.reader.errorhandler.ICSSInterpretErrorHandler;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import com.helger.css.writer.CSSWriterSettings;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/** In-process CSS parsing, ownership checks and static output. */
final class CssCompiler {
    static final class InvalidCss extends IllegalArgumentException {
        private final int offset;

        InvalidCss(String message, int offset) {
            super(message);
            this.offset = offset;
        }

        int offset() {
            return offset;
        }
    }

    private final String source;
    private final int prefix;
    private final String root;
    private final List<String> accessors;
    private final Set<String> found = new HashSet<>();
    private final CSSWriterSettings compact = new CSSWriterSettings(true);

    private CssCompiler(String source, int prefix, String root, List<String> accessors) {
        this.source = source;
        this.prefix = prefix;
        this.root = root;
        this.accessors = accessors;
    }

    static String component(String css, String root, List<String> accessors) {
        String prefix = "." + root + " {\n";
        var compiler = new CssCompiler(prefix + css + "\n}", prefix.length(), root, accessors);
        var sheet = compiler.parse(true);
        if (1 != sheet.getRuleCount() || !(sheet.getRuleAtIndex(0) instanceof CSSStyleRule style)) {
            throw new InvalidCss("Component CSS must remain inside its implicit root", 0);
        }
        String result = compiler.style(style, "." + root, true);
        for (var name : accessors) {
            if (!"style".equals(name) && !compiler.found.contains(name)) {
                throw new InvalidCss("Accessor " + name + "() has no matching local selector", 0);
            }
        }
        return result;
    }

    static String bundle(List<String> shared, List<String> fragments) {
        var sharedStyles = new StringBuilder();
        for (var css : shared) {
            var compiler = new CssCompiler(css, 0, "", List.of());
            var sheet = compiler.parse(false);
            sharedStyles.append(sheet.getAllRules().stream()
                    .map(rule -> rule.getAsCSSString(compiler.compact, 0))
                    .collect(Collectors.joining()));
        }
        var result = new StringBuilder("@layer shared,components;");
        if (!sharedStyles.isEmpty()) {
            result.append("@layer shared{").append(sharedStyles).append('}');
        }
        String components = String.join("", fragments);
        if (!components.isEmpty()) {
            result.append("@layer components{").append(components).append('}');
        }
        return result.append('\n').toString();
    }

    private CascadingStyleSheet parse(boolean component) {
        var stream = new CSSCharStream(new StringReader(source));
        stream.setTabSize(1);
        var handler = new ThrowingCSSParseErrorHandler() {
            @Override
            public void onCSSUnexpectedRule(Token token, String rule, String message) {
                throw new InvalidCss(
                        "Unsupported CSS rule " + rule + ": " + message, offset(token.beginLine, token.beginColumn));
            }

            @Override
            public void onCSSDeprecatedProperty(Token prefix, Token identifier) {
                throw new InvalidCss(
                        "Deprecated CSS property " + prefix.image + identifier.image,
                        offset(prefix.beginLine, prefix.beginColumn));
            }

            @Override
            public void onIllegalCharacter(char character) {
                throw new InvalidCss("Illegal CSS character", offset(stream.getEndLine(), stream.getEndColumn()));
            }
        };
        var tokens = new ParserCSS30TokenManager(stream);
        tokens.setCustomErrorHandler(handler);
        var parser = new ParserCSS30(tokens);
        parser.setCustomErrorHandler(handler);
        parser.setBrowserCompliantMode(false);
        CSSNode tree;
        try {
            tree = parser.styleSheet();
        } catch (ParseException e) {
            var token = e.currentToken;
            if (null != token && null != token.next) {
                token = token.next;
            }
            throw new InvalidCss(
                    "Invalid CSS: "
                            + String.valueOf(e.getMessage()).lines().findFirst().orElse(""),
                    null == token ? 0 : offset(token.beginLine, token.beginColumn));
        } catch (TokenMgrException e) {
            throw new InvalidCss(
                    "Invalid CSS token or unterminated comment/string",
                    offset(stream.getEndLine(), stream.getEndColumn()));
        }
        // Model conversion can discard unsupported nested rules. Validate the syntax tree first.
        checkSyntaxTree(tree, component);
        return CSSHandler.readCascadingStyleSheetFromNode(
                new ICSSInterpretErrorHandler() {
                    @Override
                    public void onCSSInterpretationWarning(String message) {
                        throw new InvalidCss(message, 0);
                    }

                    @Override
                    public void onCSSInterpretationError(String message) {
                        throw new InvalidCss(message, 0);
                    }
                },
                true,
                tree);
    }

    private void checkSyntaxTree(CSSNode node, boolean component) {
        if (ECSSNodeType.IMPORTANT.isNode(node)) {
            throw invalid("!important declarations defeat shared/component precedence", node.getSourceLocation());
        }
        for (var type : ECSSNodeType.values()) {
            if (type.isNode(node)
                    && (type.name().endsWith("RULE") || ECSSNodeType.CHARSET == type)
                    && ECSSNodeType.STYLERULE != type
                    && ECSSNodeType.MEDIARULE != type
                    && ECSSNodeType.SUPPORTSRULE != type) {
                throw invalid(
                        (component ? "Unsupported component rule: " : "Unsupported shared rule: ") + type.name(),
                        node.getSourceLocation());
            }
        }
        for (var child : node) {
            checkSyntaxTree(child, component);
        }
    }

    private String style(CSSStyleRule rule, String parent, boolean implicit) {
        String selector = implicit
                ? parent
                : rule.getAllSelectors().stream()
                        .map(s -> selector(s, parent, true))
                        .collect(Collectors.joining(","));
        var output = new StringBuilder(declarations(rule.getAllDeclarations(), selector));
        String nestedParent = rule.getSelectorCount() > 1 ? ":is(" + selector + ")" : selector;
        for (var nested : rule.getAllRules()) {
            if (nested instanceof CSSStyleRule child) {
                output.append(style(child, nestedParent, false));
            } else if (nested instanceof CSSNestedDeclarations declarations) {
                output.append(declarations(declarations.getAllDeclarations(), selector));
            } else if (nested instanceof CSSMediaRule media) {
                output.append(conditional(media, nestedParent));
            } else if (nested instanceof CSSSupportsRule supports) {
                output.append(conditional(supports, nestedParent));
            } else {
                throw invalid("Unsupported component rule", (ICSSSourceLocationAware) nested);
            }
        }
        return output.toString();
    }

    private String conditional(AbstractHasTopLevelRules rule, String parent) {
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
        var result = new StringBuilder(header + "{");
        for (var child : rule.getAllRules()) {
            if (child instanceof CSSStyleRule style) {
                result.append(style(style, parent, false));
            } else if (child instanceof CSSMediaRule media) {
                result.append(conditional(media, parent));
            } else if (child instanceof CSSSupportsRule supports) {
                result.append(conditional(supports, parent));
            } else {
                throw invalid("Unsupported component rule", (ICSSSourceLocationAware) child);
            }
        }
        return result.append('}').toString();
    }

    private String declarations(List<CSSDeclaration> declarations, String selector) {
        if (declarations.isEmpty()) {
            return "";
        }
        var result = new StringBuilder(selector + "{");
        for (var declaration : declarations) {
            if (Set.of("animation", "animation-name")
                    .contains(declaration.getProperty().toLowerCase(Locale.ROOT))) {
                throw invalid("Component keyframe bindings are outside this proof", declaration);
            }
            result.append(declaration.getAsCSSString(compact, 0)).append(';');
        }
        return result.append('}').toString();
    }

    private String selector(CSSSelector selector, String parent, boolean nested) {
        var result = new StringBuilder();
        int index = 0;
        for (var member : selector.getAllMembers()) {
            if (nested && 0 == index && !(member instanceof CSSSelectorSimpleMember simple && simple.isNesting())) {
                throw invalid("Component selectors must start with &", selector);
            }
            if (member instanceof CSSSelectorSimpleMember simple) {
                String value = simple.getValue();
                if (value.contains("\\")) {
                    throw invalid("Escaped selector identifiers are outside this proof", simple);
                }
                if (simple.isNesting() && nested && 0 == index) {
                    result.append(parent);
                } else if (simple.isClass()) {
                    String name = value.substring(1);
                    if ("style".equals(name) || !accessors.contains(name)) {
                        throw invalid("Local selector ." + name + " has no matching modifier accessor", simple);
                    }
                    found.add(name);
                    result.append('.').append(root).append("--").append(Names.encode(name));
                } else if (simple.isPseudo()
                        && !Set.of(":global", ":local", ":root", ":scope", ":host")
                                .contains(value.toLowerCase(Locale.ROOT))) {
                    result.append(value);
                } else {
                    throw invalid("Component selectors must target the same owned element", simple);
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
                throw invalid(
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

    private InvalidCss invalid(String message, ICSSSourceLocationAware node) {
        return invalid(message, node.getSourceLocation());
    }

    private InvalidCss invalid(String message, @Nullable CSSSourceLocation location) {
        return new InvalidCss(
                message,
                null == location
                        ? 0
                        : offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber()));
    }

    private int offset(int line, int column) {
        int offset = 0;
        for (int i = 1; i < line && offset < source.length(); i++) {
            int next = source.indexOf('\n', offset);
            offset = next < 0 ? source.length() : next + 1;
        }
        return Math.max(0, Math.min(source.length() - prefix, offset + column - 1 - prefix));
    }
}
