package react4j.css.internal;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Annotation-free binding of utility CSS to a defining owner and explicit browser hooks. */
public final class CssUtilityBinding {
    private CssUtilityBinding() {}

    public record Import(String owner, CssResource.Kind kind, boolean stable) {
        public String cssName() {
            return CssIdentity.root(owner) + (CssResource.Kind.KEYFRAMES == kind ? "--keyframes" : "");
        }
    }

    public record Context(
            Set<String> externalProperties,
            Set<String> externalAnimations,
            Set<String> externalFonts,
            Set<String> externalContainers,
            Set<String> externalAnchors,
            Map<String, Import> imports,
            Map<String, CssProperties.Import> properties,
            Map<String, CssIds.Import> ids,
            Map<String, String> assets,
            Map<String, CssFonts.Import> fonts,
            List<String> includes) {
        public Context {
            externalProperties = sorted(externalProperties);
            externalAnimations = sorted(externalAnimations);
            externalFonts = sorted(externalFonts);
            externalContainers = sorted(externalContainers);
            externalAnchors = sorted(externalAnchors);
            imports = ordered(imports);
            properties = ordered(properties);
            ids = ordered(ids);
            assets = ordered(assets);
            fonts = ordered(fonts);
            includes = List.copyOf(includes);
        }

        public static Context empty() {
            return new Context(
                    Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(),
                    List.of());
        }

        public String json() {
            return new Gson().toJson(this);
        }

        public static Context read(String json) {
            return new Gson().fromJson(json, Context.class);
        }
    }

    private static Set<String> sorted(Set<String> values) {
        return Collections.unmodifiableSet(new TreeSet<>(values));
    }

    private static <T> Map<String, T> ordered(Map<String, T> values) {
        return Collections.unmodifiableMap(new TreeMap<>(values));
    }

    public record Result(
            SourceMap.Writer output,
            Set<String> imports,
            Map<String, String> properties,
            Set<String> propertyImports,
            Map<String, String> ids,
            Map<String, String> animations,
            Map<String, String> fonts,
            Map<String, String> containers,
            Map<String, String> anchors,
            List<CssTokens.Token> names,
            List<Boolean> dynamicFonts,
            int boundary) {}

    public record Bound(String css, String sourceMap, CssResource metadata, int boundary) {
        public Bound project(int start, int end) {
            var spans = List.of(new SourceMap.Span(start, end));
            var output =
                    SourceMap.project(css, JsonParser.parseString(sourceMap).getAsJsonObject(), spans);
            return new Bound(output.css(), output.map("utility.css"), metadata.project(spans), 0);
        }
    }

