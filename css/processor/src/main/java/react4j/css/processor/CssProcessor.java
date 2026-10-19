package react4j.css.processor;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import react4j.css.internal.CssResource;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** Validates component CSS in the JVM, then emits static bindings and CSS resources. */
@SupportedAnnotationTypes({
    "react4j.css.Css",
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
        Kind kind = sourceKind(owner);
        CssDescriptor descriptor = Kind.COMPONENT == kind ? parse(owner) : null;
        if (Kind.COMPONENT != kind) {
            validateResourceType(owner);
            descriptor = resourceDescriptor(owner);
        }
        var imports = imports(owner, kind);
        var result = compileCss(owner, kind, descriptor, imports);
        if (null == result) {
            return;
        }
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
        String implementation = "";
        var bound = new ArrayList<CssResource.Binding>();
        if (null != descriptor) {
            validateStableImports(owner, descriptor, imports);
            var values = bindings(descriptor, result, imports);
            var generated = CssGenerator.buildType(processingEnv, descriptor, values);
            emitTypeSpec(descriptor.packageName(), generated.type());
            implementation = generated.implementation();
            for (var accessor : descriptor.accessors()) {
                String method = accessor.method().getSimpleName().toString();
                bound.add(new CssResource.Binding(
                        method,
                        CssResource.SymbolKind.valueOf(accessor.kind().name()),
                        Objects.requireNonNull(values.get(method)),
                        CssAccessor.Kind.ID == accessor.kind() && imports.ids().containsKey(accessor.name())
                                ? imports.ids().get(accessor.name()).providers()
                                : List.of(),
                        composition(owner, accessor, imports)));
            }
        }
        String ownerName = binaryName(owner);
        String resource = CssResource.path(ownerName);
        String file = resource.substring(resource.lastIndexOf('/') + 1);
        var dependencies = new LinkedHashSet<>(imports.globals());
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
        var providers = new TreeMap<String, List<String>>();
        imports.named().values().stream()
                .filter(binding -> Kind.KEYFRAMES == binding.kind() && dependencies.contains(binding.owner()))
                .forEach(binding -> providers
                        .computeIfAbsent(binding.cssName(), ignored -> new ArrayList<>())
                        .add(binding.owner()));
        imports.properties()
                .values()
                .forEach(binding -> providers
                        .computeIfAbsent(binding.binding().cssName(), ignored -> new ArrayList<>())
                        .add(binding.provider()));
        providers.replaceAll(
                (symbol, owners) -> owners.stream().distinct().sorted().toList());
        var metadata = new CssResource(
                ownerName,
                kind,
                List.copyOf(dependencies),
                imports.globals(),
                implementation,
                bound,
                providers,
                symbols(owner, kind, descriptor, result, imports));
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

    private String binaryName(TypeElement owner) {
        return processingEnv.getElementUtils().getBinaryName(owner).toString();
    }

    private List<CssResource.SymbolId> composition(TypeElement owner, CssAccessor accessor, Imports imports) {
        var kind = CssResource.SymbolKind.valueOf(accessor.kind().name());
        String name = accessor.name();
        String provider = binaryName(owner);
        if (CssResource.SymbolKind.CLASS == kind) {
            var root = new CssResource.SymbolId(provider, kind, "");
            return "style".equals(name) ? List.of(root) : List.of(root, new CssResource.SymbolId(provider, kind, name));
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

    private @Nullable AnnotationMirror annotation(Element owner, String name) {
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
        };
    }

    private Kind sourceKind(TypeElement owner) {
        var kinds = new ArrayList<Kind>();
        for (var kind : Kind.values()) {
            if (null != annotation(owner, annotationName(kind))) {
                kinds.add(kind);
            }
        }
        if (1 != kinds.size()) {
            throw new IllegalArgumentException(
                    "Declare exactly one of @Css, @GlobalCss or @Keyframes on a CSS definition");
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
            Map<String, CssIds.Import> ids) {}

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

    private TypeElement importTarget(AnnotationMirror mirror) {
        Object target = value(mirror, "value").getValue();
        if (!(target instanceof TypeMirror type)
                || !(processingEnv.getTypeUtils().asElement(type) instanceof TypeElement element)
                || ElementKind.CLASS != element.getKind()
                || (null == annotation(element, CSS_ANNOTATION)
                        && null == annotation(element, "react4j.css.GlobalCss")
                        && null == annotation(element, "react4j.css.Keyframes"))) {
            throw new IllegalArgumentException(
                    "@CssImport must reference a @Css, @GlobalCss or @Keyframes class: " + target);
        }
        sourceKind(element);
        return element;
    }

    private CssProperties.Binding resolveProperty(TypeElement owner, String name, Set<String> visiting) {
        String key = binaryName(owner) + "#" + name;
        if (!visiting.add(key)) {
            throw new IllegalArgumentException("Cyclic custom property import without an owner: " + key);
        }
        var css = annotation(owner, annotationName(sourceKind(owner)));
        assert null != css;
        if (!propertyNames
                .computeIfAbsent(
                        binaryName(owner),
                        ignored ->
                                CssProperties.names((String) value(css, "value").getValue(), sourceKind(owner)))
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
        var css = annotation(owner, annotationName(kind));
        assert null != css;
        boolean occurs = CssIds.names((String) value(css, "value").getValue(), kind, Set.of(name))
                .contains(name);
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

    private Imports imports(TypeElement owner, Kind kind) {
        var mirrors = importMirrors(owner);
        var imports = new LinkedHashMap<String, CssCompiler.Import>();
        var globals = new ArrayList<String>();
        var properties = new LinkedHashMap<String, CssProperties.Import>();
        var ids = new LinkedHashMap<String, CssIds.Import>();
        for (var mirror : mirrors) {
            var alias = (String) value(mirror, "name").getValue();
            var element = importTarget(mirror);
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
            if (!id.isEmpty()) {
                ids.put(alias.isEmpty() ? id : alias, resolveId(element, id, new LinkedHashSet<>()));
            } else if (!property.isEmpty()) {
                CssProperties.checkName(property);
                String local = alias.isEmpty() ? property : alias;
                CssProperties.checkName(local);
                var binding = resolveProperty(element, property, new LinkedHashSet<>());
                if (null != properties.putIfAbsent(local, new CssProperties.Import(name, binding))) {
                    throw new IllegalArgumentException("Duplicate imported custom property: " + local);
                }
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
        return new Imports(Map.copyOf(imports), List.copyOf(globals), Map.copyOf(properties), Map.copyOf(ids));
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
                publicFactory(owner, inheritedMethods, "@Css"),
                deprecated);
    }

    private @Nullable AnnotationMirror symbolAnnotation(Element member) {
        AnnotationMirror found = null;
        for (var name : List.of("CssId", "CssAnimation", "CssCustomProperty")) {
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
                publicFactory(owner, inheritedMethods, "CSS"),
                deprecated);
    }

    private boolean publicFactory(TypeElement owner, List<CssMethods.Method> methods, String label) {
        for (var inherited : methods) {
            var method = inherited.element();
            if (method.getSimpleName().contentEquals("get")
                    && method.getParameters().isEmpty()
                    && !method.getModifiers().contains(Modifier.PRIVATE)) {
                if (!method.getModifiers().contains(Modifier.STATIC)
                        || method.getModifiers().contains(Modifier.FINAL)
                        || !processingEnv
                                .getTypeUtils()
                                .isSubtype(owner.asType(), inherited.type().getReturnType())) {
                    throw new IllegalArgumentException(
                            label + " method get() conflicts with the generated singleton factory");
                }
                // Protected factory methods become public in the final generated class.
                return method.getModifiers().contains(Modifier.PUBLIC)
                        || method.getModifiers().contains(Modifier.PROTECTED);
            }
        }
        return false;
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
                                            : " " + descriptor.rootClassName() + "--" + Names.encode(name));
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
                        case "react4j.css.CssId" -> CssResource.SymbolKind.ID;
                        case "react4j.css.CssAnimation" -> CssResource.SymbolKind.ANIMATION;
                        default -> CssResource.SymbolKind.PROPERTY;
                    };
            String symbolName =
                    null == annotation ? declaration.getSimpleName().toString() : symbolName(declaration, annotation);
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
            Imports imports) {
        var symbols = new ArrayList<CssResource.Symbol>();
        if (Kind.COMPONENT == kind) {
            String root = Names.root(binaryName(owner));
            symbols.add(new CssResource.Symbol(
                    CssResource.SymbolKind.CLASS, "", root, stable(owner, CssResource.SymbolKind.CLASS, "")));
            assert null != descriptor;
            descriptor.accessors().stream()
                    .filter(accessor -> CssAccessor.Kind.CLASS == accessor.kind() && !"style".equals(accessor.name()))
                    .forEach(accessor -> symbols.add(new CssResource.Symbol(
                            CssResource.SymbolKind.CLASS,
                            accessor.name(),
                            root + "--" + Names.encode(accessor.name()),
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
        return symbols.stream()
                .sorted(Comparator.comparing(CssResource.Symbol::kind).thenComparing(CssResource.Symbol::name))
                .toList();
    }

    private CssCompiler.Exports exports(TypeElement owner, Kind kind, @Nullable CssDescriptor descriptor) {
        var ids = null == descriptor
                ? Set.<String>of()
                : descriptor.accessors().stream()
                        .filter(accessor -> CssAccessor.Kind.ID == accessor.kind())
                        .map(CssAccessor::name)
                        .collect(Collectors.toSet());
        var authoredIds = new LinkedHashSet<String>();
        var authoredProperties = new LinkedHashSet<String>();
        if (Kind.GLOBAL == kind) {
            ids.stream()
                    .filter(name -> stable(owner, CssResource.SymbolKind.ID, name))
                    .forEach(authoredIds::add);
            var css = annotation(owner, annotationName(kind));
            assert null != css;
            propertyNames
                    .computeIfAbsent(
                            binaryName(owner),
                            ignored -> CssProperties.names(
                                    (String) value(css, "value").getValue(), kind))
                    .stream()
                    .filter(name -> stable(owner, CssResource.SymbolKind.PROPERTY, name))
                    .forEach(authoredProperties::add);
        }
        return null == descriptor
                ? new CssCompiler.Exports(List.of(), Set.of(), Set.of(), authoredIds, authoredProperties)
                : descriptor.exports(authoredIds, authoredProperties);
    }

    private CssCompiler.@Nullable Result compileCss(
            TypeElement owner, Kind kind, @Nullable CssDescriptor descriptor, Imports imports) throws IOException {
        var trees = Trees.instance(processingEnv);
        var annotation = owner.getAnnotationMirrors().stream()
                .filter(a -> annotationName(kind).equals(a.getAnnotationType().toString()))
                .findFirst()
                .orElseThrow();
        var value = value(annotation, "value");
        var path = trees.getPath(owner);
        var unit = path.getCompilationUnit();
        var literal = trees.getTree(owner, annotation, value);
        if (!(literal instanceof LiteralTree text) || !(text.getValue() instanceof String css)) {
            throw new IllegalArgumentException("@" + annotationName(kind).substring("react4j.css.".length())
                    + " requires a literal Java text block; constants and concatenation are unsupported");
        }
        String source = unit.getSourceFile().getCharContent(true).toString();
        var start = (int) trees.getSourcePositions().getStartPosition(unit, literal);
        var end = (int) trees.getSourcePositions().getEndPosition(unit, literal);
        var block = TextBlock.decode(source.substring(start, end), start, css);
        String sourceName = processingEnv
                .getElementUtils()
                .getPackageOf(owner)
                .getQualifiedName()
                .toString()
                .replace('.', '/');
        sourceName = (sourceName.isEmpty() ? "" : sourceName + "/")
                + Path.of(unit.getSourceFile().getName()).getFileName();
        final String mappedSource = sourceName;
        try {
            IntFunction<SourceMap.Location> locations = offset -> {
                int original = block.offsets()[Math.min(offset, block.value().length())];
                long line = unit.getLineMap().getLineNumber(original);
                return new SourceMap.Location(mappedSource, source, (int) line - 1, (int)
                        (original - unit.getLineMap().getStartPosition(line)));
            };
            if (Kind.KEYFRAMES == kind) {
                return CssCompiler.keyframes(
                        block.value(),
                        Names.root(binaryName(owner)),
                        exports(owner, kind, descriptor),
                        locations,
                        imports.properties(),
                        imports.ids());
            }
            if (Kind.GLOBAL == kind) {
                return CssCompiler.global(
                        block.value(),
                        Names.root(binaryName(owner)),
                        exports(owner, kind, descriptor),
                        locations,
                        imports.named(),
                        imports.properties(),
                        imports.ids());
            }
            assert null != descriptor;
            return CssCompiler.component(
                    block.value(),
                    descriptor.rootClassName(),
                    exports(owner, kind, descriptor),
                    locations,
                    imports.named(),
                    imports.properties(),
                    imports.ids());
        } catch (CssSyntax.InvalidCss e) {
            int offset = block.offsets()[Math.max(0, Math.min(block.value().length(), e.offset()))];
            String location =
                    unit.getSourceFile().getName() + ":" + unit.getLineMap().getLineNumber(offset) + ":"
                            + unit.getLineMap().getColumnNumber(offset);
            processingEnv
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, location + ": " + e.getMessage(), owner, annotation, value);
            return null;
        }
    }

    private boolean isType(TypeMirror type, String name) {
        return processingEnv.getTypeUtils().asElement(type) instanceof TypeElement element
                && element.getQualifiedName().contentEquals(name);
    }
}
