package react4j.css.processor;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntFunction;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
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
    "react4j.css.GlobalCss"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class CssProcessor extends AbstractStandardProcessor {

    private static final String CSS_ANNOTATION = "react4j.css.Css";

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
                    var owner = (TypeElement) element;
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
        }
        var imports = imports(owner, kind);
        var result = compileCss(owner, kind, descriptor, imports.named());
        if (null == result) {
            return;
        }
        if (null != descriptor) {
            emitTypeSpec(descriptor.packageName(), CssGenerator.buildType(processingEnv, descriptor));
        }
        String ownerName = binaryName(owner);
        String resource = CssResource.path(ownerName);
        String file = resource.substring(resource.lastIndexOf('/') + 1);
        var dependencies = new ArrayList<>(imports.globals());
        imports.named().entrySet().stream()
                .filter(entry -> result.imports().contains(entry.getKey()))
                .map(entry -> entry.getValue().owner())
                .distinct()
                .sorted()
                .forEach(dependencies::add);
        var metadata = new CssResource(ownerName, kind, dependencies);
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

    private @Nullable AnnotationMirror annotation(TypeElement owner, String name) {
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
                || !owner.getInterfaces().isEmpty()
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
                                    == ElementsUtil.getOrigin(processingEnv.getElementUtils(), member))
                    || (ElementKind.METHOD == member.getKind()
                            && member.getModifiers().contains(Modifier.ABSTRACT))) {
                throw new IllegalArgumentException(
                        "CSS resource definitions must not declare fields, constructors or abstract methods");
            }
        }
        if (Trees.instance(processingEnv).getTree(owner).getMembers().stream().anyMatch(BlockTree.class::isInstance)) {
            throw new IllegalArgumentException("CSS resource definitions must not declare initializers");
        }
    }

    private record Imports(Map<String, CssCompiler.Import> named, List<String> globals) {}

    private Imports imports(TypeElement owner, Kind kind) {
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
        if (Kind.KEYFRAMES == kind && (null != direct || null != container)) {
            throw new IllegalArgumentException("@CssImport is only supported on stylesheet definitions");
        }
        var imports = new LinkedHashMap<String, CssCompiler.Import>();
        var globals = new ArrayList<String>();
        for (var mirror : mirrors) {
            var alias = (String) value(mirror, "name").getValue();
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
            Kind targetKind = sourceKind(element);
            if (null != Trees.instance(processingEnv).getPath(element)) {
                if (Kind.COMPONENT == targetKind) {
                    parse(element);
                } else {
                    validateResourceType(element);
                }
            }
            String name = binaryName(element);
            if (Kind.GLOBAL == targetKind) {
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
                if (null != imports.putIfAbsent(alias, new CssCompiler.Import(name, targetKind))) {
                    throw new IllegalArgumentException("Duplicate imported CSS name: " + alias);
                }
            }
        }
        return new Imports(Map.copyOf(imports), List.copyOf(globals));
    }

    private CssDescriptor parse(TypeElement owner) {
        if (ElementKind.CLASS != owner.getKind()
                || !owner.getModifiers().contains(Modifier.ABSTRACT)
                || !owner.getTypeParameters().isEmpty()
                || !owner.getInterfaces().isEmpty()
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
        var methods = new ArrayList<ExecutableElement>();
        boolean publicFactory = false;
        for (var member : owner.getEnclosedElements()) {
            if (ElementKind.FIELD == member.getKind()) {
                throw new IllegalArgumentException(
                        "@Css style classes must not declare fields: " + member.getSimpleName());
            } else if (ElementKind.CONSTRUCTOR == member.getKind()) {
                if (Elements.Origin.EXPLICIT == ElementsUtil.getOrigin(processingEnv.getElementUtils(), member)) {
                    throw new IllegalArgumentException("@Css style classes must not declare constructors");
                }
            } else if (ElementKind.METHOD == member.getKind()) {
                var method = (ExecutableElement) member;
                if (method.getSimpleName().contentEquals("get")
                        && method.getParameters().isEmpty()
                        && !method.getModifiers().contains(Modifier.PRIVATE)) {
                    if (!method.getModifiers().contains(Modifier.STATIC)
                            || method.getModifiers().contains(Modifier.FINAL)
                            || !processingEnv.getTypeUtils().isSubtype(owner.asType(), method.getReturnType())) {
                        throw new IllegalArgumentException(
                                "@Css method get() conflicts with the generated singleton factory");
                    }
                    // Preserve public/protected access; the checker forbids protected members in a final class.
                    publicFactory = method.getModifiers().contains(Modifier.PUBLIC)
                            || method.getModifiers().contains(Modifier.PROTECTED);
                }
                if (!method.getModifiers().contains(Modifier.ABSTRACT)) {
                    continue;
                }
                if (!method.getParameters().isEmpty()
                        || !method.getTypeParameters().isEmpty()
                        || !method.getThrownTypes().isEmpty()
                        || !isType(method.getReturnType(), "java.lang.String")) {
                    throw new IllegalArgumentException("CSS accessor " + method.getSimpleName()
                            + " must take no arguments and return String without type parameters or throws");
                }
                methods.add(method);
                deprecated |= processingEnv.getElementUtils().isDeprecated(method);
            }
        }
        if (trees.getTree(owner).getMembers().stream()
                .anyMatch(member -> member instanceof BlockTree block && !block.isStatic())) {
            throw new IllegalArgumentException("@Css style classes must not declare instance initializers");
        }
        methods.sort(Comparator.comparing(m -> m.getSimpleName().toString()));
        if (methods.stream().noneMatch(m -> m.getSimpleName().contentEquals("style"))) {
            throw new IllegalArgumentException("@Css style classes require abstract String style()");
        }
        return new CssDescriptor(
                owner,
                processingEnv.getElementUtils().getBinaryName(owner).toString(),
                methods,
                publicFactory,
                deprecated);
    }

    private CssCompiler.@Nullable Result compileCss(
            TypeElement owner, Kind kind, @Nullable CssDescriptor descriptor, Map<String, CssCompiler.Import> imports)
            throws IOException {
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
                return new SourceMap.Location(
                        mappedSource,
                        source,
                        (int) unit.getLineMap().getLineNumber(original) - 1,
                        (int) unit.getLineMap().getColumnNumber(original) - 1);
            };
            if (Kind.KEYFRAMES == kind) {
                return CssCompiler.keyframes(block.value(), Names.root(binaryName(owner)) + "--keyframes", locations);
            }
            if (Kind.GLOBAL == kind) {
                return CssCompiler.global(block.value(), locations, imports);
            }
            assert null != descriptor;
            return CssCompiler.component(
                    block.value(),
                    descriptor.rootClassName(),
                    descriptor.accessors().stream()
                            .map(m -> m.getSimpleName().toString())
                            .toList(),
                    locations,
                    imports);
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
