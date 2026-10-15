package react4j.testing;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.errorprone.BaseErrorProneJavaCompiler;
import com.google.errorprone.scanner.BuiltInCheckerSuppliers;
import com.google.errorprone.scanner.ScannerSupplier;
import com.uber.nullaway.JSpecifyUnrecognizedAnnotationLocation;
import com.uber.nullaway.NullAway;
import com.uber.nullaway.RequireExplicitNullMarking;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;

/** Checks generated bodies against compiled fixture declarations and the product checker policy. */
@NullMarked
public final class StrictGeneratedCode {
    private StrictGeneratedCode() {}

    /** Checks every generated Java source using the compiled fixture declarations. */
    public static void check(final Compilation fixture, final List<String> fixtureOptions) throws IOException {
        final var sourceNames = fixture.sourceOutputFilenames().stream()
                .filter(name -> name.endsWith(".java"))
                .map(name -> fixture.sourceOutput().resolve(name).toString())
                .toList();
        if (sourceNames.isEmpty()) {
            return;
        }
        final var options = new ArrayList<>(Files.readAllLines(Path.of("third_party/java/strict_checker_options.txt")));
        final int classpathIndex = fixtureOptions.indexOf("-classpath");
        final String fixtureClasspath =
                classpathIndex < 0 ? System.getProperty("java.class.path") : fixtureOptions.get(classpathIndex + 1);
        options.addAll(List.of(
                "-proc:none",
                "-sourcepath",
                "",
                "-XDcompilePolicy=simple",
                "-classpath",
                fixtureClasspath + File.pathSeparator + fixture.classOutput()));
        final var compiler = new BaseErrorProneJavaCompiler(BuiltInCheckerSuppliers.defaultChecks()
                .plus(ScannerSupplier.fromBugCheckerClasses(
                        NullAway.class,
                        JSpecifyUnrecognizedAnnotationLocation.class,
                        RequireExplicitNullMarking.class)));
        final var systemCompiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(systemCompiler);
        assert null != systemCompiler;
        try (final var manager = systemCompiler.getStandardFileManager(null, null, null)) {
            final var inputs = new ArrayList<JavaFileObject>();
            manager.getJavaFileObjectsFromStrings(sourceNames).forEach(inputs::add);
            final var checked = CompileTestUtil.compile(
                    compiler, inputs, options, Collections.emptyList(), Collections.emptyList());
            try {
                CompileTestUtil.assertNoWarnings(checked.diagnostics());
                CompileTestUtil.assertNoErrors(checked.diagnostics());
                assertTrue(checked.success());
            } finally {
                delete(checked.classOutput());
                delete(checked.sourceOutput());
            }
        }
    }

    private static void delete(final Path directory) throws IOException {
        try (final var paths = Files.walk(directory)) {
            for (final var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
