package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Custom-property targets in transition values, including variable fallbacks. */
public final class CssPropertyValues {
    private CssPropertyValues() {}

    public static List<CSSExpressionMemberTermSimple> names(CSSDeclaration declaration) {
        String property = CSSParseHelper.unescapeURL(declaration.getProperty());
        if (!"transition".equalsIgnoreCase(property) && !"transition-property".equalsIgnoreCase(property)) {
            return List.of();
        }
        var names = new ArrayList<CSSExpressionMemberTermSimple>();
        names(declaration.getExpression(), 0, names);
        return List.copyOf(names);
    }

    private static void names(CSSExpression expression, int first, List<CSSExpressionMemberTermSimple> names) {
        for (int index = first; index < expression.getMemberCount(); index++) {
            var member = expression.getMemberAtIndex(index);
            if (member instanceof CSSExpressionMemberTermSimple term
                    && CSSParseHelper.unescapeURL(term.getValue()).startsWith("--")) {
                names.add(term);
            } else if (member instanceof CSSExpressionMemberFunction function
                    && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                    && null != function.getExpression()) {
                // The first argument names the variable; only its fallback supplies transition targets.
                names(function.getExpression(), 2, names);
            }
        }
    }

    public static List<CSSExpressionMemberTermSimple> carriedNames(CascadingStyleSheet sheet) {
        var variables = new HashMap<String, List<CSSExpression>>();
        var transitions = new ArrayList<CSSDeclaration>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                String property = CSSParseHelper.unescapeURL(declaration.getProperty());
                if (property.startsWith("--")) {
                    variables
                            .computeIfAbsent(property, ignored -> new ArrayList<>())
                            .add(declaration.getExpression());
                } else if ("transition".equalsIgnoreCase(property)
                        || "transition-property".equalsIgnoreCase(property)) {
                    transitions.add(declaration);
                }
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                for (var descriptor : rule.getAllDeclarations()) {
                    if ("initial-value".equalsIgnoreCase(descriptor.getDescriptor())) {
                        variables
                                .computeIfAbsent(
                                        CSSParseHelper.unescapeURL(rule.getIdentifier()), ignored -> new ArrayList<>())
                                .add(descriptor.getExpression());
                    }
                }
            }
        });
        var targets = new ArrayList<CSSExpressionMemberTermSimple>();
        for (var transition : transitions) {
            carriedNames(transition.getExpression(), 0, false, variables, new HashSet<>(), targets);
        }
        return List.copyOf(targets);
    }

    private static void carriedNames(
            CSSExpression expression,
            int first,
            boolean definition,
            Map<String, List<CSSExpression>> variables,
            Set<String> visiting,
            List<CSSExpressionMemberTermSimple> targets) {
        for (int index = first; index < expression.getMemberCount(); index++) {
            var member = expression.getMemberAtIndex(index);
            if (definition && member instanceof CSSExpressionMemberTermSimple term) {
                if (CSSParseHelper.unescapeURL(term.getValue()).startsWith("--")) {
                    targets.add(term);
                }
            } else if (member instanceof CSSExpressionMemberFunction function
                    && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                    && null != function.getExpression()
                    && 0 < function.getExpression().getMemberCount()
                    && function.getExpression().getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple target) {
                String property = CSSParseHelper.unescapeURL(target.getValue());
                if (visiting.add(property)) {
                    for (var value : variables.getOrDefault(property, List.of())) {
                        carriedNames(value, 0, true, variables, visiting, targets);
                    }
                }
                carriedNames(function.getExpression(), 2, definition, variables, visiting, targets);
            }
        }
    }
}
