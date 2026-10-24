package react4j.css.internal;

import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSFontFaceRule;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Proves the meaning of original name-bearing terms without changing browser variable evaluation. */
final class UtilityNames {
    private final String css;
    private final CssSyntax syntax;
    private final String root;
    private final Set<String> externalAnimations;
    private final Set<String> externalFonts;
    private final Map<String, CssUtilityBinding.Import> imports;
    private final Map<String, CssFonts.Import> fontImports;
    private final Map<String, String> animations = new LinkedHashMap<>();
    private final Map<String, String> fonts = new LinkedHashMap<>();
    private final Map<String, List<CSSExpression>> variables = new LinkedHashMap<>();
    private final Map<CSSExpressionMemberTermSimple, CSSExpression> parents = new IdentityHashMap<>();
    private final Map<CSSExpressionMemberTermSimple, String> meanings = new IdentityHashMap<>();
    private final Map<Integer, Use> tokens = new LinkedHashMap<>();
    private final List<Boolean> dynamicFonts = new ArrayList<>();
    private final Set<String> usedImports = new HashSet<>();
    private final Set<String> seenAnimations = new HashSet<>();
    private final Set<String> seenFonts = new HashSet<>();

    record Use(CssResource.SymbolKind kind, int start, int end, String name) {}

    record Result(
            Map<String, String> animations,
            Map<String, String> fonts,
            Set<String> imports,
            List<Use> tokens,
            List<Boolean> dynamicFonts) {
        List<SourceMap.Edit> edits() {
            return tokens.stream()
                    .map(token -> new SourceMap.Edit(token.start(), token.end(), token.name()))
                    .toList();
        }
    }

    private UtilityNames(
            String css,
            CssSyntax syntax,
            String root,
            Set<String> externalAnimations,
            Set<String> externalFonts,
            Map<String, CssUtilityBinding.Import> imports,
            Map<String, CssFonts.Import> fontImports) {
        this.css = css;
        this.syntax = syntax;
        this.root = root;
        this.externalAnimations = externalAnimations;
        this.externalFonts = externalFonts;
        this.imports = imports;
        this.fontImports = fontImports;
    }

    static Result analyze(
            String css,
            CascadingStyleSheet sheet,
            CssSyntax syntax,
            String root,
            Set<String> externalAnimations,
            Set<String> externalFonts,
            Set<String> exports,
            Map<String, CssUtilityBinding.Import> imports,
            Map<String, CssFonts.Import> fontImports,
            boolean allowUnused) {
        var names = new UtilityNames(css, syntax, root, externalAnimations, externalFonts, imports, fontImports);
        names.collect(sheet);
        for (var name : exports) {
            if (externalAnimations.contains(name)) {
                throw new IllegalArgumentException("External animation cannot be exposed as an owned symbol: " + name);
            }
            var imported = imports.get(name);
            if (null != imported && CssResource.Kind.KEYFRAMES == imported.kind()) {
                names.animations.putIfAbsent(name, root + "--keyframes-" + CssIdentity.encode(name));
                names.usedImports.add(name);
            }
            if (!names.animations.containsKey(name)) {
                throw new IllegalArgumentException("Unknown animation: " + name);
            }
        }
        names.analyze(sheet);
        names.registrations(sheet);
        for (var name : externalAnimations) {
            if (!allowUnused && !names.seenAnimations.contains(name)) {
                throw new IllegalArgumentException("Unknown external animation: " + name);
            }
        }
        for (var name : externalFonts) {
            if (!allowUnused && !names.seenFonts.contains(name)) {
                throw new IllegalArgumentException("Unknown external font: " + name);
            }
        }
        return new Result(
                Map.copyOf(names.animations),
                Map.copyOf(names.fonts),
                Set.copyOf(names.usedImports),
                names.tokens.values().stream()
                        .sorted(Comparator.comparingInt(Use::start))
                        .toList(),
                List.copyOf(names.dynamicFonts));
    }

