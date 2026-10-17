package react4j.css.processor;

import com.sun.source.tree.LiteralTree;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** Validates component CSS in the JVM, then emits static bindings and CSS resources. */
@SupportedAnnotationTypes("react4j.css.Css")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class CssProcessor extends AbstractProcessor {

    private static final String CSS_ANNOTATION = "react4j.css.Css";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
        var annotationType = processingEnv.getElementUtils().getTypeElement(CSS_ANNOTATION);
        if (null == annotationType) {
            return false;
        }
        for (var element : round.getElementsAnnotatedWith(annotationType)) {
            try {
                process((TypeElement) element);
            } catch (IllegalArgumentException | IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
            }
        }
        return true;
    }

    private void process(TypeElement owner) throws IOException {
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
        var methods = new ArrayList<ExecutableElement>();
        boolean constructor = false;
        for (var member : owner.getEnclosedElements()) {
            if (ElementKind.CONSTRUCTOR == member.getKind()) {
                var candidate = (ExecutableElement) member;
                boolean accessible = candidate.getParameters().isEmpty()
                        && candidate.getThrownTypes().isEmpty()
                        && !candidate.getModifiers().contains(Modifier.PRIVATE);
                constructor |= accessible;
                deprecated |= accessible && processingEnv.getElementUtils().isDeprecated(candidate);
            } else if (ElementKind.METHOD == member.getKind()
                    && member.getModifiers().contains(Modifier.ABSTRACT)) {
                var method = (ExecutableElement) member;
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
        if (!constructor) {
            throw new IllegalArgumentException(
                    "@Css style classes require an accessible no-argument constructor without throws");
        }
        methods.sort(Comparator.comparing(m -> m.getSimpleName().toString()));
        if (methods.stream().noneMatch(m -> m.getSimpleName().contentEquals("style"))) {
            throw new IllegalArgumentException("@Css style classes require abstract String style()");
        }
        var annotation = owner.getAnnotationMirrors().stream()
                .filter(a -> CSS_ANNOTATION.equals(a.getAnnotationType().toString()))
                .findFirst()
                .orElseThrow();
        var value = annotation.getElementValues().values().iterator().next();
        var trees = Trees.instance(processingEnv);
        var path = trees.getPath(owner);
        var unit = path.getCompilationUnit();
        var literal = trees.getTree(owner, annotation, value);
        if (!(literal instanceof LiteralTree text) || !(text.getValue() instanceof String css)) {
            throw new IllegalArgumentException(
                    "@Css requires a literal Java text block; constants and concatenation are unsupported");
        }
        String source = unit.getSourceFile().getCharContent(true).toString();
        var start = (int) trees.getSourcePositions().getStartPosition(unit, literal);
        var end = (int) trees.getSourcePositions().getEndPosition(unit, literal);
        var block = TextBlock.decode(source.substring(start, end), start, css);
        String binaryName = processingEnv.getElementUtils().getBinaryName(owner).toString();
        String root = Names.root(binaryName);
        String sourceName = processingEnv
                .getElementUtils()
                .getPackageOf(owner)
                .getQualifiedName()
                .toString()
                .replace('.', '/');
        sourceName = (sourceName.isEmpty() ? "" : sourceName + "/")
                + Path.of(unit.getSourceFile().getName()).getFileName();
        final String mappedSource = sourceName;
        SourceMap.Writer result;
        try {
            result = CssCompiler.component(
                    block.value(),
                    root,
                    methods.stream().map(m -> m.getSimpleName().toString()).toList(),
                    offset -> {
                        int original =
                                block.offsets()[Math.min(offset, block.value().length())];
                        return new SourceMap.Location(
                                mappedSource,
                                source,
                                (int) unit.getLineMap().getLineNumber(original) - 1,
                                (int) unit.getLineMap().getColumnNumber(original) - 1);
                    });
        } catch (CssSyntax.InvalidCss e) {
            int offset = block.offsets()[Math.max(0, Math.min(block.value().length(), e.offset()))];
            String location =
                    unit.getSourceFile().getName() + ":" + unit.getLineMap().getLineNumber(offset) + ":"
                            + unit.getLineMap().getColumnNumber(offset);
            processingEnv
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, location + ": " + e.getMessage(), owner, annotation, value);
            return;
        }
        String packageName = processingEnv
                .getElementUtils()
                .getPackageOf(owner)
                .getQualifiedName()
                .toString();
        String localName = binaryName.substring(packageName.isEmpty() ? 0 : packageName.length() + 1);
        String generated = "Styled_" + Names.encode(localName);
        String qualified = packageName.isEmpty() ? generated : packageName + "." + generated;
        var java = new StringBuilder();
        if (!packageName.isEmpty()) {
            java.append("package ").append(packageName).append(";\n\n");
        }
        var suppressions = new ArrayList<String>();
        suppressions.add("\"UnnecessarilyFullyQualified\"");
        if (packageName.isEmpty()) {
            suppressions.add("\"DefaultPackage\"");
        }
        if (deprecated) {
            suppressions.add("\"deprecation\"");
            suppressions.add("\"removal\"");
        }
        java.append("@java.lang.SuppressWarnings(")
                .append(1 == suppressions.size() ? suppressions.get(0) : "{" + String.join(", ", suppressions) + "}")
                .append(")\n@org.jspecify.annotations.NullMarked\nfinal class ")
                .append(generated)
                .append(" extends ")
                .append(owner.getQualifiedName())
                .append(" {\n");
        for (var method : methods) {
            String name = method.getSimpleName().toString();
            String visibility = method.getModifiers().contains(Modifier.PUBLIC)
                    ? "public "
                    : method.getModifiers().contains(Modifier.PROTECTED) ? "protected " : "";
            java.append("  @java.lang.Override\n  ")
                    .append(visibility)
                    .append("java.lang.String ")
                    .append(name)
                    .append("() { return \"")
                    .append(root);
            if (!"style".equals(name)) {
                java.append(' ').append(root).append("--").append(Names.encode(name));
            }
            java.append("\"; }\n");
        }
        java.append("}\n");
        try (var writer =
                processingEnv.getFiler().createSourceFile(qualified, owner).openWriter()) {
            writer.write(java.toString());
        }
        String resource = binaryName.replace('.', '/') + ".css";
        String file = resource.substring(resource.lastIndexOf('/') + 1);
        for (var entry : Map.of(resource, result.css() + SourceMap.reference(file), resource + ".map", result.map(file))
                .entrySet()) {
            try (var stream = processingEnv
                    .getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", entry.getKey(), owner)
                    .openOutputStream()) {
                stream.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private boolean isType(TypeMirror type, String name) {
        return processingEnv.getTypeUtils().asElement(type) instanceof TypeElement element
                && element.getQualifiedName().contentEquals(name);
    }
}
