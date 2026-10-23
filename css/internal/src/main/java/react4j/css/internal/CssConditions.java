package react4j.css.internal;

import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.parser.ParserCSS30Constants;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Source ranges and identifier references in modern conditional rule headers. */
public final class CssConditions {
    private CssConditions() {}

    public record Reference(boolean property, int start, int end, String value, String name) {}

    public record Header(int bodyStart, int bodyEnd, List<Reference> references) {}

    private static final class Context {
        final boolean style;
        final boolean variable;
        boolean value;
        boolean first = true;

        Context(boolean style, boolean variable, boolean value) {
            this.style = style;
            this.variable = variable;
            this.value = value;
        }
    }

    public static Header read(String source, CSSUnknownRule rule) {
        var location = Objects.requireNonNull(rule.getSourceLocation());
        var syntax = new CssSyntax(source, 0);
        int first = syntax.offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber())
                + rule.getDeclaration().length();
        int end = syntax.offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1;
        var references = new ArrayList<Reference>();
        var contexts = new ArrayDeque<Context>();
        boolean initial = true;
        int count = 0;
        for (var token : CssLexical.tokens(source, first, end)) {
            if ("{".equals(token.value()) && contexts.isEmpty()) {
                if ("@starting-style".equalsIgnoreCase(rule.getDeclaration()) && 0 != count) {
                    throw syntax.invalid("@starting-style takes no condition", rule);
                }
                if ("@container".equalsIgnoreCase(rule.getDeclaration()) && 0 == count) {
                    throw syntax.invalid("@container requires a condition or name", rule);
                }
                return new Header(token.start() + 1, end - 1, List.copyOf(references));
            }
            if (ParserCSS30Constants.S == token.kind()) {
                continue;
            }
            count++;
            var context = contexts.peek();
            if (ParserCSS30Constants.IDENT == token.kind()) {
                String name = token.name();
                boolean container = initial && "@container".equalsIgnoreCase(rule.getDeclaration());
                boolean property = !container
                        && name.startsWith("--")
                        && null != context
                        && (context.variable && context.first || context.style && !context.value);
                if (property || container) {
                    references.add(new Reference(property, token.start(), token.end(), token.value(), name));
                }
            }
            if (null != context) {
                context.first = false;
                if (":".equals(token.value())) {
                    context.value = true;
                }
            }
            if (token.value().endsWith("(") || "[".equals(token.value())) {
                boolean value = null != context && context.value;
                boolean style = null != context && context.style || !value && "style(".equalsIgnoreCase(token.name());
                contexts.push(new Context(style, "var(".equalsIgnoreCase(token.name()), value));
            } else if (")".equals(token.value()) || "]".equals(token.value())) {
                if (contexts.isEmpty()) {
                    throw syntax.invalid("Unbalanced conditional rule", rule);
                }
                contexts.pop();
            }
            initial = false;
        }
        throw syntax.invalid("Conditional rule requires a body", rule);
    }
}
