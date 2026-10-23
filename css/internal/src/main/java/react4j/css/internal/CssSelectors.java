package react4j.css.internal;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorAttribute;
import com.helger.css.decl.CSSSelectorMemberHost;
import com.helger.css.decl.CSSSelectorMemberHostContext;
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorMemberSlotted;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.ECSSAttributeCase;
import com.helger.css.decl.ECSSAttributeOperator;
import com.helger.css.decl.ICSSSelectorMember;
import com.helger.css.parser.CSSParseHelper;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/** Proves a positive managed-class requirement across selector-function branches. */
public final class CssSelectors {
    private CssSelectors() {}

    public static @Nullable String classToken(CSSSelectorAttribute attribute) {
        String raw = attribute.getAttrValue();
        String namespace = attribute.getNamespacePrefix();
        if (null != namespace && !namespace.isEmpty() && !"|".equals(namespace)) {
            return null;
        }
        if (!"class".equalsIgnoreCase(CSSParseHelper.unescapeURL(attribute.getAttrName()))
                || null == raw
                || ECSSAttributeOperator.INCLUDES != attribute.getOperator()
                || ECSSAttributeCase.CASE_INSENSITIVE == attribute.getCaseSensitivityFlag()) {
            return null;
        }
        String value = CSSParseHelper.unescapeURL(
                raw.startsWith("'") || raw.startsWith("\"")
                        ? Objects.requireNonNull(CSSParseHelper.extractStringValue(raw))
                        : raw);
        return value.isEmpty() || value.codePoints().anyMatch(Character::isWhitespace) ? null : value;
    }

    public static void visit(CSSSelector selector, Consumer<ICSSSelectorMember> visitor) {
        for (var member : selector.getAllMembers()) {
            visitor.accept(member);
            if (member instanceof CSSSelector nested) {
                visit(nested, visitor);
            } else if (member instanceof CSSSelectorMemberNot function) {
                function.getAllSelectors().forEach(child -> visit(child, visitor));
            } else if (member instanceof CSSSelectorMemberPseudoIs function) {
                function.getAllSelectors().forEach(child -> visit(child, visitor));
            } else if (member instanceof CSSSelectorMemberPseudoWhere function) {
                function.getAllSelectors().forEach(child -> visit(child, visitor));
            } else if (member instanceof CSSSelectorMemberPseudoHas function) {
                function.getAllSelectors().forEach(child -> visit(child, visitor));
            } else if (member instanceof CSSSelectorMemberHost function) {
                visit(function.getSelector(), visitor);
            } else if (member instanceof CSSSelectorMemberHostContext function) {
                visit(function.getSelector(), visitor);
            } else if (member instanceof CSSSelectorMemberSlotted function) {
                visit(function.getSelector(), visitor);
            }
        }
    }

    public static boolean requiresClass(CSSSelector selector, Set<String> managed) {
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelectorSimpleMember simple
                    && simple.isClass()
                    && managed.contains(
                            CSSParseHelper.unescapeURL(simple.getValue().substring(1)))) {
                return true;
            }
            if (member instanceof CSSSelectorAttribute attribute
                    && null != classToken(attribute)
                    && managed.contains(classToken(attribute))) {
                return true;
            }
            if (member instanceof CSSSelector nested && requiresClass(nested, managed)
                    || member instanceof CSSSelectorMemberPseudoIs function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))
                    || member instanceof CSSSelectorMemberPseudoWhere function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))
                    || member instanceof CSSSelectorMemberPseudoHas function
                            && function.getAllSelectors().stream().allMatch(child -> requiresClass(child, managed))) {
                return true;
            }
        }
        return false;
    }
}
