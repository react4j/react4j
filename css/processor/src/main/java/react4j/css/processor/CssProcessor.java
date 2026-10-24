package react4j.css.processor;

import com.google.gson.JsonParser;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.util.Trees;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.ElementsUtil;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssAnchors;
import react4j.css.internal.CssContainers;
import react4j.css.internal.CssFontValues;
import react4j.css.internal.CssFonts;
import react4j.css.internal.CssFragments;
import react4j.css.internal.CssIdentity;
import react4j.css.internal.CssIds;
import react4j.css.internal.CssInput;
import react4j.css.internal.CssProperties;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSymbols;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.CssTokens;
import react4j.css.internal.CssUtilityBinding;
import react4j.css.internal.SourceMap;
import react4j.css.utility.language.UtilityLanguage;

/** Validates component CSS in the JVM, then emits static bindings and CSS resources. */
@SupportedAnnotationTypes({
    "react4j.css.Css",
    "react4j.css.UtilityCssLibrary",
    "react4j.css.UtilityStyles",
    "react4j.css.UtilityCss",
    "react4j.css.Assets",
    "react4j.css.Asset",
    "react4j.css.Keyframes",
    "react4j.css.CssImport",
    "react4j.css.CssImports",
    "react4j.css.GlobalCss",
    "react4j.css.CssCustomProperty",
    "react4j.css.CssId",
    "react4j.css.CssAnimation",
    "react4j.css.CssStable"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class CssProcessor extends AbstractStandardProcessor {

    private static final String CSS_ANNOTATION = "react4j.css.Css";
    private final Map<String, Set<String>> propertyNames = new HashMap<>();
    private final Map<String, Map<String, String>> utilityInventories = new HashMap<>();
    private final Map<String, Optional<UtilityLanguage.Prepared>> nativeSources = new HashMap<>();
    private final Map<String, UtilityLanguage.Checked> nativeLibraries = new HashMap<>();
    private final Map<String, FileInput> fileInputs = new HashMap<>();

    @Override
    protected String getOptionPrefix() {
        return "react4j.css";
    }

    @Override
    protected String getIssueTrackerURL() {
        return "https://github.com/react4j/react4j/issues";
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
        var owners = new TreeMap<String, TypeElement>();
        for (var name : getSupportedAnnotationTypes()) {
            var annotationType = processingEnv.getElementUtils().getTypeElement(name);
            if (null != annotationType) {
                for (var element : round.getElementsAnnotatedWith(annotationType)) {
                    var owner = element instanceof TypeElement type
                            ? type
                            : (TypeElement) Objects.requireNonNull(element.getEnclosingElement());
                    if (ElementKind.INTERFACE == owner.getKind()
                            && null == annotation(owner, "react4j.css.CssStable")
                            && owner.getAnnotationMirrors().stream()
                                    .noneMatch(mirror -> List.of(
                                                    CSS_ANNOTATION,
                                                    "react4j.css.UtilityCssLibrary",
                                                    "react4j.css.UtilityStyles",
                                                    "react4j.css.Assets",
                                                    "react4j.css.GlobalCss",
                                                    "react4j.css.Keyframes",
                                                    "react4j.css.CssImport",
                                                    "react4j.css.CssImports")
                                            .contains(mirror.getAnnotationType().toString()))) {
                        continue;
                    }
                    owners.put(binaryName(owner), owner);
                }
            }
        }
        for (var owner : owners.values()) {
            try {
                process(owner);
            } catch (IllegalArgumentException | IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), owner);
            }
        }
        return true;
    }

    private void process(TypeElement owner) throws IOException {
        if (null != annotation(owner, "react4j.css.Assets")) {
            for (var name : List.of(
                    "Css",
                    "GlobalCss",
                    "Keyframes",
                    "CssStable",
                    "CssImport",
                    "CssImports",
                    "UtilityStyles",
                    "UtilityCssLibrary")) {
                if (null != annotation(owner, "react4j.css." + name)) {
                    throw new IllegalArgumentException("@Assets cannot be combined with @" + name);
                }
            }
            validateResourceType(owner);
            var definition = AssetDeclarations.parse(processingEnv, owner);
            var generated = CssGenerator.buildType(
                    processingEnv,
                    owner,
                    "Assets_",
                    definition.publicFactory(),
                    definition.deprecated(),
                    definition.methods(),
                    definition.values());
            emitTypeSpec(definition.packageName(), generated.type());
            AssetDeclarations.emit(processingEnv, owner, generated.implementation(), definition);
            return;
        }
        for (var method : owner.getEnclosedElements()) {
            if (null != annotation(method, "react4j.css.Asset")) {
                throw new IllegalArgumentException("@Asset methods require an @Assets definition");
            }
        }
        Kind kind = sourceKind(owner);
        CssDescriptor descriptor = Kind.COMPONENT == kind ? parse(owner) : null;
        if (Kind.COMPONENT != kind) {
            validateResourceType(owner);
            descriptor = resourceDescriptor(owner);
        }
        var imports = imports(owner, kind);
        if (Kind.UTILITY == kind && null != nativePrepared(owner)) {
            emitNativeProvider(owner, descriptor, imports, nativeLibrary(owner));
            return;
        }
        var utilities = utilitySelection(owner, descriptor);
        var compiled = compileCss(owner, kind, descriptor, imports);
        if (null == compiled) {
            return;
        }
        var definitions = definitions(owner, kind, descriptor, compiled, imports);
        var tokens = new ArrayList<>(CssTokens.read(compiled.output().css()));
        if (Kind.UTILITY == kind) {
            tokens.removeIf(token ->
                    CssResource.SymbolKind.ANIMATION == token.kind() || CssResource.SymbolKind.FONT == token.kind());
            tokens.addAll(compiled.names());
            tokens.sort(Comparator.comparingInt(CssTokens.Token::start));
        }
        var resolved = CssSymbols.resolve(compiled.output(), definitions, tokens);
        var result = new CssCompiler.Result(
                resolved.output(),
                compiled.imports(),
                readable(compiled.properties(), CssResource.SymbolKind.PROPERTY, definitions),
                compiled.propertyImports(),
                readable(compiled.ids(), CssResource.SymbolKind.ID, definitions),
                readable(compiled.animations(), CssResource.SymbolKind.ANIMATION, definitions),
                readable(compiled.fonts(), CssResource.SymbolKind.FONT, definitions),
                readable(compiled.containers(), CssResource.SymbolKind.CONTAINER, definitions),
                compiled.names(),
                compiled.dynamicFonts());
        for (var name : imports.properties().keySet().stream().sorted().toList()) {
            if (!result.propertyImports().contains(name)) {
                throw new IllegalArgumentException("Unused custom property import: " + name);
            }
        }
        for (var name : imports.ids().keySet().stream().sorted().toList()) {
            if (!result.ids().containsKey(name)
                    && (null == descriptor
                            || descriptor.accessors().stream()
                                    .noneMatch(accessor ->
                                            CssAccessor.Kind.ID == accessor.kind() && name.equals(accessor.name())))) {
                throw new IllegalArgumentException("Unused ID import: " + name);
            }
        }
        for (var name : imports.fonts().keySet()) {
            if (!result.fonts().containsKey(name)) {
                throw new IllegalArgumentException("Unused font import: " + name);
            }
        }
        String implementation = "";
        var bound = new ArrayList<CssResource.Binding>();
        if (null != descriptor) {
            validateStableImports(owner, descriptor, imports);
            var values = new LinkedHashMap<>(bindings(descriptor, result, imports));
            for (var accessor : descriptor.accessors()) {
                if (!accessor.utilities().isEmpty()) {
                    assert null != utilities;
                    String method = accessor.method().getSimpleName().toString();
                    var classes = new ArrayList<String>();
                    for (var name : accessor.utilities()) {
                        String utility = utilities.classes().get(name);
                        if (null == utility) {
                            throw new IllegalArgumentException("Unknown managed utility class: " + name);
                        }
                        classes.add(utility);
                    }
                    classes.add(values.get(method));
                    values.put(method, String.join(" ", classes));
                }
            }
            var generated = CssGenerator.buildType(processingEnv, descriptor, values);
            emitTypeSpec(descriptor.packageName(), generated.type());
            implementation = generated.implementation();
            for (var accessor : descriptor.accessors()) {
                String method = accessor.method().getSimpleName().toString();
                bound.add(new CssResource.Binding(
                        method,
                        CssResource.SymbolKind.valueOf(accessor.kind().name()),
                        Objects.requireNonNull(values.get(method)),
                        accessor.utilities().isEmpty()
                                ? bindingProviders(accessor, imports)
                                : List.of(Objects.requireNonNull(utilities).owner()),
                        composition(owner, accessor, imports, utilities)));
            }
        }
        String ownerName = binaryName(owner);
        String resource = CssResource.path(ownerName);
        String file = resource.substring(resource.lastIndexOf('/') + 1);
        var dependencies = new LinkedHashSet<>(imports.globals());
        if (null != utilities) {
            dependencies.add(utilities.owner());
        }
        imports.named().entrySet().stream()
                .filter(entry -> result.imports().contains(entry.getKey()))
                .map(entry -> entry.getValue().owner())
                .distinct()
                .sorted()
                .forEach(dependencies::add);
        imports.properties().values().stream()
                .map(CssProperties.Import::provider)
                .sorted()
                .forEach(dependencies::add);
        imports.ids().values().stream()
                .flatMap(binding -> binding.providers().stream())
                .sorted()
                .forEach(dependencies::add);
        imports.fonts().values().stream().map(CssFonts.Import::owner).sorted().forEach(dependencies::add);
        var metadata = new CssResource(
                ownerName,
                kind,
                List.copyOf(dependencies),
                imports.globals(),
                implementation,
                bound,
                symbols(owner, kind, descriptor, result, imports, definitions),
                resolved.occurrences(),
                fontUses(
                        kind,
                        compiled.dynamicFonts(),
                        resolved.output().css(),
                        definitions.values().stream()
                                .map(CssSymbols.Definition::symbol)
                                .filter(id -> CssResource.SymbolKind.FONT == id.kind())
                                .distinct()
                                .sorted(Comparator.comparing(CssResource.SymbolId::id))
                                .toList()));
        var output = result.output();
        for (var entry : Map.of(
                        resource,
                        output.css() + SourceMap.reference(file),
                        resource + ".map",
                        output.map(file),
                        resource + ".json",
                        metadata.json())
                .entrySet()) {
            try (var stream = processingEnv
                    .getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", entry.getKey(), owner)
                    .openOutputStream()) {
                stream.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static List<CssResource.FontUse> fontUses(
            Kind kind, List<Boolean> dynamic, String css, List<CssResource.SymbolId> fonts) {
        var uses = CssFontValues.uses(css, fonts);
        if (Kind.UTILITY != kind || uses.isEmpty()) {
            return uses;
        }
        if (uses.size() != dynamic.size()) {
            throw new IllegalArgumentException("Font dependency analysis differs from emitted declarations");
        }
        var retained = new ArrayList<CssResource.FontUse>();
        for (int i = 0; i < uses.size(); i++) {
            if (dynamic.get(i)) {
                retained.add(uses.get(i));
            }
        }
        return List.copyOf(retained);
    }

    private static Map<String, String> readable(
            Map<String, String> values,
            CssResource.SymbolKind kind,
            Map<CssSymbols.Key, CssSymbols.Definition> definitions) {
        var result = new LinkedHashMap<String, String>();
        values.forEach((name, value) -> result.put(
                name,
                Objects.requireNonNull(definitions.get(new CssSymbols.Key(kind, value)))
                        .readable()));
        return Map.copyOf(result);
    }

    private Map<CssSymbols.Key, CssSymbols.Definition> definitions(
            TypeElement owner,
            Kind kind,
            @Nullable CssDescriptor descriptor,
            CssCompiler.Result result,
            Imports imports) {
        String ownerName = binaryName(owner);
        String root = CssIdentity.root(ownerName);
        var definitions = new LinkedHashMap<CssSymbols.Key, CssSymbols.Definition>();
        if (Kind.UTILITY == kind) {
            var classes = utilityClasses(owner);
            for (var token : CssTokens.read(result.output().css())) {
                if (CssResource.SymbolKind.CLASS == token.kind() && classes.containsKey(token.name())) {
                    definitions.put(
                            new CssSymbols.Key(token.kind(), token.value()),
                            new CssSymbols.Definition(
                                    new CssResource.SymbolId(ownerName, token.kind(), token.name()),
                                    classes.get(token.name()),
                                    List.of()));
                }
            }
        }
        if (Kind.COMPONENT == kind) {
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.CLASS, root),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(ownerName, CssResource.SymbolKind.CLASS, ""), root, List.of()));
            assert null != descriptor;
            for (var accessor : descriptor.accessors()) {
                if (CssAccessor.Kind.CLASS == accessor.kind() && !"style".equals(accessor.name())) {
                    String value = root + "--" + CssIdentity.encode(accessor.name());
                    definitions.put(
                            new CssSymbols.Key(CssResource.SymbolKind.CLASS, value),
                            new CssSymbols.Definition(
                                    new CssResource.SymbolId(ownerName, CssResource.SymbolKind.CLASS, accessor.name()),
                                    value,
                                    List.of()));
                }
            }
        }
        result.properties().forEach((name, value) -> {
            var imported = imports.properties().get(name);
            var id = new CssResource.SymbolId(
                    null == imported ? ownerName : imported.binding().owner(),
                    CssResource.SymbolKind.PROPERTY,
                    null == imported ? name : imported.binding().name());
            String readable = null != imported
                    ? imported.binding().cssName()
                    : Kind.GLOBAL == kind && stable(owner, CssResource.SymbolKind.PROPERTY, name) ? "--" + name : value;
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.PROPERTY, value),
                    new CssSymbols.Definition(
                            id, readable, null == imported ? List.of() : List.of(imported.provider())));
        });
        result.ids().forEach((name, value) -> {
            var imported = imports.ids().get(name);
            var id = new CssResource.SymbolId(
                    null == imported ? ownerName : imported.binding().owner(),
                    CssResource.SymbolKind.ID,
                    null == imported ? name : imported.binding().name());
            String readable = null != imported
                    ? imported.binding().cssName()
                    : Kind.GLOBAL == kind && stable(owner, CssResource.SymbolKind.ID, name) ? name : value;
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.ID, value),
                    new CssSymbols.Definition(id, readable, List.of()));
        });
        result.animations().forEach((name, value) -> {
            if (!imports.named().containsKey(name)) {
                definitions.put(
                        new CssSymbols.Key(CssResource.SymbolKind.ANIMATION, value),
                        new CssSymbols.Definition(
                                new CssResource.SymbolId(ownerName, CssResource.SymbolKind.ANIMATION, name),
                                value,
                                List.of()));
            }
        });
        result.fonts().forEach((name, value) -> {
            var imported = imports.fonts().get(name);
            definitions.put(
                    new CssSymbols.Key(CssResource.SymbolKind.FONT, value),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(
                                    null == imported ? ownerName : imported.owner(),
                                    CssResource.SymbolKind.FONT,
                                    null == imported ? name : imported.name()),
                            null == imported ? value : imported.cssName(),
                            null == imported ? List.of() : List.of(imported.owner())));
        });
        result.containers()
                .forEach((name, value) -> definitions.put(
                        new CssSymbols.Key(CssResource.SymbolKind.CONTAINER, value),
                        new CssSymbols.Definition(
                                new CssResource.SymbolId(ownerName, CssResource.SymbolKind.CONTAINER, name),
                                value,
                                List.of())));
        imports.named().forEach((alias, imported) -> {
            boolean animation = Kind.KEYFRAMES == imported.kind();
            var symbolKind = animation ? CssResource.SymbolKind.ANIMATION : CssResource.SymbolKind.CLASS;
            String intermediate = root + (animation ? "--keyframes-" : "--peer-") + CssIdentity.encode(alias);
            definitions.put(
                    new CssSymbols.Key(symbolKind, intermediate),
                    new CssSymbols.Definition(
                            new CssResource.SymbolId(imported.owner(), symbolKind, ""),
                            imported.cssName(),
                            animation ? List.of(imported.owner()) : List.of()));
        });
        return definitions;
    }

    private String binaryName(TypeElement owner) {
        return processingEnv.getElementUtils().getBinaryName(owner).toString();
    }

    private static List<String> bindingProviders(CssAccessor accessor, Imports imports) {
        return switch (accessor.kind()) {
            case ID ->
                imports.ids().containsKey(accessor.name())
                        ? imports.ids().get(accessor.name()).providers()
                        : List.of();
            case PROPERTY ->
                imports.properties().containsKey(accessor.name())
                        ? List.of(imports.properties().get(accessor.name()).provider())
                        : List.of();
            case ANIMATION ->
                imports.named().containsKey(accessor.name())
                        ? List.of(imports.named().get(accessor.name()).owner())
                        : List.of();
            case CLASS -> List.of();
        };
    }

    private List<CssResource.SymbolId> composition(
            TypeElement owner, CssAccessor accessor, Imports imports, @Nullable Utilities utilities) {
        var kind = CssResource.SymbolKind.valueOf(accessor.kind().name());
        String name = accessor.name();
        String provider = binaryName(owner);
        if (CssResource.SymbolKind.CLASS == kind) {
            var root = new CssResource.SymbolId(provider, kind, "");
            var symbols = new ArrayList<CssResource.SymbolId>();
            for (var utility : accessor.utilities()) {
                symbols.add(new CssResource.SymbolId(
                        Objects.requireNonNull(utilities).owner(), kind, utility));
            }
            symbols.add(root);
            if (!"style".equals(name)) {
                symbols.add(new CssResource.SymbolId(provider, kind, name));
            }
            return List.copyOf(symbols);
        }
        if (CssResource.SymbolKind.PROPERTY == kind && imports.properties().containsKey(name)) {
            var binding = imports.properties().get(name).binding();
            provider = binding.owner();
            name = binding.name();
        } else if (CssResource.SymbolKind.ID == kind && imports.ids().containsKey(name)) {
            var binding = imports.ids().get(name).binding();
            provider = binding.owner();
            name = binding.name();
        } else if (CssResource.SymbolKind.ANIMATION == kind && imports.named().containsKey(name)) {
            provider = imports.named().get(name).owner();
            name = "";
        }
        return List.of(new CssResource.SymbolId(provider, kind, name));
    }

    static @Nullable AnnotationMirror annotation(Element owner, String name) {
        return owner.getAnnotationMirrors().stream()
                .filter(a -> a.getAnnotationType().toString().equals(name))
                .findFirst()
                .orElse(null);
    }

    private AnnotationValue value(AnnotationMirror annotation, String name) {
        return processingEnv.getElementUtils().getElementValuesWithDefaults(annotation).entrySet().stream()
                .filter(e -> e.getKey().getSimpleName().contentEquals(name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing annotation value: " + name));
    }

    private static String annotationName(Kind kind) {
        return switch (kind) {
            case COMPONENT -> CSS_ANNOTATION;
            case GLOBAL -> "react4j.css.GlobalCss";
            case KEYFRAMES -> "react4j.css.Keyframes";
            case UTILITY -> "react4j.css.UtilityCssLibrary";
        };
    }

    private Kind sourceKind(TypeElement owner) {
        var kinds = new ArrayList<Kind>();
        for (var kind : Kind.values()) {
            if (null != annotation(owner, annotationName(kind))) {
                kinds.add(kind);
            }
        }
        if (null != annotation(owner, "react4j.css.UtilityStyles") && !kinds.contains(Kind.COMPONENT)) {
            kinds.add(Kind.COMPONENT);
        }
        if (1 != kinds.size()) {
            throw new IllegalArgumentException(
                    "Declare exactly one CSS definition kind: @Css/@UtilityStyles, @GlobalCss, @Keyframes or"
                            + " @UtilityCssLibrary");
        }
        return kinds.get(0);
    }

    private void validateResourceType(TypeElement owner) {
        if (ElementKind.CLASS != owner.getKind()
                || !owner.getTypeParameters().isEmpty()
                || !isType(owner.getSuperclass(), "java.lang.Object")
                || owner.getModifiers().contains(Modifier.PRIVATE)
                || owner.getModifiers().contains(Modifier.SEALED)
                || (owner.getNestingKind().isNested() && !owner.getModifiers().contains(Modifier.STATIC))) {
            throw new IllegalArgumentException(
                    "CSS resource definitions require an accessible, non-generic class extending Object; nested"
                            + " classes must be static");
        }
        for (var enclosing = owner.getEnclosingElement();
                enclosing instanceof TypeElement;
                enclosing = enclosing.getEnclosingElement()) {
            if (enclosing.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalArgumentException("CSS resource definitions must be accessible from their package");
            }
        }
        for (var member : owner.getEnclosedElements()) {
            if (ElementKind.FIELD == member.getKind()
                    || (ElementKind.CONSTRUCTOR == member.getKind()
                            && Elements.Origin.EXPLICIT
                                    == ElementsUtil.getOrigin(processingEnv.getElementUtils(), member))) {
                throw new IllegalArgumentException("CSS resource definitions must not declare fields or constructors");
            }
        }
        if (Trees.instance(processingEnv).getTree(owner).getMembers().stream().anyMatch(BlockTree.class::isInstance)) {
            throw new IllegalArgumentException("CSS resource definitions must not declare initializers");
        }
    }

    private record Imports(
            Map<String, CssCompiler.Import> named,
            List<String> globals,
            Map<String, CssProperties.Import> properties,
            Map<String, CssIds.Import> ids,
            Map<String, String> assets,
            Map<String, CssFonts.Import> fonts) {}

    private List<AnnotationMirror> importMirrors(TypeElement owner) {
        var mirrors = new ArrayList<AnnotationMirror>();
        var direct = annotation(owner, "react4j.css.CssImport");
        if (null != direct) {
            mirrors.add(direct);
        }
        var container = annotation(owner, "react4j.css.CssImports");
        if (null != container) {
            for (var item : (List<?>) value(container, "value").getValue()) {
                mirrors.add((AnnotationMirror) ((AnnotationValue) item).getValue());
            }
        }
        var ids = new LinkedHashSet<String>();
        for (var mirror : mirrors) {
            String property = selection(mirror, "property");
            String id = selection(mirror, "id");
            String font = selection(mirror, "font");
            if (!font.isEmpty() && (!id.isEmpty() || !property.isEmpty())) {
                throw new IllegalArgumentException("@CssImport font, id and property are mutually exclusive");
            }
            if (!property.isEmpty() && !id.isEmpty()) {
                throw new IllegalArgumentException("@CssImport id and property are mutually exclusive");
            }
            if (!id.isEmpty()) {
                CssCompiler.checkAlias(id, Kind.COMPONENT);
                var alias = (String) value(mirror, "name").getValue();
                String local = alias.isEmpty() ? id : alias;
                CssCompiler.checkAlias(local, Kind.COMPONENT);
                if (!ids.add(local)) {
                    throw new IllegalArgumentException("Duplicate imported ID: " + local);
                }
            }
        }
        return mirrors;
    }

    private String selection(AnnotationMirror mirror, String name) {
        var selected = (String) value(mirror, name).getValue();
        if (selected.isEmpty()
                && mirror.getElementValues().keySet().stream()
                        .anyMatch(key -> key.getSimpleName().contentEquals(name))) {
            throw new IllegalArgumentException(
                    "@CssImport " + name + " must not be empty; omit it to import a definition");
        }
        return selected;
    }

    private record Utilities(String owner, Map<String, String> classes) {}

    private @Nullable Utilities utilitySelection(TypeElement owner, @Nullable CssDescriptor descriptor) {
        var selected = annotation(owner, "react4j.css.UtilityStyles");
        if (null == selected) {
            return null;
        }
        Object target = value(selected, "value").getValue();
        if (!(target instanceof TypeMirror type)
                || !(processingEnv.getTypeUtils().asElement(type) instanceof TypeElement provider)
                || ElementKind.CLASS != provider.getKind()
                || null == annotation(provider, "react4j.css.UtilityCssLibrary")
                || Kind.UTILITY != sourceKind(provider)) {
            throw new IllegalArgumentException("@UtilityStyles must reference a @UtilityCssLibrary class");
        }
        if (null != Trees.instance(processingEnv).getPath(provider)) {
            validateResourceType(provider);
        }
        if (null != nativePrepared(provider)) {
            assert null != descriptor;
            var candidates = descriptor.accessors().stream()
                    .flatMap(accessor -> accessor.utilities().stream())
                    .distinct()
                    .toList();
            var batch = UtilityLanguage.expand(nativeLibrary(provider), candidates);
            if (!batch.diagnostics().isEmpty()) {
                throw new IllegalArgumentException(batch.diagnostics().stream()
                        .map(UtilityLanguage.Diagnostic::render)
                        .collect(Collectors.joining("\n")));
            }
            var fragments = batch.expansions().stream()
                    .map(expansion -> new CssFragments.Fragment(
                            expansion.candidate(),
                            expansion.orderKey(),
                            expansion.css(),
                            expansion.sourceMap(),
                            expansion.metadata()))
                    .toList();
            try {
                writeResource(
                        owner,
                        CssResource.path(binaryName(owner)) + ".fragments.json",
                        new CssFragments(binaryName(owner), fragments).json());
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot publish utility expansion fragments", e);
            }
            var classes = new LinkedHashMap<String, String>();
            batch.expansions().forEach(expansion -> classes.put(expansion.candidate(), expansion.className()));
            return new Utilities(binaryName(provider), Map.copyOf(classes));
        }
        return new Utilities(binaryName(provider), utilityClasses(provider));
    }

    private Map<String, String> utilityClasses(TypeElement owner) {
        return utilityInventories.computeIfAbsent(binaryName(owner), ignored -> inferUtilityClasses(owner));
    }

    private Map<String, String> inferUtilityClasses(TypeElement owner) {
        if (null != nativePrepared(owner)) {
            return Map.of();
        }
        var library = Objects.requireNonNull(annotation(owner, "react4j.css.UtilityCssLibrary"));
        var form = sourceForm(owner, Kind.UTILITY);
        List<String> names;
        if (form.file().isEmpty()) {
            new CssSyntax(form.css(), 0).parse(CssSyntax.Mode.UTILITY);
            names = CssTokens.read(form.css()).stream()
                    .filter(token -> CssResource.SymbolKind.CLASS == token.kind())
                    .map(CssTokens.Token::name)
                    .distinct()
                    .toList();
        } else {
            names = fileInput(owner, form.file()).classes();
        }
        var classes = new LinkedHashMap<String, String>();
        for (var name : names) {
            if (name.isEmpty() || name.chars().anyMatch(Character::isWhitespace)) {
                throw new IllegalArgumentException("Utility class names must be nonempty class-list tokens");
            }
            classes.put(name, CssIdentity.root(binaryName(owner)) + "--class-" + CssIdentity.encode(name));
        }
        var external = new LinkedHashSet<String>();
        for (var entry : (List<?>) value(library, "externalClasses").getValue()) {
            var name = (String) ((AnnotationValue) entry).getValue();
            if (!external.add(name)) {
                throw new IllegalArgumentException("Duplicate external class: " + name);
            }
            if (null == classes.remove(name)) {
                throw new IllegalArgumentException("Unknown external class: " + name);
            }
        }
        return Map.copyOf(classes);
    }

    private UtilityLanguage.@Nullable Prepared nativePrepared(TypeElement owner) {
        return nativeSources
                .computeIfAbsent(binaryName(owner), ignored -> {
                    var form = sourceForm(owner, Kind.UTILITY);
                    if (!form.file().isEmpty()) {
                        return Optional.ofNullable(fileInput(owner, form.file()).rules());
                    }
                    if (!UtilityLanguage.nativeSource(form.css())) {
                        return Optional.empty();
                    }
                    String name = binaryName(owner).replace('.', '/') + ".utilities.css";
                    String map = "";
                    if (null != Trees.instance(processingEnv).getPath(owner)) {
                        try {
                            var input = input(owner, Kind.UTILITY);
                            var writer = new SourceMap.Writer();
                            for (int i = 0; i < input.css().length(); i++) {
                                writer.append(
                                        input.css().substring(i, i + 1),
                                        input.locations().apply(i));
                            }
                            map = writer.map("rules.css");
                        } catch (IOException e) {
                            throw new IllegalArgumentException("Cannot read native rule source", e);
                        }
                    }
                    return Optional.of(UtilityLanguage.prepare(new UtilityLanguage.Source(name, form.css(), map)));
                })
                .orElse(null);
    }

    private Set<String> utilityExternalClasses(TypeElement owner) {
        var names = new LinkedHashSet<String>();
        var mirror = Objects.requireNonNull(annotation(owner, "react4j.css.UtilityCssLibrary"));
        for (var entry : (List<?>) value(mirror, "externalClasses").getValue()) {
            var name = (String) ((AnnotationValue) entry).getValue();
            if (!UtilityLanguage.candidates(name).equals(List.of(name)) || !names.add(name)) {
                throw new IllegalArgumentException("Invalid or duplicate external class: " + name);
            }
        }
        return Set.copyOf(names);
    }

    private UtilityLanguage.Checked nativeLibrary(TypeElement owner) {
        return nativeLibraries.computeIfAbsent(binaryName(owner), ignored -> {
            var prepared = Objects.requireNonNull(nativePrepared(owner));
            var imports = imports(owner, Kind.UTILITY);
            var named = new LinkedHashMap<String, CssUtilityBinding.Import>();
            imports.named()
                    .forEach((name, imported) -> named.put(
                            name, new CssUtilityBinding.Import(imported.owner(), imported.kind(), imported.stable())));
            var context = new CssUtilityBinding.Context(
                    utilityExternalProperties(owner),
                    utilityExternalNames(owner, "externalAnimations", false),
                    utilityExternalNames(owner, "externalFonts", true),
                    utilityExternalNames(owner, "externalContainers", false),
                    utilityExternalNames(owner, "externalAnchors", false),
                    named,
                    imports.properties(),
                    imports.ids(),
                    imports.assets(),
                    imports.fonts(),
                    imports.globals());
            var exports = exports(resourceDescriptor(owner));
            var checked = UtilityLanguage.bind(
                    prepared,
                    binaryName(owner),
                    new UtilityLanguage.Bindings(
                            utilityExternalClasses(owner), context, exports.ids(), exports.animations()));
            if (null == Trees.instance(processingEnv).getPath(owner)) {
                String path = CssResource.path(binaryName(owner));
                String publishedJson = classpathResource(path + ".rules.json");
                if (null != publishedJson) {
                    var published = UtilityLanguage.readChecked(publishedJson);
                    if (!checked.fingerprint().equals(published.fingerprint())) {
                        throw new IllegalArgumentException(
                                "Native utility descriptor differs from provider bindings: " + binaryName(owner));
                    }
                    return published;
                }
                if (null != classpathResource(path + ".json")) {
                    throw new IllegalArgumentException(
                            "Missing checked native utility descriptor: " + binaryName(owner));
                }
                // Header jars carry annotations and explicit prepared inputs; their checked snapshot is rebuilt.
            }
            return checked;
        });
    }

    private @Nullable String classpathResource(String path) {
        try (var input = processingEnv
                .getFiler()
                .getResource(StandardLocation.CLASS_PATH, "", path)
                .openInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read compiled CSS resource: " + path, e);
        }
    }

    private void emitNativeProvider(
            TypeElement owner, @Nullable CssDescriptor descriptor, Imports imports, UtilityLanguage.Checked checked)
            throws IOException {
        var support = checked.support();
        var metadata = support.metadata().withFingerprint(checked.fingerprint());
        String implementation = "";
        var bound = new ArrayList<CssResource.Binding>();
        if (null != descriptor) {
            validateStableImports(owner, descriptor, imports);
            var values = new LinkedHashMap<String, String>();
            for (var accessor : descriptor.accessors()) {
                var kind = CssResource.SymbolKind.valueOf(accessor.kind().name());
                String name = accessor.name();
                var property = imports.properties().get(name);
                var id = imports.ids().get(name);
                var animation = imports.named().get(name);
                String value = CssResource.SymbolKind.PROPERTY == kind && null != property
                        ? property.binding().cssName()
                        : CssResource.SymbolKind.ID == kind && null != id
                                ? id.binding().cssName()
                                : CssResource.SymbolKind.ANIMATION == kind && null != animation
                                        ? animation.cssName()
                                        : metadata.symbols().stream()
                                                .filter(symbol -> kind == symbol.kind() && name.equals(symbol.name()))
                                                .map(CssResource.Symbol::value)
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                        "CSS accessor has no matching native symbol: " + name));
                values.put(accessor.method().getSimpleName().toString(), value);
                bound.add(new CssResource.Binding(
                        accessor.method().getSimpleName().toString(),
                        kind,
                        value,
                        bindingProviders(accessor, imports),
                        composition(owner, accessor, imports, null)));
            }
            var generated = CssGenerator.buildType(processingEnv, descriptor, values);
            emitTypeSpec(descriptor.packageName(), generated.type());
            implementation = generated.implementation();
        }
        metadata = new CssResource(
                metadata.owner(),
                metadata.kind(),
                metadata.dependencies(),
                metadata.includes(),
                implementation,
                bound,
                metadata.symbols(),
                metadata.occurrences(),
                metadata.fontUses(),
                checked.fingerprint());
        String path = CssResource.path(binaryName(owner));
        String file = path.substring(path.lastIndexOf('/') + 1);
        var map = JsonParser.parseString(support.sourceMap()).getAsJsonObject();
        map.addProperty("file", file);
        writeResource(owner, path, support.css() + SourceMap.reference(file));
        writeResource(owner, path + ".map", map.toString());
        writeResource(owner, path + ".json", metadata.json());
        writeResource(owner, path + ".rules.json", checked.json());
    }

    private void writeResource(TypeElement owner, String path, String text) throws IOException {
        try (var stream = processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", path, owner)
                .openOutputStream()) {
            stream.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Set<String> utilityExternalProperties(TypeElement owner) {
        var mirror = Objects.requireNonNull(annotation(owner, "react4j.css.UtilityCssLibrary"));
        var declared = (List<?>) value(mirror, "externalProperties").getValue();
        if (declared.isEmpty()) {
            return Set.of();
        }
        var candidates = CssTokens.read(sourceValue(owner, Kind.UTILITY)).stream()
                .filter(token -> CssResource.SymbolKind.PROPERTY == token.kind()
                        && token.name().startsWith("--"))
                .map(token -> token.name().substring(2))
                .collect(Collectors.toSet());
        var external = new LinkedHashSet<String>();
        for (var item : declared) {
            var name = (String) ((AnnotationValue) item).getValue();
            CssProperties.checkName(name);
            if (!external.add(name)) {
                throw new IllegalArgumentException("Duplicate external custom property: " + name);
            }
            if (null == nativePrepared(owner) && !candidates.contains(name)) {
                throw new IllegalArgumentException("Unknown external custom property: " + name);
            }
        }
        return Set.copyOf(external);
    }

    private Set<String> utilityExternalNames(TypeElement owner, String field, boolean font) {
        var mirror = Objects.requireNonNull(annotation(owner, "react4j.css.UtilityCssLibrary"));
        var names = new LinkedHashSet<String>();
        for (var item : (List<?>) value(mirror, field).getValue()) {
            String name = (String) ((AnnotationValue) item).getValue();
            if (font) {
                name = CssFontValues.canonical(name);
            } else if ("externalAnchors".equals(field)) {
                CssAnchors.checkName(name);
            } else if ("externalContainers".equals(field)) {
                CssContainers.checkName(name);
            } else if (name.isEmpty() || name.codePoints().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Invalid external animation: " + name);
            }
            if (!names.add(name)) {
                throw new IllegalArgumentException("Duplicate " + field + " name: " + name);
            }
        }
        return Set.copyOf(names);
    }

    private TypeElement importTarget(AnnotationMirror mirror) {
        Object target = value(mirror, "value").getValue();
        if (!(target instanceof TypeMirror type)
                || !(processingEnv.getTypeUtils().asElement(type) instanceof TypeElement element)
                || ElementKind.CLASS != element.getKind()
                || (null == annotation(element, CSS_ANNOTATION)
                        && null == annotation(element, "react4j.css.UtilityCssLibrary")
                        && null == annotation(element, "react4j.css.UtilityStyles")
                        && null == annotation(element, "react4j.css.GlobalCss")
                        && null == annotation(element, "react4j.css.Keyframes")
                        && null == annotation(element, "react4j.css.Assets"))) {
            throw new IllegalArgumentException(
                    "@CssImport must reference a @Css, @GlobalCss, @Keyframes or @Assets class: " + target);
        }
        if (null == annotation(element, "react4j.css.Assets")) {
            sourceKind(element);
        }
        return element;
    }

    private CssProperties.Binding resolveProperty(TypeElement owner, String name, Set<String> visiting) {
        if (Kind.UTILITY == sourceKind(owner)
                && utilityExternalProperties(owner).contains(name)) {
            throw new IllegalArgumentException(
                    "External custom property cannot be imported as an owned symbol: " + name);
        }
        String key = binaryName(owner) + "#" + name;
        if (!visiting.add(key)) {
            throw new IllegalArgumentException("Cyclic custom property import without an owner: " + key);
        }
        if (!propertyNames
                .computeIfAbsent(
                        binaryName(owner),
                        ignored -> CssProperties.names(sourceValue(owner, sourceKind(owner)), sourceKind(owner)))
                .contains(name)) {
            throw new IllegalArgumentException("Unknown custom property " + name + " in " + binaryName(owner));
        }
        boolean stable = stable(owner, CssResource.SymbolKind.PROPERTY, name);
        CssProperties.Binding binding = new CssProperties.Binding(binaryName(owner), name, sourceKind(owner), stable);
        boolean imported = false;
        for (var mirror : importMirrors(owner)) {
            var property = (String) value(mirror, "property").getValue();
            var alias = (String) value(mirror, "name").getValue();
            if (!property.isEmpty() && name.equals(alias.isEmpty() ? property : alias)) {
                if (imported) {
                    throw new IllegalArgumentException("Duplicate imported custom property: " + name);
                }
                imported = true;
                CssProperties.checkName(property);
                binding = resolveProperty(importTarget(mirror), property, visiting);
            }
        }
        visiting.remove(key);
        return binding;
    }

    private CssIds.Import resolveId(TypeElement owner, String name, Set<String> visiting) {
        String ownerName = binaryName(owner);
        String key = ownerName + "#" + name;
        if (!visiting.add(key)) {
            throw new IllegalArgumentException("Cyclic ID import without an owner: " + key);
        }
        var declared = new LinkedHashSet<String>();
        for (var method : CssMethods.collect(processingEnv, owner)) {
            var annotation = symbolAnnotation(method.declaration());
            if (null != annotation
                    && "react4j.css.CssId".equals(annotation.getAnnotationType().toString())) {
                if (!method.element().getModifiers().contains(Modifier.ABSTRACT)
                        || !method.isAccessor(processingEnv.getTypeUtils())) {
                    throw new IllegalArgumentException(
                            "CSS ID accessors must be abstract, take no arguments and return String");
                }
                declared.add(symbolName(method.declaration(), annotation));
            }
        }
        Kind kind = sourceKind(owner);
        boolean occurs =
                CssIds.names(sourceValue(owner, kind), kind, Set.of(name)).contains(name);
        for (var mirror : importMirrors(owner)) {
            var id = (String) value(mirror, "id").getValue();
            var alias = (String) value(mirror, "name").getValue();
            if (!id.isEmpty() && name.equals(alias.isEmpty() ? id : alias)) {
                if (!occurs && !declared.contains(name)) {
                    throw new IllegalArgumentException("Unused ID import: " + name);
                }
                var resolved = resolveId(importTarget(mirror), id, visiting);
                var providers = new LinkedHashSet<String>();
                providers.add(ownerName);
                providers.addAll(resolved.providers());
                visiting.remove(key);
                return new CssIds.Import(List.copyOf(providers), resolved.binding());
            }
        }
        if (!declared.contains(name) || !occurs) {
            throw new IllegalArgumentException("Unknown ID " + name + " in " + ownerName);
        }
        visiting.remove(key);
        return new CssIds.Import(
                List.of(ownerName),
                new CssIds.Binding(ownerName, name, kind, stable(owner, CssResource.SymbolKind.ID, name)));
    }

    private CssFonts.Import resolveFont(TypeElement owner, String selected, Set<String> visiting) {
        String name = CssFontValues.canonical(selected);
        String ownerName = binaryName(owner);
        String key = ownerName + "#font:" + name;
        if (!visiting.add(key)) {
            throw new IllegalArgumentException("Cyclic font import without an owner: " + key);
        }
        Kind kind = sourceKind(owner);
        if (Kind.UTILITY == kind
                && utilityExternalNames(owner, "externalFonts", true).contains(name)) {
            throw new IllegalArgumentException("External font cannot be imported as an owned symbol: " + name);
        }
        if (CssFonts.names(sourceValue(owner, kind), kind).contains(name)) {
            visiting.remove(key);
            return new CssFonts.Import(ownerName, name, stable(owner, CssResource.SymbolKind.FONT, name));
        }
        for (var mirror : importMirrors(owner)) {
            var font = (String) value(mirror, "font").getValue();
            var alias = (String) value(mirror, "name").getValue();
            if (!font.isEmpty() && name.equals(CssFontValues.canonical(alias.isEmpty() ? font : alias))) {
                var imported = resolveFont(importTarget(mirror), font, visiting);
                visiting.remove(key);
                return imported;
            }
        }
        throw new IllegalArgumentException("Unknown owned font family " + name + " in " + ownerName);
    }

    private Imports imports(TypeElement owner, Kind kind) {
        var mirrors = importMirrors(owner);
        var imports = new LinkedHashMap<String, CssCompiler.Import>();
        var globals = new ArrayList<String>();
        var properties = new LinkedHashMap<String, CssProperties.Import>();
        var ids = new LinkedHashMap<String, CssIds.Import>();
        var assets = new LinkedHashMap<String, String>();
        var assetAliases = new LinkedHashSet<String>();
        var fonts = new LinkedHashMap<String, CssFonts.Import>();
        for (var mirror : mirrors) {
            var alias = (String) value(mirror, "name").getValue();
            var element = importTarget(mirror);
            if (null != annotation(element, "react4j.css.Assets")) {
                if (!selection(mirror, "id").isEmpty()
                        || !selection(mirror, "property").isEmpty()
                        || !selection(mirror, "font").isEmpty()) {
                    throw new IllegalArgumentException(
                            "@Assets imports select a named asset catalogue without id, property or font");
                }
                CssCompiler.checkAlias(alias, Kind.COMPONENT);
                if (null != Trees.instance(processingEnv).getPath(element)) {
                    validateResourceType(element);
                }
                if (!assetAliases.add(alias)) {
                    throw new IllegalArgumentException("Duplicate imported asset catalogue: " + alias);
                }
                var declaration = AssetDeclarations.parse(processingEnv, element);
                for (var binding : declaration.bindings()) {
                    assets.put(alias + "." + binding.method(), binding.path());
                }
                continue;
            }
            Kind targetKind = sourceKind(element);
            if (null != Trees.instance(processingEnv).getPath(element)) {
                if (Kind.COMPONENT == targetKind) {
                    parse(element);
                } else {
                    validateResourceType(element);
                }
            }
            String name = binaryName(element);
            var property = (String) value(mirror, "property").getValue();
            var id = (String) value(mirror, "id").getValue();
            var font = (String) value(mirror, "font").getValue();
            if (!font.isEmpty()) {
                String local = CssFontValues.canonical(alias.isEmpty() ? font : alias);
                if (CssFontValues.RESERVED.contains(local)) {
                    throw new IllegalArgumentException("Reserved imported font family: " + local);
                }
                if (null != fonts.putIfAbsent(local, resolveFont(element, font, new LinkedHashSet<>()))) {
                    throw new IllegalArgumentException("Duplicate imported font family: " + local);
                }
            } else if (!id.isEmpty()) {
                ids.put(alias.isEmpty() ? id : alias, resolveId(element, id, new LinkedHashSet<>()));
            } else if (!property.isEmpty()) {
                CssProperties.checkName(property);
                String local = alias.isEmpty() ? property : alias;
                CssProperties.checkName(local);
                var binding = resolveProperty(element, property, new LinkedHashSet<>());
                if (null != properties.putIfAbsent(local, new CssProperties.Import(name, binding))) {
                    throw new IllegalArgumentException("Duplicate imported custom property: " + local);
                }
            } else if (Kind.UTILITY == targetKind) {
                throw new IllegalArgumentException(
                        "Utility libraries require @UtilityStyles or a checked symbol import");
            } else if (Kind.KEYFRAMES == kind) {
                throw new IllegalArgumentException(
                        "@CssImport is only supported on stylesheet definitions unless importing a property or ID");
            } else if (Kind.GLOBAL == targetKind) {
                if (!alias.isEmpty()) {
                    throw new IllegalArgumentException("Global CSS imports must not declare a name: " + name);
                }
                if (name.equals(binaryName(owner))) {
                    throw new IllegalArgumentException("CSS definitions must not import themselves as global styles");
                }
                if (globals.contains(name)) {
                    throw new IllegalArgumentException("Duplicate imported global stylesheet: " + name);
                }
                globals.add(name);
            } else {
                CssCompiler.checkAlias(alias, targetKind);
                if (null
                        != imports.putIfAbsent(
                                alias,
                                new CssCompiler.Import(
                                        name,
                                        targetKind,
                                        stable(
                                                element,
                                                Kind.KEYFRAMES == targetKind
                                                        ? CssResource.SymbolKind.ANIMATION
                                                        : CssResource.SymbolKind.CLASS,
                                                "")))) {
                    throw new IllegalArgumentException("Duplicate imported CSS name: " + alias);
                }
            }
        }
        return new Imports(
                Map.copyOf(imports),
                List.copyOf(globals),
                Map.copyOf(properties),
                Map.copyOf(ids),
                Map.copyOf(assets),
                Map.copyOf(fonts));
    }

    private CssDescriptor parse(TypeElement owner) {
        if (ElementKind.CLASS != owner.getKind()
                || !owner.getModifiers().contains(Modifier.ABSTRACT)
                || !owner.getTypeParameters().isEmpty()
                || !isType(owner.getSuperclass(), "java.lang.Object")
                || owner.getModifiers().contains(Modifier.PRIVATE)
                || (owner.getNestingKind().isNested() && !owner.getModifiers().contains(Modifier.STATIC))) {
            throw new IllegalArgumentException(
                    "@Css requires an accessible abstract, non-generic style class extending Object; nested classes"
                            + " must be static");
        }
        if (owner.getModifiers().contains(Modifier.SEALED)) {
            throw new IllegalArgumentException("@Css style classes must not be sealed");
        }
        boolean deprecated = processingEnv.getElementUtils().isDeprecated(owner);
        for (var enclosing = owner.getEnclosingElement();
                enclosing instanceof TypeElement;
                enclosing = enclosing.getEnclosingElement()) {
            if (enclosing.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalArgumentException("@Css style classes must be accessible from their package");
            }
            deprecated |= processingEnv.getElementUtils().isDeprecated(enclosing);
        }
        var trees = Trees.instance(processingEnv);
        var methods = new ArrayList<CssMethods.Method>();
        for (var member : owner.getEnclosedElements()) {
            if (ElementKind.FIELD == member.getKind()) {
                throw new IllegalArgumentException(
                        "@Css style classes must not declare fields: " + member.getSimpleName());
            } else if (ElementKind.CONSTRUCTOR == member.getKind()) {
                if (Elements.Origin.EXPLICIT == ElementsUtil.getOrigin(processingEnv.getElementUtils(), member)) {
                    throw new IllegalArgumentException("@Css style classes must not declare constructors");
                }
            }
        }
        var inheritedMethods = CssMethods.collect(processingEnv, owner);
        for (var inherited : inheritedMethods) {
            var method = inherited.element();
            if (null != annotation(inherited.declaration(), "react4j.css.Asset")) {
                throw new IllegalArgumentException("@Asset methods require an @Assets definition");
            }
            if (null != annotation(inherited.declaration(), "react4j.css.UtilityCss")
                    && null == annotation(owner, "react4j.css.UtilityStyles")) {
                throw new IllegalArgumentException("@UtilityCss accessors require @UtilityStyles");
            }
            if (!method.getModifiers().contains(Modifier.ABSTRACT)) {
                if (null != symbolAnnotation(inherited.declaration())
                        || null != annotation(inherited.declaration(), "react4j.css.CssStable")) {
                    throw new IllegalArgumentException(
                            "CSS symbol accessors must be abstract: " + method.getSimpleName());
                }
                continue;
            }
            if (!inherited.isAccessor(processingEnv.getTypeUtils())) {
                throw new IllegalArgumentException("CSS accessor " + method.getSimpleName()
                        + " must take no arguments and return String without type parameters or throws");
            }
            methods.add(inherited);
            deprecated |= processingEnv.getElementUtils().isDeprecated(inherited.element())
                    || processingEnv.getElementUtils().isDeprecated(inherited.declaration());
        }
        if (trees.getTree(owner).getMembers().stream()
                .anyMatch(member -> member instanceof BlockTree block && !block.isStatic())) {
            throw new IllegalArgumentException("@Css style classes must not declare instance initializers");
        }

        if (methods.stream().noneMatch(m -> m.element().getSimpleName().contentEquals("style"))) {
            throw new IllegalArgumentException("@Css style classes require abstract String style()");
        }
        return new CssDescriptor(
                owner,
                processingEnv.getElementUtils().getBinaryName(owner).toString(),
                parseAccessors(methods, Kind.COMPONENT),
                CssMethods.publicFactory(processingEnv, owner, inheritedMethods, "@Css"),
                deprecated);
    }

    private @Nullable AnnotationMirror symbolAnnotation(Element member) {
        AnnotationMirror found = null;
        for (var name : List.of("CssId", "CssAnimation", "CssCustomProperty", "UtilityCss")) {
            var annotation = annotation(member, "react4j.css." + name);
            if (null != annotation) {
                if (null != found) {
                    throw new IllegalArgumentException(
                            "CSS accessors must declare only one symbol annotation: " + member.getSimpleName());
                }
                found = annotation;
            }
        }
        return found;
    }

    private List<CssAccessor> parseAccessors(List<CssMethods.Method> methods, Kind ownerKind) {
        var names = new LinkedHashSet<String>();
        var accessors = new ArrayList<CssAccessor>();
        for (var inherited : methods) {
            var method = inherited.element();
            var annotation = symbolAnnotation(inherited.declaration());
            if (null != annotation
                    && "react4j.css.UtilityCss"
                            .equals(annotation.getAnnotationType().toString())) {
                var value = (String) value(annotation, "value").getValue();
                var utilities = Arrays.stream(value.split("[\\t\\n\\r\\f ]+"))
                        .filter(token -> !token.isEmpty())
                        .toList();
                if (utilities.isEmpty()) {
                    throw new IllegalArgumentException("@UtilityCss requires a nonempty class list");
                }
                if (new LinkedHashSet<>(utilities).size() != utilities.size()) {
                    throw new IllegalArgumentException("@UtilityCss classes must be distinct");
                }
                accessors.add(new CssAccessor(
                        method, CssAccessor.Kind.CLASS, method.getSimpleName().toString(), utilities));
                continue;
            }
            var kind = null == annotation
                    ? CssAccessor.Kind.CLASS
                    : switch (annotation.getAnnotationType().toString()) {
                        case "react4j.css.CssId" -> CssAccessor.Kind.ID;
                        case "react4j.css.CssAnimation" -> CssAccessor.Kind.ANIMATION;
                        default -> CssAccessor.Kind.PROPERTY;
                    };
            String name = method.getSimpleName().toString();
            if (null != annotation) {
                if ("style".equals(name)) {
                    throw new IllegalArgumentException("style() must be an unannotated component class accessor");
                }
                name = symbolName(method, annotation);
                if (CssAccessor.Kind.PROPERTY == kind) {
                    CssProperties.checkName(name);
                } else if (CssAccessor.Kind.ANIMATION == kind && Kind.KEYFRAMES == ownerKind) {
                    if (!"<default>".equals(value(annotation, "name").getValue())) {
                        throw new IllegalArgumentException(
                                "@CssAnimation on @Keyframes exposes its own name and must not declare name");
                    }
                    name = "";
                } else {
                    CssCompiler.checkAlias(name, CssAccessor.Kind.ANIMATION == kind ? Kind.KEYFRAMES : Kind.COMPONENT);
                }
                if (!names.add(kind + ":" + name)) {
                    throw new IllegalArgumentException("Duplicate CSS symbol accessor: " + name);
                }
            }
            accessors.add(new CssAccessor(method, kind, name));
        }
        return List.copyOf(accessors);
    }

    private String symbolName(ExecutableElement method, AnnotationMirror annotation) {
        String name = (String) value(annotation, "name").getValue();
        if ("<default>".equals(name)) {
            name = method.getSimpleName().toString();
            if (name.matches("get[A-Z].*")) {
                name = name.substring(3, 4).toLowerCase(Locale.ROOT) + name.substring(4);
            }
        }
        return name;
    }

    private @Nullable CssDescriptor resourceDescriptor(TypeElement owner) {
        var methods = new ArrayList<CssMethods.Method>();
        boolean deprecated = processingEnv.getElementUtils().isDeprecated(owner);
        for (var enclosing = owner.getEnclosingElement();
                enclosing instanceof TypeElement;
                enclosing = enclosing.getEnclosingElement()) {
            deprecated |= processingEnv.getElementUtils().isDeprecated(enclosing);
        }
        var inheritedMethods = CssMethods.collect(processingEnv, owner);
        for (var inherited : inheritedMethods) {
            var method = inherited.element();
            if (null != annotation(inherited.declaration(), "react4j.css.UtilityCss")) {
                throw new IllegalArgumentException("@UtilityCss accessors require @UtilityStyles");
            }
            if (null != annotation(inherited.declaration(), "react4j.css.Asset")) {
                throw new IllegalArgumentException("@Asset methods require an @Assets definition");
            }
            if (null != symbolAnnotation(inherited.declaration())
                    || null != annotation(inherited.declaration(), "react4j.css.CssStable")) {
                if (!method.getModifiers().contains(Modifier.ABSTRACT)
                        || !inherited.isAccessor(processingEnv.getTypeUtils())) {
                    throw new IllegalArgumentException(
                            "CSS symbol accessors must be abstract, take no arguments and return String without"
                                    + " type parameters or throws");
                }
                if (null == symbolAnnotation(inherited.declaration())) {
                    throw new IllegalArgumentException(
                            "CSS resource abstract methods require a symbol annotation: " + method.getSimpleName());
                }
                methods.add(inherited);
                deprecated |= processingEnv.getElementUtils().isDeprecated(inherited.element())
                        || processingEnv.getElementUtils().isDeprecated(inherited.declaration());
            } else if (method.getModifiers().contains(Modifier.ABSTRACT)) {
                throw new IllegalArgumentException(
                        "CSS resource abstract methods require a symbol annotation: " + method.getSimpleName());
            }
        }
        if (methods.isEmpty()) {
            return null;
        }
        if (!owner.getModifiers().contains(Modifier.ABSTRACT)
                || owner.getModifiers().contains(Modifier.FINAL)) {
            throw new IllegalArgumentException("CSS resource bindings require an abstract, non-final class");
        }

        return new CssDescriptor(
                owner,
                binaryName(owner),
                parseAccessors(methods, sourceKind(owner)),
                CssMethods.publicFactory(processingEnv, owner, inheritedMethods, "CSS"),
                deprecated);
    }

    private Map<String, String> bindings(CssDescriptor descriptor, CssCompiler.Result result, Imports imports) {
        var bindings = new LinkedHashMap<String, String>();
        for (var accessor : descriptor.accessors()) {
            String method = accessor.method().getSimpleName().toString();
            String name = accessor.name();
            String token =
                    switch (accessor.kind()) {
                        case CLASS ->
                            descriptor.rootClassName()
                                    + ("style".equals(name)
                                            ? ""
                                            : " " + descriptor.rootClassName() + "--" + CssIdentity.encode(name));
                        case PROPERTY -> result.properties().get(name);
                        case ID ->
                            imports.ids().containsKey(name)
                                    ? imports.ids().get(name).binding().cssName()
                                    : result.ids().get(name);
                        case ANIMATION -> result.animations().get(name);
                    };
            if (null == token) {
                throw new IllegalArgumentException(
                        "CSS " + accessor.kind().name().toLowerCase(Locale.ROOT) + " accessor " + method
                                + "() has no matching CSS occurrence: " + name);
            }
            bindings.put(method, token);
        }
        return bindings;
    }

    private boolean stable(TypeElement owner, CssResource.SymbolKind kind, String name) {
        if (null != annotation(owner, "react4j.css.CssStable")) {
            return true;
        }
        return methodStable(owner, kind, name);
    }

    private boolean methodStable(TypeElement owner, CssResource.SymbolKind kind, String name) {
        for (var method : CssMethods.collect(processingEnv, owner)) {
            var declaration = method.declaration();
            if (null == annotation(declaration, "react4j.css.CssStable")) {
                continue;
            }
            var annotation = symbolAnnotation(declaration);
            var symbolKind = null == annotation
                    ? CssResource.SymbolKind.CLASS
                    : switch (annotation.getAnnotationType().toString()) {
                        case "react4j.css.UtilityCss" -> CssResource.SymbolKind.CLASS;
                        case "react4j.css.CssId" -> CssResource.SymbolKind.ID;
                        case "react4j.css.CssAnimation" -> CssResource.SymbolKind.ANIMATION;
                        default -> CssResource.SymbolKind.PROPERTY;
                    };
            String symbolName = null == annotation
                            || "react4j.css.UtilityCss"
                                    .equals(annotation.getAnnotationType().toString())
                    ? declaration.getSimpleName().toString()
                    : symbolName(declaration, annotation);
            if (CssResource.SymbolKind.CLASS == symbolKind && "style".equals(symbolName)
                    || CssResource.SymbolKind.ANIMATION == symbolKind && Kind.KEYFRAMES == sourceKind(owner)) {
                symbolName = "";
            }
            if (symbolKind == kind && symbolName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void validateStableImports(TypeElement owner, CssDescriptor descriptor, Imports imports) {
        for (var accessor : descriptor.accessors()) {
            var kind = CssResource.SymbolKind.valueOf(accessor.kind().name());
            if (!methodStable(owner, kind, accessor.name())) {
                continue;
            }
            var property = imports.properties().get(accessor.name());
            var animation = imports.named().get(accessor.name());
            var id = imports.ids().get(accessor.name());
            if (CssResource.SymbolKind.PROPERTY == kind
                            && null != property
                            && !property.binding().stable()
                    || CssResource.SymbolKind.ANIMATION == kind && null != animation && !animation.stable()
                    || CssResource.SymbolKind.ID == kind
                            && null != id
                            && !id.binding().stable()) {
                throw new IllegalArgumentException(
                        "@CssStable importing accessor conflicts with its defining owner's policy: "
                                + accessor.method().getSimpleName());
            }
        }
    }

    private List<CssResource.Symbol> symbols(
            TypeElement owner,
            Kind kind,
            @Nullable CssDescriptor descriptor,
            CssCompiler.Result result,
            Imports imports,
            Map<CssSymbols.Key, CssSymbols.Definition> definitions) {
        var symbols = new ArrayList<CssResource.Symbol>();
        if (Kind.UTILITY == kind) {
            definitions.values().stream()
                    .filter(d -> CssResource.SymbolKind.CLASS == d.symbol().kind())
                    .distinct()
                    .forEach(d -> symbols.add(new CssResource.Symbol(
                            d.symbol().kind(),
                            d.symbol().name(),
                            d.readable(),
                            stable(owner, d.symbol().kind(), d.symbol().name()))));
        }
        if (Kind.COMPONENT == kind) {
            String root = CssIdentity.root(binaryName(owner));
            symbols.add(new CssResource.Symbol(
                    CssResource.SymbolKind.CLASS, "", root, stable(owner, CssResource.SymbolKind.CLASS, "")));
            assert null != descriptor;
            descriptor.accessors().stream()
                    .filter(accessor -> CssAccessor.Kind.CLASS == accessor.kind() && !"style".equals(accessor.name()))
                    .forEach(accessor -> symbols.add(new CssResource.Symbol(
                            CssResource.SymbolKind.CLASS,
                            accessor.name(),
                            root + "--" + CssIdentity.encode(accessor.name()),
                            stable(owner, CssResource.SymbolKind.CLASS, accessor.name()))));
        }
        result.properties().forEach((name, value) -> {
            if (!imports.properties().containsKey(name)) {
                symbols.add(new CssResource.Symbol(
                        CssResource.SymbolKind.PROPERTY,
                        name,
                        value,
                        stable(owner, CssResource.SymbolKind.PROPERTY, name)));
            }
        });
        result.ids().forEach((name, value) -> {
            if (!imports.ids().containsKey(name)) {
                symbols.add(new CssResource.Symbol(
                        CssResource.SymbolKind.ID, name, value, stable(owner, CssResource.SymbolKind.ID, name)));
            }
        });
        result.animations().forEach((name, value) -> {
            if (!imports.named().containsKey(name)) {
                symbols.add(new CssResource.Symbol(
                        CssResource.SymbolKind.ANIMATION,
                        name,
                        value,
                        stable(owner, CssResource.SymbolKind.ANIMATION, name)));
            }
        });
        result.fonts().forEach((name, value) -> {
            if (!imports.fonts().containsKey(name)) {
                symbols.add(new CssResource.Symbol(
                        CssResource.SymbolKind.FONT, name, value, stable(owner, CssResource.SymbolKind.FONT, name)));
            }
        });
        result.containers()
                .forEach((name, value) -> symbols.add(new CssResource.Symbol(
                        CssResource.SymbolKind.CONTAINER,
                        name,
                        value,
                        stable(owner, CssResource.SymbolKind.CONTAINER, name))));
        return symbols.stream()
                .sorted(Comparator.comparing(CssResource.Symbol::kind).thenComparing(CssResource.Symbol::name))
                .toList();
    }

    private static CssCompiler.Exports exports(@Nullable CssDescriptor descriptor) {
        return null == descriptor ? new CssCompiler.Exports(List.of(), Set.of(), Set.of()) : descriptor.exports();
    }

    private CssCompiler.@Nullable Result compileCss(
            TypeElement owner, Kind kind, @Nullable CssDescriptor descriptor, Imports imports) throws IOException {
        if (Kind.COMPONENT == kind && null == annotation(owner, CSS_ANNOTATION)) {
            assert null != descriptor;
            return CssCompiler.component(
                    "",
                    descriptor.rootClassName(),
                    exports(descriptor),
                    offset -> new SourceMap.Location(binaryName(owner), "", 0, 0),
                    imports.named(),
                    imports.properties(),
                    imports.ids(),
                    new CssCompiler.Resources(imports.assets(), imports.fonts()));
        }
        var input = input(owner, kind);
        try {
            if (Kind.UTILITY == kind) {
                return CssCompiler.utility(
                        input.css(),
                        CssIdentity.root(binaryName(owner)),
                        exports(descriptor),
                        input.locations(),
                        utilityClasses(owner).keySet(),
                        utilityExternalProperties(owner),
                        utilityExternalNames(owner, "externalAnimations", false),
                        utilityExternalNames(owner, "externalFonts", true),
                        utilityExternalNames(owner, "externalContainers", false),
                        imports.named(),
                        imports.properties(),
                        imports.ids(),
                        new CssCompiler.Resources(imports.assets(), imports.fonts()));
            }
            if (Kind.KEYFRAMES == kind) {
                return CssCompiler.keyframes(
                        input.css(),
                        CssIdentity.root(binaryName(owner)),
                        exports(descriptor),
                        input.locations(),
                        imports.properties(),
                        imports.ids(),
                        new CssCompiler.Resources(imports.assets(), imports.fonts()));
            }
            if (Kind.GLOBAL == kind) {
                return CssCompiler.global(
                        input.css(),
                        CssIdentity.root(binaryName(owner)),
                        exports(descriptor),
                        input.locations(),
                        imports.named(),
                        imports.properties(),
                        imports.ids(),
                        new CssCompiler.Resources(imports.assets(), imports.fonts()));
            }
            assert null != descriptor;
            return CssCompiler.component(
                    input.css(),
                    descriptor.rootClassName(),
                    exports(descriptor),
                    input.locations(),
                    imports.named(),
                    imports.properties(),
                    imports.ids(),
                    new CssCompiler.Resources(imports.assets(), imports.fonts()));
        } catch (CssSyntax.InvalidCss e) {
            var origin =
                    input.locations().apply(Math.max(0, Math.min(input.css().length(), e.offset())));
            String location = input.diagnosticPath() + ":" + (origin.line() + 1) + ":" + (origin.column() + 1);
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.ERROR,
                            location + ": " + e.getMessage(),
                            owner,
                            input.annotation(),
                            input.value());
            return null;
        }
    }

    private record SourceForm(String css, String file) {}

    private SourceForm sourceForm(TypeElement owner, Kind kind) {
        var mirror = Objects.requireNonNull(annotation(owner, annotationName(kind)));
        var css = (String) value(mirror, "value").getValue();
        if (Kind.GLOBAL != kind && Kind.UTILITY != kind) {
            return new SourceForm(css, "");
        }
        var file = (String) value(mirror, "file").getValue();
        boolean inline = mirror.getElementValues().keySet().stream()
                .anyMatch(key -> key.getSimpleName().contentEquals("value"));
        boolean fromFile = mirror.getElementValues().keySet().stream()
                .anyMatch(key -> key.getSimpleName().contentEquals("file"));
        if (inline == fromFile || fromFile && file.isEmpty()) {
            throw new IllegalArgumentException(
                    "CSS definitions require exactly one literal text block or nonempty file");
        }
        if (fromFile) {
            AssetResource.validatePath(file);
        }
        return new SourceForm(css, file);
    }

    private record FileInput(String path, String css, List<String> classes, UtilityLanguage.@Nullable Prepared rules) {}

    private FileInput fileInput(TypeElement owner, String file) {
        String path = AssetResource.resolve(binaryName(owner), file);
        return fileInputs.computeIfAbsent(path, ignored -> {
            try {
                String resource = CssInput.path(path);
                String css;
                String catalogue;
                try (var input = processingEnv
                        .getFiler()
                        .getResource(StandardLocation.CLASS_PATH, "", resource)
                        .openInputStream()) {
                    css = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                }
                if (UtilityLanguage.nativeSource(css)) {
                    try (var input = processingEnv
                            .getFiler()
                            .getResource(StandardLocation.CLASS_PATH, "", resource + ".rules.json")
                            .openInputStream()) {
                        var prepared =
                                UtilityLanguage.readPrepared(new String(input.readAllBytes(), StandardCharsets.UTF_8));
                        if (!path.equals(prepared.source().name())
                                || !CssInput.normalize(css)
                                        .equals(prepared.source().text())) {
                            throw new IllegalArgumentException(
                                    "Native CSS input descriptor differs from its source: " + path);
                        }
                        return new FileInput(path, css, List.of(), prepared);
                    }
                }
                try (var input = processingEnv
                        .getFiler()
                        .getResource(StandardLocation.CLASS_PATH, "", resource + ".classes.json")
                        .openInputStream()) {
                    catalogue = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                }
                return new FileInput(path, css, CssInput.classes(path, css, catalogue), null);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Missing declared CSS input or catalogue: " + path + ": " + e.getMessage(), e);
            }
        });
    }

    private String sourceValue(TypeElement owner, Kind kind) {
        if (Kind.UTILITY == kind) {
            var nativeRules = nativePrepared(owner);
            if (null != nativeRules) {
                return UtilityLanguage.validationCss(nativeRules);
            }
        }
        var form = sourceForm(owner, kind);
        return form.file().isEmpty()
                ? form.css()
                : CssInput.normalize(fileInput(owner, form.file()).css());
    }

    private record Input(
            String css,
            IntFunction<SourceMap.Location> locations,
            String diagnosticPath,
            AnnotationMirror annotation,
            AnnotationValue value) {}

    private Input input(TypeElement owner, Kind kind) throws IOException {
        var form = sourceForm(owner, kind);
        var mirror = Objects.requireNonNull(annotation(owner, annotationName(kind)));
        if (!form.file().isEmpty()) {
            var file = fileInput(owner, form.file());
            String css = CssInput.normalize(file.css());
            int[] lines = new int[css.length() + 1];
            int[] columns = new int[lines.length];
            for (int i = 0; i < css.length(); i++) {
                boolean newline = '\n' == css.charAt(i);
                lines[i + 1] = lines[i] + (newline ? 1 : 0);
                columns[i + 1] = newline ? 0 : columns[i] + 1;
            }
            return new Input(
                    css,
                    offset -> new SourceMap.Location(file.path(), file.css(), lines[offset], columns[offset]),
                    file.path(),
                    mirror,
                    value(mirror, "file"));
        }
        var trees = Trees.instance(processingEnv);
        var value = value(mirror, "value");
        var unit = trees.getPath(owner).getCompilationUnit();
        var literal = trees.getTree(owner, mirror, value);
        if (!(literal instanceof LiteralTree text) || !(text.getValue() instanceof String css)) {
            throw new IllegalArgumentException("@" + annotationName(kind).substring("react4j.css.".length())
                    + " requires a literal Java text block; constants and concatenation are unsupported");
        }
        String source = unit.getSourceFile().getCharContent(true).toString();
        var start = (int) trees.getSourcePositions().getStartPosition(unit, literal);
        var end = (int) trees.getSourcePositions().getEndPosition(unit, literal);
        var block = TextBlock.decode(source.substring(start, end), start, css);
        String packageName = processingEnv
                .getElementUtils()
                .getPackageOf(owner)
                .getQualifiedName()
                .toString()
                .replace('.', '/');
        String mappedSource = (packageName.isEmpty() ? "" : packageName + "/")
                + Path.of(unit.getSourceFile().getName()).getFileName();
        return new Input(
                block.value(),
                offset -> {
                    int original =
                            block.offsets()[Math.min(offset, block.value().length())];
                    long line = unit.getLineMap().getLineNumber(original);
                    return new SourceMap.Location(mappedSource, source, (int) line - 1, (int)
                            (original - unit.getLineMap().getStartPosition(line)));
                },
                unit.getSourceFile().getName(),
                mirror,
                value);
    }

    private boolean isType(TypeMirror type, String name) {
        return processingEnv.getTypeUtils().asElement(type) instanceof TypeElement element
                && element.getQualifiedName().contentEquals(name);
    }
}
