package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSFontFaceRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.ICSSExpressionMember;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import react4j.css.internal.CssResource.Kind;

public final class CssFonts {
    private CssFonts() {}

    public record Import(String owner, String name, boolean stable) {
        public String cssName() {
            return CssIdentity.root(owner) + "--font-" + CssIdentity.encode(name);
        }
    }

    public static Set<String> names(String css, Kind kind) {
        String prefix = Kind.COMPONENT == kind ? ".root {\n" : Kind.KEYFRAMES == kind ? "@keyframes shared {\n" : "";
        var syntax = new CssSyntax(prefix + css + (prefix.isEmpty() ? "" : "\n}"), prefix.length());
        var sheet = Kind.KEYFRAMES == kind
                ? syntax.parseKeyframes()
                : syntax.parse(
                        Kind.UTILITY == kind
                                ? CssSyntax.Mode.UTILITY
                                : Kind.COMPONENT == kind ? CssSyntax.Mode.COMPONENT : CssSyntax.Mode.GLOBAL);
        var names = new TreeSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onBeginFontFaceRule(CSSFontFaceRule face) {
                names.add(CssFontValues.face(face));
            }
        });
        return Set.copyOf(names);
    }

    public static Map<String, String> rewrite(
            CascadingStyleSheet sheet, CssSyntax syntax, String root, Map<String, Import> imports) {
        var names = new LinkedHashMap<String, String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onBeginFontFaceRule(CSSFontFaceRule face) {
                try {
                    String name = CssFontValues.face(face);
                    if (imports.containsKey(name)) {
                        throw new IllegalArgumentException("Owned font conflicts with imported family: " + name);
                    }
                    names.put(name, root + "--font-" + CssIdentity.encode(name));
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), face);
                }
            }
        });
        var managed = new HashSet<>(names.keySet());
        managed.addAll(imports.keySet());
        var variables = new LinkedHashMap<String, List<CSSExpression>>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                if (declaration.getProperty().startsWith("--")) {
                    var definitions = variables.computeIfAbsent(declaration.getProperty(), name -> new ArrayList<>());
                    if (!definitions.contains(declaration.getExpression())) {
                        definitions.add(declaration.getExpression());
                    }
                }
            }
        });
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                try {
                    var value = CssFontValues.read(declaration);
                    if (value.dynamic()) {
                        imports.keySet()
                                .forEach(name -> names.putIfAbsent(name, root + "--font-" + CssIdentity.encode(name)));
                    }
                    // Replace right-to-left because a multiword family collapses to one identifier.
                    var families = new ArrayList<>(value.families());
                    Collections.reverse(families);
                    for (var family : families) {
                        String name = family.name();
                        if (imports.containsKey(name)) {
                            names.putIfAbsent(name, root + "--font-" + CssIdentity.encode(name));
                        }
                        String resolved = names.get(name);
                        if (null != resolved) {
                            family.replace(resolved);
                        }
                    }
                    if (value.dynamic() && !managed.isEmpty()) {
                        checkVariables(declaration, managed, variables);
                    }
                } catch (IllegalArgumentException e) {
                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                }
            }
        });
        return Map.copyOf(names);
    }

    private static void checkVariables(
            CSSDeclaration declaration, Set<String> managed, Map<String, List<CSSExpression>> variables) {
        expand(declaration.getExpression().getAllMembers(), List.of(), variables, Set.of(), expression -> {
            CssFontValues.Value value;
            try {
                value = CssFontValues.read(new CSSDeclaration(declaration.getProperty(), expression));
            } catch (IllegalArgumentException e) {
                // Definitions from unrelated scopes can form invalid computed values. They do not select a font.
                return;
            }
            var families = value.families();
            if (value.dynamic() && "font".equalsIgnoreCase(CSSParseHelper.unescapeURL(declaration.getProperty()))) {
                var members = expression.getAllMembers();
                for (int first = 1; first < members.size(); first++) {
                    var suffix = new CSSExpression();
                    members.subList(first, members.size()).forEach(suffix::addMember);
                    try {
                        // The longest valid family suffix excludes weight/size/line-height fragments.
                        families = CssFontValues.read(new CSSDeclaration("font-family", suffix))
                                .families();
                        break;
                    } catch (IllegalArgumentException e) {
                        // This suffix still includes shorthand grammar before the family list.
                    }
                }
            }
            if (families.stream().anyMatch(family -> managed.contains(family.name()))) {
                throw new IllegalArgumentException(
                        "Managed font families in custom-property values require a font-family fallback instead");
            }
        });
    }

    // Substitute into the complete declaration: a variable can supply a weight, size, line-height or family.
    // Keep every scoped definition and break dependency cycles without mutating the emitted syntax tree.
    public static void expand(
            List<ICSSExpressionMember> remaining,
            List<ICSSExpressionMember> prefix,
            Map<String, List<CSSExpression>> variables,
            Set<String> visiting,
            Consumer<CSSExpression> consumer) {
        if (remaining.isEmpty()) {
            var expression = new CSSExpression();
            prefix.forEach(expression::addMember);
            consumer.accept(expression);
            return;
        }
        var member = remaining.get(0);
        var tail = remaining.subList(1, remaining.size());
        Consumer<List<ICSSExpressionMember>> append = replacement -> {
            var combined = new ArrayList<>(prefix);
            combined.addAll(replacement);
            expand(tail, combined, variables, visiting, consumer);
        };
        if (member instanceof CSSExpressionMemberFunction function && null != function.getExpression()) {
            var arguments = function.getExpression().getAllMembers();
            if ("var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                    && !arguments.isEmpty()
                    && arguments.get(0) instanceof CSSExpressionMemberTermSimple first
                    && !visiting.contains(CSSParseHelper.unescapeURL(first.getValue()))) {
                var path = new HashSet<>(visiting);
                path.add(CSSParseHelper.unescapeURL(first.getValue()));
                var definitions = variables.getOrDefault(CSSParseHelper.unescapeURL(first.getValue()), List.of());
                for (var definition : definitions) {
                    expand(
                            definition.getAllMembers(),
                            List.of(),
                            variables,
                            path,
                            expression -> append.accept(expression.getAllMembers()));
                }
                int comma = arguments.indexOf(ECSSExpressionOperator.COMMA);
                if (comma >= 0) {
                    expand(
                            arguments.subList(comma + 1, arguments.size()),
                            List.of(),
                            variables,
                            path,
                            expression -> append.accept(expression.getAllMembers()));
                }
                if (!definitions.isEmpty() || comma >= 0) {
                    return;
                }
            }
            expand(
                    arguments,
                    List.of(),
                    variables,
                    visiting,
                    expression -> append.accept(
                            List.of(new CSSExpressionMemberFunction(function.getFunctionName(), expression))));
        } else {
            append.accept(List.of(member));
        }
    }
}
