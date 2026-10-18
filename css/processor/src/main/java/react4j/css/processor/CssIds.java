package react4j.css.processor;

import com.helger.css.decl.CSSDeclaration;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import react4j.css.internal.CssExpressions;
import react4j.css.internal.CssSyntax;

/** Rewrites explicitly bound ID selectors and same-document fragments. */
final class CssIds {
    private CssIds() {}

    private static String asciiLower(String value) {
        var result = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            result.append(c >= 'A' && c <= 'Z' ? (char) (c + 'a' - 'A') : c);
        }
        return result.toString();
    }

    static Map<String, String> rewrite(CascadingStyleSheet sheet, CssSyntax syntax, String root, Set<String> bindings) {
        var found = new LinkedHashMap<String, String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private String resolve(String name) {
                String resolved = root + "--id-" + Names.encode(name);
                found.put(name, resolved);
                return resolved;
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                CssExpressions.visit(declaration.getExpression(), member -> {
                    if (member instanceof CSSExpressionMemberTermURI uri) {
                        String value = uri.getURIString();
                        if (value.startsWith("#") && bindings.contains(value.substring(1))) {
                            uri.setURIString("#" + resolve(value.substring(1)));
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
                boolean uri = Set.of("href", "xlink:href", "src").contains(name);
                boolean id = Set.of(
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
                if ((!uri && !id) || bindings.isEmpty()) {
                    return;
                }
                if (value.contains("\\")) {
                    throw syntax.invalid("Escaped ID attribute references are unsupported when binding IDs", attribute);
                }
                String authored = ECSSAttributeCase.CASE_INSENSITIVE == attribute.getCaseSensitivityFlag()
                        ? asciiLower(value)
                        : value;
                boolean reference = bindings.stream().anyMatch(binding -> {
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
                        if (bindings.contains(name)) {
                            resolve(name);
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
                                    && bindings.contains(simple.getValue().substring(1))) {
                                throw syntax.invalid("Bound IDs are unsupported in this selector function", function);
                            }
                        });
                    }
                }
            }
        });
        return Map.copyOf(found);
    }
}
