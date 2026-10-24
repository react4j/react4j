package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSExpressionMemberTermURI;
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
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSAttributeCase;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import react4j.css.internal.CssResource.Kind;

/** Rewrites explicitly bound ID selectors and same-document fragments. */
public final class CssIds {
    private CssIds() {}

    public record Binding(String owner, String name, Kind kind, boolean stable) {
        public String cssName() {
            return Kind.GLOBAL == kind && stable ? name : CssIdentity.root(owner) + "--id-" + CssIdentity.encode(name);
        }
    }

    public record Import(List<String> providers, Binding binding) {
        public Import {
            providers = List.copyOf(providers);
        }
    }

    public static Set<String> names(String css, Kind kind, Set<String> bindings) {
        String prefix = Kind.COMPONENT == kind ? ".root {\n" : Kind.KEYFRAMES == kind ? "@keyframes shared {\n" : "";
        var syntax = new CssSyntax(prefix + css + (prefix.isEmpty() ? "" : "\n}"), prefix.length());
        var sheet = Kind.KEYFRAMES == kind
                ? syntax.parseKeyframes()
                : syntax.parse(
                        Kind.UTILITY == kind
                                ? CssSyntax.Mode.UTILITY
                                : Kind.COMPONENT == kind ? CssSyntax.Mode.COMPONENT : CssSyntax.Mode.GLOBAL);
        var names = new LinkedHashMap<String, String>();
        bindings.forEach(name -> names.put(name, name));
        return rewrite(sheet, syntax, names, false).keySet();
    }

    private static String asciiLower(String value) {
        var result = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            result.append(c >= 'A' && c <= 'Z' ? (char) (c + 'a' - 'A') : c);
        }
        return result.toString();
    }

    public static Map<String, String> rewrite(
            CascadingStyleSheet sheet, CssSyntax syntax, Map<String, String> bindings, boolean global) {
        var found = new LinkedHashMap<String, String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private String resolve(String name) {
                String resolved = Objects.requireNonNull(bindings.get(name));
                found.put(name, resolved);
                return resolved;
            }

            private String fragment(String value, CSSDeclaration declaration) {
                if (!value.startsWith("#")) {
                    return value;
                }
                String name;
                try {
                    name = CssTokens.fragmentName(value);
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                }
                return bindings.containsKey(name) ? "#" + resolve(name) : value;
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                CssExpressions.visit(declaration.getExpression(), member -> {
                    if (member instanceof CSSExpressionMemberTermURI uri) {
                        uri.setURIString(fragment(uri.getURIString(), declaration));
                    } else if (member instanceof CSSExpressionMemberFunction function
                            && "url".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))) {
                        var expression = function.getExpression();
                        if (null != expression
                                && 1 == expression.getMemberCount()
                                && expression.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple first) {
                            String raw = first.getValue();
                            boolean quoted = raw.startsWith("'") || raw.startsWith("\"");
                            String value = CSSParseHelper.unescapeURL(
                                    quoted ? Objects.requireNonNull(CSSParseHelper.extractStringValue(raw)) : raw);
                            String resolved = fragment(value, declaration);
                            if (!resolved.equals(value)) {
                                first.setValue(quoted ? raw.charAt(0) + resolved + raw.charAt(0) : resolved);
                            }
                        }
                    }
                });
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }

            private void attribute(CSSSelectorAttribute attribute) {
                String name = attribute.getAttrName().toLowerCase(Locale.ROOT);
                if (name.contains("\\")) {
                    throw syntax.invalid("Escaped selector identifiers are unsupported", attribute);
                }
                String value = attribute.getAttrValue();
                if (null == value) {
                    return;
                }
                if (value.startsWith("\"") || value.startsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                }
                boolean uri = CssTokens.isUriAttribute(name);
                boolean id = CssTokens.isIdAttribute(name);
                if ((!uri && !id) || bindings.isEmpty()) {
                    return;
                }
                if (value.contains("\\")) {
                    throw syntax.invalid("Escaped ID attribute references are unsupported when binding IDs", attribute);
                }
                String authored = ECSSAttributeCase.CASE_INSENSITIVE == attribute.getCaseSensitivityFlag()
                        ? asciiLower(value)
                        : value;
                boolean reference = bindings.keySet().stream().anyMatch(binding -> {
                    String candidate = (uri ? "#" : "") + binding;
                    if (ECSSAttributeCase.CASE_INSENSITIVE == attribute.getCaseSensitivityFlag()) {
                        candidate = asciiLower(candidate);
                    }
                    if (!uri && Arrays.asList(authored.split("\\s+")).contains(candidate)) {
                        return true;
                    }
                    return switch (Objects.requireNonNull(attribute.getOperator())) {
                        case EQUALS -> candidate.equals(authored);
                        case INCLUDES -> Arrays.asList(candidate.split("\\s+")).contains(authored);
                        case DASHMATCH -> candidate.equals(authored) || candidate.startsWith(authored + "-");
                        case BEGINMATCH -> !authored.isEmpty() && candidate.startsWith(authored);
                        case ENDMATCH -> !authored.isEmpty() && candidate.endsWith(authored);
                        case CONTAINSMATCH -> !authored.isEmpty() && candidate.contains(authored);
                    };
                });
                if (reference) {
                    throw syntax.invalid("Attribute selectors referencing bound IDs are unsupported", attribute);
                }
            }

            @Override
            public void onStyleRuleSelector(CSSSelector selector) {
                for (var member : selector.getAllMembers()) {
                    if (member instanceof CSSSelector nested) {
                        onStyleRuleSelector(nested);
                    } else if (member instanceof CSSSelectorSimpleMember simple && simple.isHash()) {
                        String name = simple.getValue().substring(1);
                        if (bindings.containsKey(name)) {
                            resolve(name);
                        } else if (global) {
                            throw syntax.invalid(
                                    "Global ID selectors require @CssId or a selected ID import: #" + name, simple);
                        }
                    } else if (member instanceof CSSSelectorAttribute attribute) {
                        attribute(attribute);
                    } else if (member instanceof CSSSelectorMemberNot function) {
                        function.getAllSelectors().forEach(this::onStyleRuleSelector);
                    } else if (member instanceof CSSSelectorMemberPseudoIs function) {
                        function.getAllSelectors().forEach(this::onStyleRuleSelector);
                    } else if (member instanceof CSSSelectorMemberPseudoWhere function) {
                        function.getAllSelectors().forEach(this::onStyleRuleSelector);
                    } else if (member instanceof CSSSelectorMemberPseudoHas function) {
                        function.getAllSelectors().forEach(this::onStyleRuleSelector);
                    } else if (member instanceof CSSSelectorMemberHost function) {
                        onStyleRuleSelector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberHostContext function) {
                        onStyleRuleSelector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberSlotted function) {
                        onStyleRuleSelector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberFunctionLike function) {
                        CssExpressions.visit(function.getParameterExpression(), term -> {
                            if (term instanceof CSSExpressionMemberTermSimple simple
                                    && simple.getValue().startsWith("#")
                                    && (global
                                            || bindings.containsKey(
                                                    simple.getValue().substring(1)))) {
                                throw syntax.invalid(
                                        "ID selectors are unsupported in this selector function", function);
                            }
                        });
                    }
                }
            }
        });
        return Map.copyOf(found);
    }
}
