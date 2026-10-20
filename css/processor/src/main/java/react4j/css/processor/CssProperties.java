package react4j.css.processor;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import react4j.css.internal.CssExpressions;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;

/** Finds and rewrites property symbols in the parsed model, including support conditions. */
final class CssProperties {
    private CssProperties() {}

    record Binding(String owner, String name, Kind kind, boolean stable) {
        String cssName() {
            return Kind.GLOBAL == kind && stable ? "--" + name : "--" + Names.root(owner) + "--" + Names.encode(name);
        }
    }

    record Import(String provider, Binding binding) {}

    static void checkName(String name) {
        CssCompiler.checkAlias(name, Kind.COMPONENT);
        if (name.startsWith("--")) {
            throw new IllegalArgumentException("Custom property names must omit --: " + name);
        }
    }

    static Set<String> names(String css, Kind kind) {
        String prefix = Kind.COMPONENT == kind ? ".root {\n" : Kind.KEYFRAMES == kind ? "@keyframes shared {\n" : "";
        var syntax = new CssSyntax(prefix + css + (prefix.isEmpty() ? "" : "\n}"), prefix.length());
        var sheet = Kind.KEYFRAMES == kind ? syntax.parseKeyframes() : syntax.parse(Kind.COMPONENT == kind);
        return rewrite(sheet, syntax, name -> "--" + name);
    }

    static Set<String> rewrite(CascadingStyleSheet sheet, CssSyntax syntax, UnaryOperator<String> resolve) {
        var names = new LinkedHashSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private String property(String token, CSSDeclaration declaration) {
                String name = token.substring(2);
                try {
                    checkName(name);
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                }
                names.add(name);
                return resolve.apply(name);
            }

            private void expression(CSSExpression expression, CSSDeclaration declaration) {
                CssExpressions.visit(expression, member -> {
                    if (member instanceof CSSExpressionMemberFunction function) {
                        var nested = function.getExpression();
                        if ("var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))) {
                            if (null == nested) {
                                throw syntax.invalid("var() requires a custom property name", declaration);
                            }
                            var arguments = nested.getAllMembers();
                            if (arguments.isEmpty()
                                    || !(arguments.get(0) instanceof CSSExpressionMemberTermSimple first)
                                    || !first.getValue().startsWith("--")
                                    || (arguments.size() > 1 && ECSSExpressionOperator.COMMA != arguments.get(1))) {
                                throw syntax.invalid(
                                        "var() requires an unquoted custom property name and optional fallback",
                                        declaration);
                            }
                            first.setValue(property(first.getValue(), declaration));
                        }
                    }
                });
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                if (declaration.getProperty().startsWith("--")) {
                    declaration.setProperty(property(declaration.getProperty(), declaration));
                }
                expression(declaration.getExpression(), declaration);
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }
        });
        return Set.copyOf(names);
    }
}
