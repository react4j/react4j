package react4j.css.processor;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import react4j.css.internal.CssExpressions;
import react4j.css.internal.CssFontValues;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;

final class CssFonts {
    private CssFonts() {}

    record Import(String owner, String name, boolean stable) {
        String cssName() {
            return Names.root(owner) + "--font-" + Names.encode(name);
        }
    }

    static Set<String> names(String css, Kind kind) {
        String prefix = Kind.COMPONENT == kind ? ".root {\n" : Kind.KEYFRAMES == kind ? "@keyframes shared {\n" : "";
        var syntax = new CssSyntax(prefix + css + (prefix.isEmpty() ? "" : "\n}"), prefix.length());
        var sheet = Kind.KEYFRAMES == kind ? syntax.parseKeyframes() : syntax.parse(Kind.COMPONENT == kind);
        var names = new TreeSet<String>();
        for (var face : sheet.getAllFontFaceRules()) {
            names.add(CssFontValues.face(face));
        }
        return Set.copyOf(names);
    }

    static Map<String, String> rewrite(
            CascadingStyleSheet sheet, CssSyntax syntax, String root, Map<String, Import> imports) {
        var names = new LinkedHashMap<String, String>();
        for (var face : sheet.getAllFontFaceRules()) {
            try {
                String name = CssFontValues.face(face);
                if (imports.containsKey(name)) {
                    throw new IllegalArgumentException("Owned font conflicts with imported family: " + name);
                }
                names.put(name, root + "--font-" + Names.encode(name));
            } catch (IllegalArgumentException e) {
                throw syntax.invalid(String.valueOf(e.getMessage()), face);
            }
        }
        var managed = new HashSet<>(names.keySet());
        managed.addAll(imports.keySet());
        var variables = new LinkedHashMap<String, CSSExpression>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                if (declaration.getProperty().startsWith("--")) {
                    variables.put(declaration.getProperty(), declaration.getExpression());
                }
            }
        });
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                try {
                    var value = CssFontValues.read(declaration);
                    if (value.dynamic()) {
                        checkVariables(
                                declaration.getExpression(),
                                declaration.getProperty(),
                                managed,
                                variables,
                                new HashSet<>());
                        imports.keySet()
                                .forEach(name -> names.putIfAbsent(name, root + "--font-" + Names.encode(name)));
                    }
                    // Replace right-to-left because a multiword family collapses to one identifier.
                    var families = new ArrayList<>(value.families());
                    Collections.reverse(families);
                    for (var family : families) {
                        String name = family.name();
                        if (imports.containsKey(name)) {
                            names.putIfAbsent(name, root + "--font-" + Names.encode(name));
                        }
                        String resolved = names.get(name);
                        if (null != resolved) {
                            family.replace(resolved);
                        }
                    }
                    if (value.dynamic()
                            && "font".equalsIgnoreCase(CSSParseHelper.unescapeURL(declaration.getProperty()))) {
                        CssExpressions.visit(declaration.getExpression(), member -> {
                            if (member instanceof CSSExpressionMemberTermSimple term
                                    && managed.contains(CssFontValues.canonical(
                                            term.isStringLiteral()
                                                    ? Objects.requireNonNull(
                                                            CSSParseHelper.extractStringValue(term.getValue()))
                                                    : term.getValue()))) {
                                throw new IllegalArgumentException(
                                        "Dynamic font shorthand with a managed family requires font-family longhand");
                            }
                        });
                    }
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                }
            }
        });
        return Map.copyOf(names);
    }

    private static void checkVariables(
            CSSExpression expression,
            String property,
            Set<String> managed,
            Map<String, CSSExpression> variables,
            Set<String> visiting) {
        CssExpressions.visit(expression, member -> {
            if (member instanceof CSSExpressionMemberFunction function
                    && "var".equalsIgnoreCase(function.getFunctionName())) {
                var arguments = function.getExpression();
                if (null != arguments
                        && arguments.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple first
                        && visiting.add(first.getValue())) {
                    var definition = variables.get(first.getValue());
                    if (null != definition) {
                        var value = CssFontValues.read(new CSSDeclaration(property, definition));
                        if (value.families().stream().anyMatch(family -> managed.contains(family.name()))) {
                            throw new IllegalArgumentException(
                                    "Managed font families in custom-property values require a font-family fallback"
                                            + " instead");
                        }
                        checkVariables(definition, property, managed, variables, visiting);
                    }
                }
            }
        });
    }
}
