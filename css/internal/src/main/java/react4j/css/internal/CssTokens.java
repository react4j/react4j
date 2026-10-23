package react4j.css.internal;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSExpressionMemberTermURI;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSPropertyRule;
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
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.ECSSAttributeCase;
import com.helger.css.decl.ECSSAttributeOperator;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssResource.SymbolKind;

/** Typed identifier occurrences in generated or shared CSS, using UTF-16 source offsets. */
public final class CssTokens {
    private CssTokens() {}

    public record Token(SymbolKind kind, int start, int end, String value, String name) {}

    record Parsed(List<Token> tokens, List<Attribute> attributes) {}

    record Attribute(SymbolKind kind, ECSSAttributeOperator operator, String value, boolean insensitive, boolean uri) {
        List<String> names() {
            return uri && !value.startsWith("#")
                    ? List.of()
                    : Arrays.stream((uri ? value.substring(1) : value).split("\\s+"))
                            .filter(name -> !name.isEmpty())
                            .toList();
        }

        boolean references(String name) {
            return (ECSSAttributeOperator.EQUALS == operator || ECSSAttributeOperator.INCLUDES == operator)
                    && names().stream().anyMatch(value -> key(value).equals(key(name)));
        }

        boolean gains(String readable, String compiled) {
            return matches(compiled) && !matches(readable);
        }

        private String key(String text) {
            return insensitive ? asciiLower(text) : text;
        }

        private boolean matches(String name) {
            String candidate = key((uri ? "#" : "") + name);
            String authored = key(value);
            return switch (operator) {
                case EQUALS -> candidate.equals(authored);
                case INCLUDES -> Arrays.asList(candidate.split("\\s+")).contains(authored);
                case DASHMATCH -> candidate.equals(authored) || candidate.startsWith(authored + "-");
                case BEGINMATCH -> !authored.isEmpty() && candidate.startsWith(authored);
                case ENDMATCH -> !authored.isEmpty() && candidate.endsWith(authored);
                case CONTAINSMATCH -> !authored.isEmpty() && candidate.contains(authored);
            };
        }
    }

