package react4j.processor;

import static org.testng.Assert.*;

import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeVariable;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.Test;

public final class NullabilityTest extends AbstractProcessorTest {
    @Override
    protected String getOptionPrefix() {
        return "react4j";
    }

    @Override
    protected Processor processor() {
        return new React4jProcessor();
    }

    @Override
    protected List<String> getOptions() {
        return List.of(
                "--release", "17", "-Xlint:all,-processing", "-XDaddTypeAnnotationsToSymbol=true", "-sourcepath", "");
    }

    @Test
    public void resolvesGenericBindingsAndIntersectionBounds() throws Exception {
        final boolean[] checked = {false};
        final var inspector = inspector(checked);
        final var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                List.of(fixture("input/com/example/jspecify/NullabilityCases.java")),
                getOptions().stream().filter(option -> !option.startsWith("-A")).toList(),
                List.of(inspector),
                List.of());
        try {
            assertTrue(checked[0]);
        } finally {
            for (final var directory : List.of(compilation.classOutput(), compilation.sourceOutput())) {
                try (final var paths = Files.walk(directory)) {
                    for (final var path :
                            paths.sorted(Comparator.reverseOrder()).toList()) {
                        Files.delete(path);
                    }
                }
            }
        }
    }

    @Test
    public void resolvesCompiledGenericBindingsAndScopes() throws Exception {
        var base = CompileTestUtil.assertCompilesWithoutWarnings(
                List.of(fixture("input/com/example/jspecify/NullabilityCases.java")),
                getOptions(),
                List.of(),
                List.of());
        try {
            boolean[] checked = {false};
            var probe = CompileTestUtil.assertCompilesWithoutWarnings(
                    List.of(fixture("input/com/example/jspecify/NullabilityProbe.java")), getOptions(),
                    List.of(inspector(checked)),
                            buildClasspath(base.classOutput().toFile()));
            try {
                assertTrue(checked[0]);
            } finally {
                delete(probe);
            }
        } finally {
            delete(base);
        }
    }

    private static void delete(Compilation compilation) throws Exception {
        for (var directory : List.of(compilation.sourceOutput(), compilation.classOutput())) {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    private static AbstractProcessor inspector(boolean[] checked) {
        return new AbstractProcessor() {
            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return Set.of("*");
            }

            @Override
            public SourceVersion getSupportedSourceVersion() {
                return SourceVersion.RELEASE_17;
            }

            @Override
            public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment round) {
                if (!round.processingOver()) {
                    checkContracts(processingEnv);
                    checked[0] = true;
                }
                return false;
            }
        };
    }

    private static void checkContracts(final ProcessingEnvironment environment) {
        final var elements = environment.getElementUtils();
        final String name = "com.example.jspecify.NullabilityCases";
        final TypeElement model = elements.getTypeElement(name);
        final var parametric = method(model, "parametric");
        assertEquals(Nullability.resolve(parametric, parametric.getReturnType()), Nullability.Nullness.PARAMETRIC);
        assertFalse(Nullability.isNonNull(parametric));
        assertFalse(Nullability.isNullable(parametric));
        assertThrows(
                ProcessorException.class, () -> Nullability.requireDefinite(parametric, parametric.getReturnType()));
        assertTrue(Nullability.isNullable(method(model, "nullable")));
        assertTrue(Nullability.isNonNull(method(model, "nonnull")));
        assertTrue(Nullability.isNonNull(method(model, "legacyNullable")));
        assertTrue(Nullability.isNonNull(method(model, "legacyJetbrainsNullable")));
        assertTrue(Nullability.isNonNull(method(model, "intersection")));
        assertTrue(Nullability.isNonNull(method(model, "markedIntersection")));

        for (var pair : List.of(
                List.of("strings", "strings"),
                List.of("boundedStrings", "strings"),
                List.of("boundedNullableStrings", "nullableStrings"),
                List.of("nullableStrings", "upperNullableStrings"),
                List.of("strings", "upperNullableStrings"),
                List.of("nullableArrayList", "nullableStrings"),
                List.of("ownerSubclass", "strings"),
                List.of("nullableOwnerSubclass", "nullableStrings"),
                List.of("upperStrings", "upperNullableStrings"),
                List.of("lowerNullableStrings", "lowerStrings"),
                List.of("nestedUpperStrings", "nestedUpperStrings"),
                List.of("stringArray", "nullableArray"),
                List.of("stringArray", "nullableElementArray"),
                List.of("unbounded", "upperObject"))) {
            assertAssignable(environment, model, pair.get(0), pair.get(1), true);
        }
        for (var pair : List.of(
                List.of("nullableStrings", "strings"),
                List.of("boundedNullableStrings", "strings"),
                List.of("boundedIntersection", "strings"),
                List.of("strings", "nullableStrings"),
                List.of("nullableArrayList", "strings"),
                List.of("nullableOwnerSubclass", "strings"),
                List.of("nestedNullableStrings", "nestedStrings"),
                List.of("nullableElementArray", "stringArray"),
                List.of("nullableArray", "stringArray"),
                List.of("upperNullableStrings", "upperStrings"),
                List.of("lowerStrings", "lowerNullableStrings"),
                List.of("nestedUpperStrings", "nestedUpperNullableStrings"),
                List.of("nestedUpperNullableStrings", "nestedUpperStrings"),
                List.of("nullableOwnerStrings", "ownerStrings"))) {
            assertAssignable(environment, model, pair.get(0), pair.get(1), false);
        }

        final TypeElement unmarked = elements.getTypeElement(name + ".Unmarked");
        assertFalse(Nullability.hasNonNullBound(
                (TypeVariable) unmarked.getTypeParameters().get(0).asType()));
        assertThrows(ProcessorException.class, () -> Nullability.isNonNull(method(unmarked, "unspecified")));

        assertThrows(ProcessorException.class, () -> Nullability.isNonNull(method(unmarked, "legacyNonnull")));
        assertThrows(ProcessorException.class, () -> Nullability.isNonNull(method(unmarked, "legacyJetbrainsNonnull")));

        final TypeElement base = elements.getTypeElement(name + ".Base");
        final var value = method(base, "value");
        for (final String child : List.of("NonNullChild", "NullableChild", "Concrete", "Parametric", "Bounded")) {
            final TypeElement context = elements.getTypeElement(name + "." + child);
            final var resolved =
                    (ExecutableType) environment.getTypeUtils().asMemberOf((DeclaredType) context.asType(), value);
            assertEquals(
                    Nullability.isNonNull(value, resolved.getReturnType(), context),
                    !child.equals("NullableChild") && !child.equals("Parametric"),
                    child);
            if (child.equals("NullableChild") || child.equals("NonNullChild")) {
                assertEquals(
                        Nullability.resolve(value, resolved.getReturnType()),
                        child.equals("NullableChild") ? Nullability.Nullness.NULLABLE : Nullability.Nullness.NONNULL);
            }
        }
        final TypeElement unmarkedChild = elements.getTypeElement(name + ".UnmarkedChild");
        final var resolved =
                (ExecutableType) environment.getTypeUtils().asMemberOf((DeclaredType) unmarkedChild.asType(), value);
        assertThrows(
                ProcessorException.class, () -> Nullability.isNonNull(value, resolved.getReturnType(), unmarkedChild));
    }

    private static void assertAssignable(
            ProcessingEnvironment environment,
            TypeElement model,
            String sourceName,
            String targetName,
            boolean expected) {
        var source = method(model, sourceName);
        var target = method(model, targetName);
        assertEquals(
                Nullability.isAssignable(
                        environment.getTypeUtils(),
                        source,
                        source.getReturnType(),
                        target,
                        target.getReturnType(),
                        model),
                expected,
                sourceName + " -> " + targetName);
    }

    private static ExecutableElement method(final TypeElement type, final String name) {
        return type.getEnclosedElements().stream()
                .filter(element -> element.getSimpleName().contentEquals(name))
                .map(element -> (ExecutableElement) element)
                .findFirst()
                .orElseThrow();
    }
}
