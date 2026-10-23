package react4j.css.internal;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSExpressionMemberTermURI;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.writer.CSSWriterSettings;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/** URL-bearing expression nodes, including the string form of image-set sources. */
public final class CssUrls {
    private CssUrls() {}

    public record Reference(String value, ICSSSourceLocationAware node, UnaryOperator<String> replace) {}

    public static void visit(CascadingStyleSheet sheet, Consumer<Reference> visitor) {
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private void expression(CSSExpression expression) {
                for (var member : expression.getAllMembers()) {
                    if (member instanceof CSSExpressionMemberTermURI uri) {
                        visitor.accept(new Reference(uri.getURIString(), uri, value -> {
                            uri.setURIString(value);
                            return uri.getAsCSSString(new CSSWriterSettings(true), 0);
                        }));
                    } else if (member instanceof CSSExpressionMemberFunction function) {
                        String name = CSSParseHelper.unescapeURL(function.getFunctionName());
                        var nested = function.getExpression();
                        if ("url".equalsIgnoreCase(name)) {
                            if (null == nested
                                    || 1 != nested.getMemberCount()
                                    || !(nested.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple term)) {
                                throw new IllegalArgumentException("CSS url() requires a static URL");
                            }
                            visitor.accept(new Reference(text(term), function, value -> {
                                term.setValue(CSSExpression.getQuotedStringValue(value));
                                return function.getAsCSSString(new CSSWriterSettings(true), 0);
                            }));
                        } else if (null != nested) {
                            if ("image-set".equalsIgnoreCase(name) || "-webkit-image-set".equalsIgnoreCase(name)) {
                                for (var item : nested.getAllMembers()) {
                                    if (item instanceof CSSExpressionMemberTermSimple term && term.isStringLiteral()) {
                                        visitor.accept(new Reference(text(term), term, value -> {
                                            term.setValue(CSSExpression.getQuotedStringValue(value));
                                            return term.getValue();
                                        }));
                                    }
                                }
                            }
                            expression(nested);
                        }
                    }
                }
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                expression(declaration.getExpression());
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                for (var descriptor : rule.getAllDeclarations()) {
                    expression(descriptor.getExpression());
                }
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }
        });
    }

    private static String text(CSSExpressionMemberTermSimple term) {
        String text = term.getValue();
        return CSSParseHelper.unescapeURL(
                term.isStringLiteral() ? Objects.requireNonNull(CSSParseHelper.extractStringValue(text)) : text);
    }

    public static List<SourceMap.Edit> edits(String css, UnaryOperator<String> resolve) {
        var syntax = new CssSyntax(css, 0);
        var sheet = syntax.parse(CssSyntax.Mode.PROCESSED);
        var edits = new ArrayList<SourceMap.Edit>();
        visit(sheet, reference -> {
            String value = resolve.apply(reference.value());
            if (!value.equals(reference.value())) {
                var location = Objects.requireNonNull(reference.node().getSourceLocation());
                edits.add(new SourceMap.Edit(
                        syntax.offset(
                                location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber()),
                        syntax.offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1,
                        reference.replace().apply(value)));
            }
        });
        return edits.stream()
                .sorted(Comparator.comparingInt(SourceMap.Edit::start))
                .toList();
    }

    /** Empty URLs and bare local paths are rejected; fragments and explicit external schemes remain browser-owned. */
    public static String checked(String value, BiFunction<String, String, String> managed) {
        if (value.chars().anyMatch(c -> Character.isISOControl(c) || '\\' == c)) {
            throw new IllegalArgumentException("Unsafe CSS URL: " + value);
        }
        if (value.startsWith("asset:")) {
            int suffix = value.length();
            for (char c : new char[] {'?', '#'}) {
                int index = value.indexOf(c, 6);
                if (index >= 0) {
                    suffix = Math.min(suffix, index);
                }
            }
            return managed.apply(value.substring(6, suffix), value.substring(suffix));
        }
        if (value.startsWith("#") || value.startsWith("//") || value.matches("(?is)(?:https?|data|blob):.*")) {
            return value;
        }
        throw new IllegalArgumentException("Local CSS URLs require a checked asset import: " + value);
    }
}