    public static Result compile(
            SourceMap.Writer input,
            String root,
            Set<String> managed,
            Set<String> exportedIds,
            Set<String> exportedAnimations,
            Context context,
            boolean nativeRules,
            int boundary) {
        String css = input.css();
        var syntax = new CssSyntax(css, 0);
        var sheet = syntax.parse(CssSyntax.Mode.UTILITY);
        UtilitySelectors.check(sheet, syntax, managed);
        var containers = UtilityContainers.names(css, sheet, syntax, root, context.externalContainers(), nativeRules);
        var anchors = nativeRules
                ? UtilityAnchors.names(sheet, syntax, root, context.externalAnchors())
                : Map.<String, String>of();
        var names = UtilityNames.analyze(
                css,
                sheet,
                syntax,
                root,
                context.externalAnimations(),
                context.externalFonts(),
                exportedAnimations,
                context.imports(),
                context.fonts(),
                nativeRules);
        if (context.externalProperties().stream().anyMatch(context.properties()::containsKey)) {
            throw new IllegalArgumentException("An external custom property cannot also be imported");
        }
        if (nativeRules) {
            var owned = new LinkedHashSet<String>();
            CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
                @Override
                public void onDeclaration(CSSDeclaration declaration) {
                    String name = CSSParseHelper.unescapeURL(declaration.getProperty());
                    if (name.startsWith("--")) {
                        owned.add(name.substring(2));
                    }
                }

                @Override
                public void onBeginPropertyRule(CSSPropertyRule rule) {
                    owned.add(CSSParseHelper.unescapeURL(rule.getIdentifier()).substring(2));
                }
            });
            for (var token : CssTokens.read(css)) {
                if (CssResource.SymbolKind.PROPERTY == token.kind()) {
                    String name = token.name().substring(2);
                    if (!owned.contains(name)
                            && !context.externalProperties().contains(name)
                            && !context.properties().containsKey(name)) {
                        throw new CssSyntax.InvalidCss(
                                "U_EXTERNAL_PROPERTY: Declare externalProperties hook or an owned property: --" + name,
                                token.start());
                    }
                }
            }
        }
        var properties = new LinkedHashMap<String, String>();
        var usedProperties = new LinkedHashSet<String>();
        CssProperties.rewrite(
                sheet,
                syntax,
                name -> {
                    if (context.externalProperties().contains(name)) {
                        return "--" + name;
                    }
                    if (context.properties().containsKey(name)) {
                        usedProperties.add(name);
                    }
                    String resolved = "--" + root + "--" + CssIdentity.encode(name);
                    properties.put(name, resolved);
                    return resolved;
                },
                true);
        var idBindings = new LinkedHashMap<String, String>();
        exportedIds.forEach(name -> idBindings.put(name, root + "--id-" + CssIdentity.encode(name)));
        context.ids().keySet().forEach(name -> idBindings.put(name, root + "--id-" + CssIdentity.encode(name)));
        var ids = CssIds.rewrite(sheet, syntax, idBindings, true);
        var edits = new ArrayList<SourceMap.Edit>();
        for (var token : CssTokens.read(css)) {
            String replacement = CssResource.SymbolKind.PROPERTY == token.kind()
                    ? properties.get(token.name().substring(2))
                    : CssResource.SymbolKind.ID == token.kind()
                            ? ids.get(token.name())
                            : CssResource.SymbolKind.CONTAINER == token.kind()
                                    ? containers.get(token.name())
                                    : CssResource.SymbolKind.ANCHOR == token.kind() ? anchors.get(token.name()) : null;
            if (null != replacement && !replacement.equals(token.value())) {
                edits.add(new SourceMap.Edit(token.start(), token.end(), replacement));
            }
        }
        edits.addAll(CssUrls.edits(
                css,
                value -> CssUrls.checked(value, (name, suffix) -> {
                    String asset = context.assets().get(name);
                    if (null == asset) {
                        throw new IllegalArgumentException("Unknown CSS asset import or accessor: " + name);
                    }
                    return "asset:" + asset + suffix;
                })));
        edits.addAll(names.edits());
        edits.sort(Comparator.comparingInt(SourceMap.Edit::start));
        var nameTokens = new ArrayList<CssTokens.Token>();
        for (var token : names.tokens()) {
            int shift = delta(edits, token.start());
            nameTokens.add(new CssTokens.Token(
                    token.kind(),
                    token.start() + shift,
                    token.start() + shift + token.name().length(),
                    token.name(),
                    token.name()));
        }
        var output = edits.isEmpty()
                ? input
                : SourceMap.replace(
                        css, JsonParser.parseString(input.map("utility.css")).getAsJsonObject(), edits);
        return new Result(
                output,
                names.imports(),
                Map.copyOf(properties),
                Set.copyOf(usedProperties),
                ids,
                names.animations(),
                names.fonts(),
                containers,
                anchors,
                List.copyOf(nameTokens),
                names.dynamicFonts(),
                boundary + delta(edits, boundary));
    }

    /** Checks references in a token even when no template instantiates that token. */
    public static void checkValue(String value, CssResource universe, Context context) {
        String css = ".token { x:" + value + "; }";
        var properties = new LinkedHashSet<>(context.externalProperties());
        properties.addAll(context.properties().keySet());
        universe.symbols().stream()
                .filter(symbol -> CssResource.SymbolKind.PROPERTY == symbol.kind())
                .map(CssResource.Symbol::name)
                .forEach(properties::add);
        var anchors = new LinkedHashSet<>(context.externalAnchors());
        universe.symbols().stream()
                .filter(symbol -> CssResource.SymbolKind.ANCHOR == symbol.kind())
                .map(CssResource.Symbol::name)
                .forEach(anchors::add);
        for (var token : CssTokens.read(css)) {
            if (CssResource.SymbolKind.PROPERTY == token.kind()
                    && !properties.contains(token.name().substring(2))) {
                throw new IllegalArgumentException("U_EXTERNAL_PROPERTY: Unknown token property " + token.name());
            }
            if (CssResource.SymbolKind.ANCHOR == token.kind() && !anchors.contains(token.name())) {
                throw new IllegalArgumentException("U_ANCHOR_NAME: Unknown token anchor " + token.name());
            }
        }
        CssUrls.edits(
                css,
                url -> CssUrls.checked(url, (name, suffix) -> {
                    String asset = context.assets().get(name);
                    if (null == asset) {
                        throw new IllegalArgumentException("Unknown CSS asset import or accessor: " + name);
                    }
                    return "asset:" + asset + suffix;
                }));
    }

    private static int delta(List<SourceMap.Edit> edits, int offset) {
        return edits.stream()
                .filter(edit -> edit.end() <= offset)
                .mapToInt(edit -> edit.value().length() - (edit.end() - edit.start()))
                .sum();
    }

    /** Resolves compiler intermediates to canonical symbols, independently of annotation processing. */
    public static Bound resolve(String owner, Result result, Map<String, String> classes, Context context) {
        var definitions = new LinkedHashMap<CssSymbols.Key, CssSymbols.Definition>();
        classes.forEach((name, value) -> definitions.put(
                new CssSymbols.Key(CssResource.SymbolKind.CLASS, value),
                new CssSymbols.Definition(
                        new CssResource.SymbolId(owner, CssResource.SymbolKind.CLASS, name), value, List.of())));
        result.properties().forEach((name, value) -> {
            var imported = context.properties().get(name);
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.PROPERTY, value),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(
                                    null == imported
                                            ? owner
                                            : imported.binding().owner(),
                                    CssResource.SymbolKind.PROPERTY,
                                    null == imported ? name : imported.binding().name()),
                            null == imported ? value : imported.binding().cssName(),
                            null == imported ? List.of() : List.of(imported.provider())));
        });
        result.ids().forEach((name, value) -> {
            var imported = context.ids().get(name);
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.ID, value),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(
                                    null == imported
                                            ? owner
                                            : imported.binding().owner(),
                                    CssResource.SymbolKind.ID,
                                    null == imported ? name : imported.binding().name()),
                            null == imported ? value : imported.binding().cssName(),
                            List.of()));
        });
        result.animations().forEach((name, value) -> {
            var imported = context.imports().get(name);
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.ANIMATION, value),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(
                                    null == imported ? owner : imported.owner(),
                                    CssResource.SymbolKind.ANIMATION,
                                    null == imported ? name : ""),
                            null == imported ? value : imported.cssName(),
                            null == imported ? List.of() : List.of(imported.owner())));
        });
        result.fonts().forEach((name, value) -> {
            var imported = context.fonts().get(name);
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.FONT, value),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(
                                    null == imported ? owner : imported.owner(),
                                    CssResource.SymbolKind.FONT,
                                    null == imported ? name : imported.name()),
                            null == imported ? value : imported.cssName(),
                            null == imported ? List.of() : List.of(imported.owner())));
        });
        result.containers()
                .forEach((name, value) -> definitions.put(
                        new CssSymbols.Key(CssResource.SymbolKind.CONTAINER, value),
                        new CssSymbols.Definition(
                                new CssResource.SymbolId(owner, CssResource.SymbolKind.CONTAINER, name),
                                value,
                                List.of())));
        result.anchors()
                .forEach((name, value) -> definitions.put(
                        new CssSymbols.Key(CssResource.SymbolKind.ANCHOR, value),
                        new CssSymbols.Definition(
                                new CssResource.SymbolId(owner, CssResource.SymbolKind.ANCHOR, name),
                                value,
                                List.of())));
        var tokens = new ArrayList<>(CssTokens.read(result.output().css()));
        tokens.removeIf(token ->
                CssResource.SymbolKind.ANIMATION == token.kind() || CssResource.SymbolKind.FONT == token.kind());
        tokens.addAll(result.names());
        tokens.sort(Comparator.comparingInt(CssTokens.Token::start));
        int boundary = result.boundary();
        int shift = 0;
        for (var token : tokens) {
            var definition = definitions.get(new CssSymbols.Key(token.kind(), token.value()));
            if (null != definition && token.end() <= boundary) {
                shift += definition.readable().length() - (token.end() - token.start());
            }
        }
        var resolved = CssSymbols.resolve(result.output(), definitions, tokens);
        var dependencies = new TreeSet<>(context.includes());
        context.imports().values().forEach(value -> dependencies.add(value.owner()));
        context.properties().values().forEach(value -> dependencies.add(value.provider()));
        context.ids().values().forEach(value -> dependencies.addAll(value.providers()));
        context.fonts().values().forEach(value -> dependencies.add(value.owner()));
        var symbols = definitions.values().stream()
                .filter(value -> owner.equals(value.symbol().owner()))
                .map(value -> new CssResource.Symbol(
                        value.symbol().kind(), value.symbol().name(), value.readable(), false))
                .sorted(Comparator.comparing(symbol -> symbol.kind() + ":" + symbol.name()))
                .toList();
        var fonts = definitions.values().stream()
                .map(CssSymbols.Definition::symbol)
                .filter(id -> CssResource.SymbolKind.FONT == id.kind())
                .distinct()
                .toList();
        var uses = CssFontValues.uses(resolved.output().css(), fonts);
        var dynamicUses = new ArrayList<CssResource.FontUse>();
        for (int i = 0; i < uses.size(); i++) {
            if (result.dynamicFonts().get(i)) {
                dynamicUses.add(uses.get(i));
            }
        }
        var resource = new CssResource(
                owner,
                CssResource.Kind.UTILITY,
                List.copyOf(dependencies),
                context.includes(),
                "",
                List.of(),
                symbols,
                resolved.occurrences(),
                dynamicUses);
        return new Bound(resolved.output().css(), resolved.output().map("utility.css"), resource, boundary + shift);
    }
}
