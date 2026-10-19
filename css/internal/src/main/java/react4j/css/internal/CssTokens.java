package react4j.css.internal;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSExpressionMemberTermURI;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberFunctionLike;
import com.helger.css.decl.CSSSelectorMemberHost;
import com.helger.css.decl.CSSSelectorMemberHostContext;
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorMemberSlotted;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import react4j.css.internal.CssResource.SymbolKind;

/** Typed identifier occurrences in generated or shared CSS, using UTF-16 source offsets. */
public final class CssTokens {
    private CssTokens() {}

    public record Token(SymbolKind kind, int start, int end, String value, String name) {}

    public static String fragmentName(String uri) {
        try {
            return uri.contains("%") ? Objects.requireNonNull(URI.create(uri).getFragment()) : uri.substring(1);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid encoded same-document CSS fragment: " + uri, e);
        }
    }

    private static String name(SymbolKind kind, String value) {
        if (CssResource.SymbolKind.ANIMATION == kind && (value.startsWith("'") || value.startsWith("\""))) {
            value = Objects.requireNonNull(CSSParseHelper.extractStringValue(value));
        }
        return CSSParseHelper.unescapeURL(value);
    }

    public static List<Token> read(String css) {
        var tokens = new ArrayList<Token>();
        var syntax = new CssSyntax(css, 0);
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                css, new CSSReaderSettings().setTabSize(1).setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private int start(ICSSSourceLocationAware node) {
                var location = Objects.requireNonNull(node.getSourceLocation());
                return syntax.offset(
                        location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber());
            }

            private void token(SymbolKind kind, int offset, String value) {
                if (!css.startsWith(value, offset)) {
                    throw new IllegalArgumentException("CSS identifier differs from its parsed location: " + value);
                }
                tokens.add(new Token(kind, offset, offset + value.length(), value, name(kind, value)));
            }

            private void selector(CSSSelector selector) {
                for (var member : selector.getAllMembers()) {
                    if (member instanceof CSSSelector nested) {
                        selector(nested);
                    } else if (member instanceof CSSSelectorSimpleMember simple
                            && (simple.isClass() || simple.isHash())) {
                        token(
                                simple.isClass() ? SymbolKind.CLASS : SymbolKind.ID,
                                start(simple) + 1,
                                simple.getValue().substring(1));
                    } else if (member instanceof CSSSelectorMemberNot function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoIs function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoWhere function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoHas function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberHost function) {
                        selector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberHostContext function) {
                        selector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberSlotted function) {
                        selector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberFunctionLike function) {
                        CssExpressions.visit(function.getParameterExpression(), memberExpression -> {
                            if (memberExpression instanceof CSSExpressionMemberTermSimple simple
                                    && (simple.getValue().startsWith(".")
                                            || simple.getValue().startsWith("#"))) {
                                token(
                                        simple.getValue().startsWith(".") ? SymbolKind.CLASS : SymbolKind.ID,
                                        start(simple) + 1,
                                        simple.getValue().substring(1));
                            }
                        });
                    }
                }
            }

            @Override
            public void onStyleRuleSelector(CSSSelector selector) {
                selector(selector);
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                if (declaration.getProperty().startsWith("--")) {
                    token(SymbolKind.PROPERTY, start(declaration), declaration.getProperty());
                }
                boolean animation = "animation".equalsIgnoreCase(declaration.getProperty())
                        || "animation-name".equalsIgnoreCase(declaration.getProperty());
                if (animation) {
                    for (var member : declaration.getExpression().getAllMembers()) {
                        if (member instanceof CSSExpressionMemberTermSimple simple) {
                            token(SymbolKind.ANIMATION, start(simple), simple.getValue());
                        }
                    }
                }
                CssExpressions.visit(declaration.getExpression(), member -> {
                    if (member instanceof CSSExpressionMemberFunction function
                            && "var".equalsIgnoreCase(function.getFunctionName())) {
                        var expression = function.getExpression();
                        if (null != expression
                                && expression.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple first) {
                            token(SymbolKind.PROPERTY, start(first), first.getValue());
                        }
                    } else if (member instanceof CSSExpressionMemberTermURI uri
                            && uri.getURIString().startsWith("#")) {
                        var location = Objects.requireNonNull(uri.getSourceLocation());
                        int first = css.indexOf('(', start(uri)) + 1;
                        int last = syntax.offset(
                                        location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber())
                                + 1;
                        last = css.lastIndexOf(')', last - 1);
                        if (first < 1 || last < first) {
                            throw new IllegalArgumentException("CSS fragment differs from its parsed location");
                        }
                        while (first < last && Character.isWhitespace(css.charAt(first))) {
                            first++;
                        }
                        while (last > first && Character.isWhitespace(css.charAt(last - 1))) {
                            last--;
                        }
                        if (first < last && ('\"' == css.charAt(first) || '\'' == css.charAt(first))) {
                            first++;
                            last--;
                        }
                        if (first < last && '#' == css.charAt(first)) {
                            first++;
                        }
                        String raw = css.substring(first, last);
                        tokens.add(new Token(SymbolKind.ID, first, last, raw, fragmentName(uri.getURIString())));
                    }
                });
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }

            @Override
            public void onBeginKeyframesRule(CSSKeyframesRule rule) {
                String header = "@keyframes " + rule.getAnimationName();
                int offset = start(rule);
                if (!css.startsWith(header, offset)) {
                    throw new IllegalArgumentException("Generated keyframes header differs from its parsed location");
                }
                token(SymbolKind.ANIMATION, offset + "@keyframes ".length(), rule.getAnimationName());
            }
        });
        return tokens.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Token::start))
                .toList();
    }
}