    static String asciiLower(String text) {
        var result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            result.append(c >= 'A' && c <= 'Z' ? (char) (c + 32) : c);
        }
        return result.toString();
    }

    public static boolean isUriAttribute(String name) {
        return Set.of("href", "xlink:href", "src").contains(name);
    }

    public static boolean isIdAttribute(String name) {
        return Set.of(
                        "id",
                        "for",
                        "headers",
                        "list",
                        "form",
                        "aria-labelledby",
                        "aria-describedby",
                        "aria-controls",
                        "aria-owns",
                        "aria-activedescendant",
                        "aria-details",
                        "aria-errormessage")
                .contains(name);
    }

    private static @Nullable Attribute attribute(CSSSelectorAttribute attribute) {
        String name = asciiLower(CSSParseHelper.unescapeURL(attribute.getAttrName()));
        String raw = attribute.getAttrValue();
        if (null == raw) {
            return null;
        }
        boolean uri = isUriAttribute(name);
        if (!"class".equals(name) && !uri && !isIdAttribute(name)) {
            return null;
        }
        String value = raw.startsWith("'") || raw.startsWith("\"")
                ? Objects.requireNonNull(CSSParseHelper.extractStringValue(raw))
                : raw;
        return new Attribute(
                "class".equals(name) ? SymbolKind.CLASS : SymbolKind.ID,
                Objects.requireNonNull(attribute.getOperator()),
                CSSParseHelper.unescapeURL(value),
                ECSSAttributeCase.CASE_INSENSITIVE == attribute.getCaseSensitivityFlag(),
                uri);
    }

    public static String fragmentName(String uri) {
        try {
            return uri.contains("%") ? Objects.requireNonNull(URI.create(uri).getFragment()) : uri.substring(1);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid encoded same-document CSS fragment: " + uri, e);
        }
    }

    private static String name(SymbolKind kind, String value) {
        if ((SymbolKind.ANIMATION == kind || SymbolKind.FONT == kind)
                && (value.startsWith("'") || value.startsWith("\""))) {
            value = Objects.requireNonNull(CSSParseHelper.extractStringValue(value));
        }
        return CSSParseHelper.unescapeURL(value);
    }

    public static List<Token> read(String css) {
        return parse(css).tokens();
    }

    static Parsed parse(String css) {
        var attributes = new ArrayList<Attribute>();
        var tokens = new ArrayList<Token>();
        var syntax = new CssSyntax(css, 0);
        var sheet = syntax.parse(CssSyntax.Mode.PROCESSED);
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
                    } else if (member instanceof CSSSelectorAttribute attribute) {
                        var reference = attribute(attribute);
                        if (null != reference) {
                            attributes.add(reference);
                        }
                        String className = CssSelectors.classToken(attribute);
                        if (null != className) {
                            var location = Objects.requireNonNull(attribute.getSourceLocation());
                            int last = syntax.offset(
                                            location.getLastTokenEndLineNumber(),
                                            location.getLastTokenEndColumnNumber())
                                    + 1;
                            boolean value = false;
                            for (var part : CssLexical.tokens(css, start(attribute), last)) {
                                if (value && !part.value().isBlank()) {
                                    String raw = part.value();
                                    int first = part.start();
                                    if (raw.startsWith("'") || raw.startsWith("\"")) {
                                        first++;
                                        raw = raw.substring(1, raw.length() - 1);
                                    }
                                    tokens.add(
                                            new Token(SymbolKind.CLASS, first, first + raw.length(), raw, className));
                                    break;
                                }
                                if ("~=".equals(part.value())) {
                                    value = true;
                                }
                            }
                        }
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

            private void fragment(int first, int last, String uri) {
                while (first < last && Character.isWhitespace(css.charAt(first))) {
                    first++;
                }
                while (last > first && Character.isWhitespace(css.charAt(last - 1))) {
                    last--;
                }
                if (first < last && ('"' == css.charAt(first) || '\'' == css.charAt(first))) {
                    first++;
                    last--;
                }
                if (first < last && '#' == css.charAt(first)) {
                    first++;
                }
                String raw = css.substring(first, last);
                tokens.add(new Token(SymbolKind.ID, first, last, raw, fragmentName(uri)));
            }

            private void expression(CSSExpression tree) {
                CssExpressions.visit(tree, member -> {
                    if (member instanceof CSSExpressionMemberFunction function
                            && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))) {
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
                        fragment(first, last, uri.getURIString());
                    } else if (member instanceof CSSExpressionMemberFunction function
                            && "url".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))) {
                        var expression = function.getExpression();
                        if (null != expression
                                && 1 == expression.getMemberCount()
                                && expression.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple first) {
                            String raw = first.getValue();
                            String value = raw.startsWith("'") || raw.startsWith("\"")
                                    ? Objects.requireNonNull(CSSParseHelper.extractStringValue(raw))
                                    : raw;
                            value = CSSParseHelper.unescapeURL(value);
                            if (value.startsWith("#")) {
                                fragment(start(first), start(first) + raw.length(), value);
                            }
                        }
                    }
                });
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                String property = CSSParseHelper.unescapeURL(declaration.getProperty());
                if (property.startsWith("--")) {
                    token(SymbolKind.PROPERTY, start(declaration), declaration.getProperty());
                }
                for (var target : CssPropertyValues.names(declaration)) {
                    token(SymbolKind.PROPERTY, start(target), target.getValue());
                }
                boolean animation =
                        "animation".equalsIgnoreCase(property) || "animation-name".equalsIgnoreCase(property);
                if (animation) {
                    for (var member : declaration.getExpression().getAllMembers()) {
                        if (member instanceof CSSExpressionMemberTermSimple simple) {
                            token(SymbolKind.ANIMATION, start(simple), simple.getValue());
                        }
                    }
                }
                for (var family : CssFontValues.read(declaration).families()) {
                    var terms = family.terms();
                    int first = start(terms.get(0));
                    var last = terms.get(terms.size() - 1);
                    int end = start(last) + last.getValue().length();
                    tokens.add(new Token(SymbolKind.FONT, first, end, css.substring(first, end), family.name()));
                }
                for (var container : CssContainers.read(declaration).names()) {
                    token(SymbolKind.CONTAINER, start(container), container.getValue());
                }
                expression(declaration.getExpression());
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                var location = Objects.requireNonNull(rule.getSourceLocation());
                int end =
                        syntax.offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1;
                boolean found = false;
                for (var identifier : CssLexical.tokens(css, start(rule), end)) {
                    if ((ParserCSS30Constants.IDENT == identifier.kind()
                                    || ParserCSS30Constants.DASHED_IDENT == identifier.kind())
                            && identifier.name().startsWith("--")) {
                        token(SymbolKind.PROPERTY, identifier.start(), identifier.value());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw syntax.invalid("Property registration requires an identifier", rule);
                }
                for (var descriptor : rule.getAllDeclarations()) {
                    expression(descriptor.getExpression());
                }
            }

            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                for (var reference : CssConditions.read(css, rule).references()) {
                    token(
                            reference.property() ? SymbolKind.PROPERTY : SymbolKind.CONTAINER,
                            reference.start(),
                            reference.value());
                }
            }

            @Override
            public void onBeginKeyframesRule(CSSKeyframesRule rule) {
                var location = Objects.requireNonNull(rule.getSourceLocation());
                int last =
                        syntax.offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1;
                var header = CssLexical.tokens(css, start(rule), last).stream()
                        .filter(token -> !token.value().isBlank())
                        .toList();
                var identifier = header.get(1);
                token(SymbolKind.ANIMATION, identifier.start(), identifier.value());
            }
        });
        return new Parsed(
                tokens.stream()
                        .distinct()
                        .sorted(Comparator.comparingInt(Token::start))
                        .toList(),
                List.copyOf(attributes));
    }
}
