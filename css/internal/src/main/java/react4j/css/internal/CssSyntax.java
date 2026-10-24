package react4j.css.internal;

import com.helger.css.CSSSourceLocation;
import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
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
import java.io.StringReader;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class CssSyntax {
    public enum Mode {
        COMPONENT,
        GLOBAL,
        UTILITY,
        KEYFRAMES,
        PROCESSED
    }

    public static final class InvalidCss extends IllegalArgumentException {
        private final int offset;

        public InvalidCss(String message, int offset) {
            super(message);
            this.offset = offset;
        }

        public int offset() {
            return offset;
        }
    }

    private final String source;
    private final int prefix;

    public CssSyntax(String source, int prefix) {
        this.source = source;
        this.prefix = prefix;
    }

    public CascadingStyleSheet parse(boolean component) {
        return parse(component ? Mode.COMPONENT : Mode.GLOBAL);
    }

    public CascadingStyleSheet parseKeyframes() {
        return parse(Mode.KEYFRAMES);
    }

    public CascadingStyleSheet parse(Mode mode) {
        boolean component = Mode.COMPONENT == mode;
        boolean framesOnly = Mode.KEYFRAMES == mode;
        var stream = new CSSCharStream(new StringReader(source));
        stream.setTabSize(1);
        var handler = new ThrowingCSSParseErrorHandler() {
            @Override
            public void onCSSUnexpectedRule(Token token, String rule, String message) {
                // Check placement in the syntax tree so diagnostics identify the keyframes header.
                if ("@keyframes".equals(rule) || "@font-face".equals(rule)) {
                    return;
                }
                if (modern(mode) && ("@container".equalsIgnoreCase(rule) || "@starting-style".equalsIgnoreCase(rule))) {
                    return;
                }
                if (modern(mode) && "@property".equalsIgnoreCase(rule)) {
                    return;
                }
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
        if (component) {
            tree = liftKeyframes(tree);
        }
        if (framesOnly && (1 != tree.jjtGetNumChildren() || !ECSSNodeType.KEYFRAMESRULE.isNode(tree.jjtGetChild(0)))) {
            throw new InvalidCss("@Keyframes requires a single animation frame body", 0);
        }
        checkSyntaxTree(tree, mode, 0, false);
        var sheet = CSSHandler.readCascadingStyleSheetFromNode(
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
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                if (null == rule.getParsedRules()) {
                    var header = CssConditions.read(source, rule);
                    var body = new StringBuilder(source);
                    for (int i = 0; i < body.length(); i++) {
                        if ((i < header.bodyStart() || i >= header.bodyEnd())
                                && '\n' != body.charAt(i)
                                && '\r' != body.charAt(i)) {
                            body.setCharAt(i, ' ');
                        }
                    }
                    rule.setParsedRules(new CssSyntax(body.toString(), prefix).parse(mode));
                }
            }

            @Override
            public void onBeginStyleRule(CSSStyleRule rule) {
                if (Mode.UTILITY == mode && rule.hasRules()) {
                    throw invalid("Utility CSS must be flattened before compilation", rule);
                }
            }
        });
        return sheet;
    }

    // ph-css omits keyframes in style rules during model conversion. Lift parsed nodes without losing token locations.
    private CSSNode liftKeyframes(CSSNode tree) {
        if (1 != tree.jjtGetNumChildren() || !ECSSNodeType.STYLERULE.isNode(tree.jjtGetChild(0))) {
            throw new InvalidCss("Component CSS must remain inside its implicit root", 0);
        }
        var result = shell(tree);
        var owner = tree.jjtGetChild(0);
        var style = shell(owner);
        append(result, style);
        for (var child : owner) {
            if (ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED.isNode(child)) {
                var declarations = shell(child);
                append(style, declarations);
                for (var declaration : child) {
                    append(
                            (ECSSNodeType.KEYFRAMESRULE.isNode(declaration)
                                            || ECSSNodeType.FONTFACERULE.isNode(declaration))
                                    ? result
                                    : declarations,
                            declaration);
                }
            } else {
                append(style, child);
            }
        }
        return result;
    }

    private static CSSNode shell(CSSNode node) {
        var result = new CSSNode(node.getId());
        result.jjtSetFirstToken(Objects.requireNonNull(node.jjtGetFirstToken()));
        result.jjtSetLastToken(Objects.requireNonNull(node.jjtGetLastToken()));
        return result;
    }

    private static void append(CSSNode parent, CSSNode child) {
        parent.jjtAddChild(child, parent.jjtGetNumChildren());
        child.jjtSetParent(parent);
    }

    private static boolean modern(Mode mode) {
        return Mode.GLOBAL == mode || Mode.UTILITY == mode || Mode.PROCESSED == mode;
    }

    private void checkSyntaxTree(CSSNode node, Mode mode, int depth, boolean inStyle) {
        boolean component = Mode.COMPONENT == mode || Mode.KEYFRAMES == mode;
        if (ECSSNodeType.IMPORTANT.isNode(node) && (component || Mode.UTILITY == mode)) {
            throw invalid("!important declarations defeat shared/component precedence", node.getSourceLocation());
        }
        boolean frames = ECSSNodeType.KEYFRAMESRULE.isNode(node);
        if ((frames || ECSSNodeType.FONTFACERULE.isNode(node))
                && (inStyle || component && 1 != depth || frames && Mode.GLOBAL == mode)) {
            throw invalid(
                    ECSSNodeType.KEYFRAMESRULE.isNode(node)
                            ? "Keyframes must be declared at the component's outer level"
                            : "Font faces must be declared at the outer level",
                    node.getSourceLocation());
        }
        for (var type : ECSSNodeType.values()) {
            if (type.isNode(node)
                    && (type.name().endsWith("RULE") || ECSSNodeType.CHARSET == type)
                    && ECSSNodeType.STYLERULE != type
                    && ECSSNodeType.MEDIARULE != type
                    && ECSSNodeType.SUPPORTSRULE != type
                    && !((component || Mode.UTILITY == mode || Mode.PROCESSED == mode)
                            && ECSSNodeType.KEYFRAMESRULE == type)
                    && !(modern(mode) && (ECSSNodeType.LAYERRULE == type || ECSSNodeType.PROPERTYRULE == type))
                    && !(modern(mode)
                            && ECSSNodeType.UNKNOWNRULE == type
                            && ("@container".equalsIgnoreCase(node.getText())
                                    || "@starting-style".equalsIgnoreCase(node.getText())))
                    && ECSSNodeType.FONTFACERULE != type) {
                throw invalid(
                        (component ? "Unsupported component rule: " : "Unsupported shared rule: ") + type.name(),
                        node.getSourceLocation());
            }
        }
        for (var child : node) {
            checkSyntaxTree(child, mode, depth + 1, inStyle || ECSSNodeType.STYLERULE.isNode(node));
        }
    }

    public CssConditions.Header condition(CSSUnknownRule rule) {
        return CssConditions.read(source, rule);
    }

    public InvalidCss invalid(String message, ICSSSourceLocationAware node) {
        return invalid(message, node.getSourceLocation());
    }

    private InvalidCss invalid(String message, @Nullable CSSSourceLocation location) {
        return new InvalidCss(
                message,
                null == location
                        ? 0
                        : offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber()));
    }

    public int offset(int line, int column) {
        int offset = 0;
        for (int i = 1; i < line && offset < source.length(); i++) {
            int next = source.indexOf('\n', offset);
            offset = next < 0 ? source.length() : next + 1;
        }
        return Math.max(0, Math.min(source.length() - prefix, offset + column - 1 - prefix));
    }
}
