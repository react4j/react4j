package react4j.css.internal;

import com.helger.css.CSSSourceLocation;
import com.helger.css.ICSSSourceLocationAware;
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
import java.io.StringReader;
import org.jspecify.annotations.Nullable;

public final class CssSyntax {
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
