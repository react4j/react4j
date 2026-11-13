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
import org.jspecify.annotations.Nullable;

/** Proves the meaning of original name-bearing terms without changing browser variable evaluation. */
final class UtilityNames {
    private final @Nullable Prepared prepared;
    private final Set<ICSSSourceLocationAware> nodes = Collections.newSetFromMap(new IdentityHashMap<>());
    private final List<CSSPropertyRule> registrations = new ArrayList<>();
    private boolean changesVariables;
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

    static final class Prepared {
        private final UtilityNames names;
        private final CascadingStyleSheet sheet;
        private final Result result;

        private Prepared(UtilityNames names, CascadingStyleSheet sheet) {
            this.names = names;
            this.sheet = sheet;
            result = names.result();
        }

        Result result() {
            return result;
        }
    }

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
            Map<String, CssFonts.Import> fontImports,
            @Nullable Prepared prepared) {
        this.prepared = prepared;
        if (null != prepared) {
            variables.putAll(prepared.names.variables);
            animations.putAll(prepared.names.animations);
            fonts.putAll(prepared.names.fonts);
            usedImports.addAll(prepared.names.usedImports);
        }
        this.css = css;
        this.syntax = syntax;
        this.root = root;
        this.externalAnimations = externalAnimations;
        this.externalFonts = externalFonts;
        this.imports = imports;
        this.fontImports = fontImports;
    }

    static Prepared prepare(
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
        var names = new UtilityNames(css, syntax, root, externalAnimations, externalFonts, imports, fontImports, null);
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
        names.registrations.forEach(names::registration);
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
        return new Prepared(names, sheet);
    }

    static Result analyze(String css, CascadingStyleSheet sheet, CssSyntax syntax, Prepared prepared) {
        var base = prepared.names;
        var names = new UtilityNames(
                css,
                syntax,
                base.root,
                base.externalAnimations,
                base.externalFonts,
                base.imports,
                base.fontImports,
                prepared);
        names.collect(sheet);
        // New assignments can change the meaning of variables used by unrequested templates.
        if (names.changesVariables) {
            names.analyze(prepared.sheet);
        }
        names.analyze(sheet);
        base.registrations.forEach(names::registration);
        names.registrations.forEach(names::registration);
        return names.result();
    }

    private Result result() {
        return new Result(
                Map.copyOf(animations),
                Map.copyOf(fonts),
                Set.copyOf(usedImports),
                tokens.values().stream()
                        .sorted(Comparator.comparingInt(Use::start))
                        .toList(),
                List.copyOf(dynamicFonts));
    }

    private boolean inherited(ICSSSourceLocationAware node) {
        return null != prepared && prepared.names.nodes.contains(node);
    }

    private CssSyntax syntax(ICSSSourceLocationAware node) {
        return null != prepared && inherited(node) ? prepared.names.syntax : syntax;
    }

    private String source(ICSSSourceLocationAware node) {
        return null != prepared && inherited(node) ? prepared.names.css : css;
    }

    private CssSyntax.InvalidCss invalid(String message, ICSSSourceLocationAware node) {
        return syntax(node).invalid(message, node);
    }

    private @Nullable CSSExpression parent(CSSExpressionMemberTermSimple term) {
        var parent = parents.get(term);
        return null != parent || null == prepared ? parent : prepared.names.parents.get(term);
    }

    private @Nullable String meaning(CSSExpressionMemberTermSimple term) {
        String value = meanings.get(term);
        return null != value || changesVariables || null == prepared ? value : prepared.names.meanings.get(term);
    }

    private int start(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return syntax(node).offset(location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber());
    }

    private int finish(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return syntax(node).offset(location.getLastTokenEndLineNumber(), location.getLastTokenEndColumnNumber()) + 1;
    }

    private void remember(CSSExpression expression) {
        CssExpressions.visit(expression, node -> {
            if (node instanceof ICSSSourceLocationAware located) {
                nodes.add(located);
            }
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
                nodes.add(declaration);
                remember(declaration.getExpression());
                String property = CSSParseHelper.unescapeURL(declaration.getProperty());
                if (property.startsWith("--")) {
                    variable(property, declaration.getExpression());
                }
            }

            @Override
            public void onBeginPropertyRule(CSSPropertyRule rule) {
                nodes.add(rule);
                registrations.add(rule);
                for (var descriptor : rule.getAllDeclarations()) {
                    nodes.add(descriptor);
                    remember(descriptor.getExpression());
                    if ("initial-value".equalsIgnoreCase(descriptor.getDescriptor())) {
                        variable(CSSParseHelper.unescapeURL(rule.getIdentifier()), descriptor.getExpression());
                    }
                }
            }

            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                nodes.add(rule);
            }

            @Override
            public void onBeginKeyframesRule(CSSKeyframesRule frames) {
                nodes.add(frames);
                var header = CssLexical.tokens(source(frames), start(frames), finish(frames)).stream()
                        .filter(token -> !token.value().isBlank())
                        .toList();
                var token = header.get(1);
                String name = CssAnimationValues.name(token.value());
                seenAnimations.add(name);
                if (externalAnimations.contains(name)) {
                    return;
                }
                if (imports.containsKey(name)) {
                    throw invalid("Owned animation conflicts with imported animation: " + name, frames);
                }
                if (Set.of("none", "default").contains(name.toLowerCase(Locale.ROOT))
                        || CssAnimationValues.CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))) {
                    throw invalid("Reserved keyframe name: " + name, frames);
                }
                String replacement = root + "--keyframes-" + CssIdentity.encode(name);
                animations.put(name, replacement);
                token(CssResource.SymbolKind.ANIMATION, token.start(), token.end(), replacement);
            }

            @Override
            public void onBeginFontFaceRule(CSSFontFaceRule face) {
                nodes.add(face);
                String name = CssFontValues.face(face);
                seenFonts.add(name);
                if (externalFonts.contains(name)) {
                    return;
                }
                if (fontImports.containsKey(name)) {
                    throw invalid("Owned font conflicts with imported family: " + name, face);
                }
                fonts.put(name, root + "--font-" + CssIdentity.encode(name));
            }
        });
        if (externalAnimations.stream().anyMatch(imports::containsKey)
                || externalFonts.stream().anyMatch(fontImports::containsKey)) {
            throw new IllegalArgumentException("An external animation or font cannot also be imported");
        }
    }

    private void variable(String name, CSSExpression expression) {
        changesVariables = true;
        var values = new ArrayList<>(variables.getOrDefault(name, List.of()));
        values.add(expression);
        variables.put(name, List.copyOf(values));
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
        String previous = meaning(term);
        meanings.putIfAbsent(term, meaning);
        if (null != previous && !previous.equals(meaning)) {
            throw invalid(
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
                    String meaning = meaning(term);
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

    private void registration(CSSPropertyRule rule) {
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
                    throw invalid(
                            "A registered name carrier cannot constrain a managed name with a literal syntax"
                                    + " keyword; use a universal or type-only syntax",
                            descriptor);
                }
            }
        }
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
                                        var parent = parent(first);
                                        int index = 0;
                                        if (null == parent) {
                                            throw invalid(
                                                    "A managed font family must be a complete literal in one value",
                                                    first);
                                        }
                                        while (index < parent.getMemberCount()
                                                && parent.getMemberAtIndex(index) != first) {
                                            index++;
                                        }
                                        for (int part = 0; part < terms.size(); part++) {
                                            var term = terms.get(part);
                                            if (parent != parent(term)
                                                    || index + part >= parent.getMemberCount()
                                                    || parent.getMemberAtIndex(index + part) != term) {
                                                throw invalid(
                                                        "A managed font family must be a complete literal in one value",
                                                        first);
                                            }
                                            meaning(term, "font:" + name);
                                            selected.add(term);
                                        }
                                        if (!inherited(first)) {
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
                                }
                            } else if (animation) {
                                CssAnimationValues.Value value;
                                try {
                                    value = CssAnimationValues.read(
                                            new CSSDeclaration(property, expression),
                                            syntax(declaration),
                                            CssAnimationValues.Mode.UTILITY);
                                } catch (CssSyntax.InvalidCss e) {
                                    throw invalid(String.valueOf(e.getMessage()), declaration);
                                }
                                for (var candidate : value.ambiguous()) {
                                    if (animations.containsKey(candidate.name())
                                            || imports.containsKey(candidate.name())) {
                                        throw invalid(
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
                                        throw invalid("Unknown animation: " + name.name(), name.term());
                                    }
                                    meaning(name.term(), "animation:" + name.name());
                                    selected.add(name.term());
                                    if (!inherited(name.term())) {
                                        token(
                                                CssResource.SymbolKind.ANIMATION,
                                                start(name.term()),
                                                start(name.term())
                                                        + name.term().getValue().length(),
                                                replacement);
                                    }
                                }
                            }
                            CssExpressions.visit(expression, member -> {
                                if (member instanceof CSSExpressionMemberTermSimple term && !selected.contains(term)) {
                                    meaning(term, "data");
                                }
                            });
                        });
                if (font
                        && !inherited(declaration)
                        && CssFontValues.read(declaration).dynamic()) {
                    dynamicFonts.add(dynamic[0]);
                }
            }

            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                for (var reference : CssConditions.read(source(rule), rule).references()) {
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