    private int start(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return syntax.offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber());
    }

    private int finish(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return syntax.offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1;
    }

    private void remember(CSSExpression expression) {
        CssExpressions.visit(expression, node -> {
            if (node instanceof CSSExpression nested) {
                for (var member : nested.getAllMembers()) {
                    if (member instanceof CSSExpressionMemberTermSimple term) {
                        parents.put(term, nested);
                    }
                }
            }
        });
    }

    private void collect(CascadingStyleSheet sheet) {
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                remember(declaration.getExpression());
                String property = CSSParseHelper.unescapeURL(declaration.getProperty());
                if (property.startsWith("--")) {
                    variables
                            .computeIfAbsent(property, ignored -> new ArrayList<>())
                            .add(declaration.getExpression());
                }
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                for (var descriptor : rule.getAllDeclarations()) {
                    remember(descriptor.getExpression());
                    if ("initial-value".equalsIgnoreCase(descriptor.getDescriptor())) {
                        variables
                                .computeIfAbsent(
                                        CSSParseHelper.unescapeURL(rule.getIdentifier()), ignored -> new ArrayList<>())
                                .add(descriptor.getExpression());
                    }
                }
            }

            @Override
            public void onBeginKeyframesRule(CSSKeyframesRule frames) {
                var header = CssLexical.tokens(css, start(frames), finish(frames)).stream()
                        .filter(token -> !token.value().isBlank())
                        .toList();
                var token = header.get(1);
                String name = CssAnimationValues.name(token.value());
                seenAnimations.add(name);
                if (externalAnimations.contains(name)) {
                    return;
                }
                if (imports.containsKey(name)) {
                    throw syntax.invalid("Owned animation conflicts with imported animation: " + name, frames);
                }
                if (Set.of("none", "default").contains(name.toLowerCase(Locale.ROOT))
                        || CssAnimationValues.CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))) {
                    throw syntax.invalid("Reserved keyframe name: " + name, frames);
                }
                String replacement = root + "--keyframes-" + CssIdentity.encode(name);
                animations.put(name, replacement);
                token(CssResource.SymbolKind.ANIMATION, token.start(), token.end(), replacement);
            }

            @Override
            public void onBeginFontFaceRule(CSSFontFaceRule face) {
                String name = CssFontValues.face(face);
                seenFonts.add(name);
                if (externalFonts.contains(name)) {
                    return;
                }
                if (fontImports.containsKey(name)) {
                    throw syntax.invalid("Owned font conflicts with imported family: " + name, face);
                }
                fonts.put(name, root + "--font-" + CssIdentity.encode(name));
            }
        });
        if (externalAnimations.stream().anyMatch(imports::containsKey)
                || externalFonts.stream().anyMatch(fontImports::containsKey)) {
            throw new IllegalArgumentException("An external animation or font cannot also be imported");
        }
    }

    private void token(CssResource.SymbolKind kind, int first, int last, String replacement) {
        if ('\'' == css.charAt(first) || '"' == css.charAt(first)) {
            first++;
            last--;
        }
        var token = new Use(kind, first, last, replacement);
        var previous = tokens.putIfAbsent(first, token);
        if (null != previous && !previous.equals(token)) {
            throw new CssSyntax.InvalidCss("Ambiguous managed name in a custom-property value", first);
        }
    }

    private void meaning(CSSExpressionMemberTermSimple term, String meaning) {
        String previous = meanings.putIfAbsent(term, meaning);
        if (null != previous && !previous.equals(meaning)) {
            throw syntax.invalid(
                    "Ambiguous managed name: a custom-property value is used as both a name and other data", term);
        }
    }

    private void data(CSSExpression expression, Set<String> visiting) {
        CssExpressions.visit(expression, member -> {
            if (member instanceof CSSExpressionMemberFunction function
                    && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                    && null != function.getExpression()
                    && 0 < function.getExpression().getMemberCount()
                    && function.getExpression().getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple first) {
                String name = CSSParseHelper.unescapeURL(first.getValue());
                if (visiting.add(name)) {
                    for (var definition : variables.getOrDefault(name, List.of())) {
                        data(definition, visiting);
                    }
                }
            } else if (member instanceof CSSExpressionMemberTermSimple term) {
                meaning(term, "data");
            }
        });
    }

    private void carrierMeanings(String property, Set<String> visiting, Set<String> result) {
        if (!visiting.add(property)) {
            return;
        }
        for (var definition : variables.getOrDefault(property, List.of())) {
            CssExpressions.visit(definition, member -> {
                if (member instanceof CSSExpressionMemberTermSimple term) {
                    String meaning = meanings.get(term);
                    if (null != meaning && !"data".equals(meaning)) {
                        result.add(meaning);
                    }
                } else if (member instanceof CSSExpressionMemberFunction function
                        && "var".equalsIgnoreCase(CSSParseHelper.unescapeURL(function.getFunctionName()))
                        && null != function.getExpression()
                        && 0 < function.getExpression().getMemberCount()
                        && function.getExpression().getMemberAtIndex(0)
                                instanceof CSSExpressionMemberTermSimple first) {
                    carrierMeanings(CSSParseHelper.unescapeURL(first.getValue()), visiting, result);
                }
            });
        }
    }

    private void registrations(CascadingStyleSheet sheet) {
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                var carriers = new HashSet<String>();
                carrierMeanings(CSSParseHelper.unescapeURL(rule.getIdentifier()), new HashSet<>(), carriers);
                if (carriers.isEmpty()) {
                    return;
                }
                for (var descriptor : rule.getAllDeclarations()) {
                    if (!"syntax".equalsIgnoreCase(descriptor.getDescriptor())) {
                        continue;
                    }
                    var expression = descriptor.getExpression();
                    if (1 != expression.getMemberCount()
                            || !(expression.getMemberAtIndex(0) instanceof CSSExpressionMemberTermSimple term)) {
                        continue;
                    }
                    String grammar = CSSParseHelper.extractStringValue(term.getValue());
                    if (null == grammar) {
                        continue;
                    }
                    grammar = CSSParseHelper.unescapeURL(grammar);
                    boolean type = false;
                    for (var token : CssLexical.tokens(grammar, 0, grammar.length())) {
                        if ("<".equals(token.value())) {
                            type = true;
                        } else if (">".equals(token.value())) {
                            type = false;
                        } else if (!type
                                && (ParserCSS30Constants.IDENT == token.kind()
                                        || ParserCSS30Constants.DASHED_IDENT == token.kind())
                                && (carriers.contains("animation:" + token.name())
                                        || carriers.contains("font:" + CssFontValues.canonical(token.name())))) {
                            throw syntax.invalid(
                                    "A registered name carrier cannot constrain a managed name with a literal syntax"
                                            + " keyword; use a universal or type-only syntax",
                                    descriptor);
                        }
                    }
                }
            }
        });
    }

    private void analyze(CascadingStyleSheet sheet) {
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                String property =
                        CSSParseHelper.unescapeURL(declaration.getProperty()).toLowerCase(Locale.ROOT);
                if (property.startsWith("--")) {
                    return;
                }
                boolean font = "font".equals(property) || "font-family".equals(property);
                boolean animation = "animation".equals(property) || "animation-name".equals(property);
                if (!font && !animation) {
                    data(declaration.getExpression(), new HashSet<>());
                    return;
                }
                boolean[] dynamic = {false};
                CssFonts.expand(
                        declaration.getExpression().getAllMembers(), List.of(), variables, Set.of(), expression -> {
                            var selected = Collections.newSetFromMap(
                                    new IdentityHashMap<CSSExpressionMemberTermSimple, Boolean>());
                            if (font) {
                                CssFontValues.Value value;
                                try {
                                    value = CssFontValues.read(new CSSDeclaration(property, expression));
                                } catch (IllegalArgumentException e) {
                                    CssExpressions.visit(expression, member -> {
                                        if (member instanceof CSSExpressionMemberTermSimple term) {
                                            meaning(term, "data");
                                        }
                                    });
                                    return;
                                }
                                dynamic[0] |= value.dynamic();
                                if (value.dynamic()) {
                                    fontImports
                                            .keySet()
                                            .forEach(name -> fonts.putIfAbsent(
                                                    name, root + "--font-" + CssIdentity.encode(name)));
                                }
                                var families = value.families();
                                if (value.dynamic() && "font".equals(property)) {
                                    var members = expression.getAllMembers();
                                    for (int first = 1; first < members.size(); first++) {
                                        var suffix = new CSSExpression();
                                        members.subList(first, members.size()).forEach(suffix::addMember);
                                        try {
                                            families = CssFontValues.read(new CSSDeclaration("font-family", suffix))
                                                    .families();
                                            break;
                                        } catch (IllegalArgumentException e) {
                                            /* Prefix still contains shorthand grammar. */
                                        }
                                    }
                                }
                                for (var family : families) {
                                    String name = family.name();
                                    seenFonts.add(name);
                                    if (externalFonts.contains(name)) {
                                        continue;
                                    }
                                    if (fontImports.containsKey(name)) {
                                        fonts.putIfAbsent(name, root + "--font-" + CssIdentity.encode(name));
                                    }
                                    String replacement = fonts.get(name);
                                    if (null != replacement) {
                                        var terms = family
                                                .expression()
                                                .getAllMembers()
                                                .subList(family.first(), family.last())
                                                .stream()
                                                .map(CSSExpressionMemberTermSimple.class::cast)
                                                .toList();
                                        var first = terms.get(0);
                                        var parent = parents.get(first);
                                        int index = 0;
                                        if (null == parent) {
                                            throw syntax.invalid(
                                                    "A managed font family must be a complete literal in one value",
                                                    first);
                                        }
                                        while (index < parent.getMemberCount()
                                                && parent.getMemberAtIndex(index) != first) {
                                            index++;
                                        }
                                        for (int part = 0; part < terms.size(); part++) {
                                            var term = terms.get(part);
                                            if (parent != parents.get(term)
                                                    || index + part >= parent.getMemberCount()
                                                    || parent.getMemberAtIndex(index + part) != term) {
                                                throw syntax.invalid(
                                                        "A managed font family must be a complete literal in one value",
                                                        first);
                                            }
                                            meaning(term, "font:" + name);
                                            selected.add(term);
                                        }
                                        token(
                                                CssResource.SymbolKind.FONT,
                                                start(first),
                                                start(terms.get(terms.size() - 1))
                                                        + terms.get(terms.size() - 1)
                                                                .getValue()
                                                                .length(),
                                                replacement);
                                    }
                                }
                            } else if (animation) {
                                CssAnimationValues.Value value;
                                try {
                                    value = CssAnimationValues.read(
                                            new CSSDeclaration(property, expression),
                                            syntax,
                                            CssAnimationValues.Mode.UTILITY);
                                } catch (CssSyntax.InvalidCss e) {
                                    throw syntax.invalid(String.valueOf(e.getMessage()), declaration);
                                }
                                for (var candidate : value.ambiguous()) {
                                    if (animations.containsKey(candidate.name())
                                            || imports.containsKey(candidate.name())) {
                                        throw syntax.invalid(
                                                "Ambiguous animation keyword after a dynamic variable",
                                                candidate.term());
                                    }
                                }
                                for (var name : value.names()) {
                                    seenAnimations.add(name.name());
                                    if (externalAnimations.contains(name.name())) {
                                        continue;
                                    }
                                    var imported = imports.get(name.name());
                                    if (null != imported && CssResource.Kind.KEYFRAMES == imported.kind()) {
                                        animations.putIfAbsent(
                                                name.name(), root + "--keyframes-" + CssIdentity.encode(name.name()));
                                        usedImports.add(name.name());
                                    }
                                    String replacement = animations.get(name.name());
                                    if (null == replacement) {
                                        throw syntax.invalid("Unknown animation: " + name.name(), name.term());
                                    }
                                    meaning(name.term(), "animation:" + name.name());
                                    selected.add(name.term());
                                    token(
                                            CssResource.SymbolKind.ANIMATION,
                                            start(name.term()),
                                            start(name.term())
                                                    + name.term().getValue().length(),
                                            replacement);
                                }
                            }
                            CssExpressions.visit(expression, member -> {
                                if (member instanceof CSSExpressionMemberTermSimple term && !selected.contains(term)) {
                                    meaning(term, "data");
                                }
                            });
                        });
                if (font && CssFontValues.read(declaration).dynamic()) {
                    dynamicFonts.add(dynamic[0]);
                }
            }

            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                for (var reference : CssConditions.read(css, rule).references()) {
                    if (reference.property()) {
                        for (var definition : variables.getOrDefault(reference.name(), List.of())) {
                            onDeclaration(new CSSDeclaration("content", definition));
                        }
                    }
                }
            }
        });
    }
}
