package react4j.css.processor;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import react4j.css.internal.CssExpressions;
import react4j.css.internal.CssPropertyValues;
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
        var sheet = Kind.KEYFRAMES == kind
                ? syntax.parseKeyframes()
                : Kind.UTILITY == kind ? syntax.parse(CssSyntax.Mode.UTILITY) : syntax.parse(Kind.COMPONENT == kind);
        return rewrite(sheet, syntax, name -> "--" + name, Kind.UTILITY == kind || Kind.GLOBAL == kind);
    }

    static Set<String> rewrite(CascadingStyleSheet sheet, CssSyntax syntax, UnaryOperator<String> resolve) {
        return rewrite(sheet, syntax, resolve, false);
    }

    static Set<String> rewrite(
            CascadingStyleSheet sheet, CssSyntax syntax, UnaryOperator<String> resolve, boolean modern) {
        var names = new LinkedHashSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private String property(String token, ICSSSourceLocationAware declaration) {
                String name = (modern ? CSSParseHelper.unescapeURL(token) : token).substring(2);
                try {
                    checkName(name);
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                }
                names.add(name);
                return resolve.apply(name);
            }

            private void expression(CSSExpression expression, ICSSSourceLocationAware declaration) {
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
                                    || !(modern ? CSSParseHelper.unescapeURL(first.getValue()) : first.getValue())
                                            .startsWith("--")
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
                if ((modern ? CSSParseHelper.unescapeURL(declaration.getProperty()) : declaration.getProperty())
                        .startsWith("--")) {
                    declaration.setProperty(property(declaration.getProperty(), declaration));
                }
                for (var target : CssPropertyValues.names(declaration)) {
                    target.setValue(property(target.getValue(), declaration));
                }
                expression(declaration.getExpression(), declaration);
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                property(rule.getIdentifier(), rule);
                for (var descriptor : rule.getAllDeclarations()) {
                    expression(descriptor.getExpression(), descriptor);
                }
            }

            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                var header = syntax.condition(rule);
                var parameters = new StringBuilder(header.parameters());
                for (int i = header.references().size() - 1; i >= 0; i--) {
                    var reference = header.references().get(i);
                    if (reference.property()) {
                        parameters.replace(
                                reference.start() - header.parameterStart(),
                                reference.end() - header.parameterStart(),
                                property(reference.value(), rule));
                    }
                }
                String text = parameters.toString().strip();
                rule.setParameterList(text.isEmpty() ? null : text);
            }

            @Override
            public void onBeginSupportsRule(CSSSupportsRule rule) {
                CssExpressions.supports(rule.getAllSupportConditionMembers(), this::onDeclaration);
            }
        });
        return Set.copyOf(names);
    }
}
