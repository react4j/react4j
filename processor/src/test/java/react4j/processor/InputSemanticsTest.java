package react4j.processor;

import static org.testng.Assert.*;

import arez.processor.ArezProcessor;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.Test;
import react4j.testing.StrictGeneratedCode;

public final class InputSemanticsTest extends AbstractProcessorTest {
    @Override
    protected String getOptionPrefix() {
        return "react4j";
    }

    @Override
    protected Processor processor() {
        return new React4jProcessor();
    }

    @Override
    protected Processor[] additionalProcessors() {
        return new Processor[] {new ArezProcessor()};
    }

    private static JavaFileObject source(final String declarations, final String inputs) {
        final var text = """
            package test;
            import java.util.*;
            import org.jspecify.annotations.*;
            import react4j.*;
            import react4j.annotations.*;
            @NullMarked
            public final class Case {
              %s
              @View(type=View.Type.STATEFUL)
              static abstract class Component {
                %s
                @Render @Nullable ReactNode render() { return null; }
              }
            }
            """.formatted(declarations, inputs);
        return sourceFile("Case", text);
    }

    private static JavaFileObject sourceFile(final String name, final String text) {
        return new SimpleJavaFileObject(URI.create("string:///test/" + name + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return text;
            }
        };
    }

    @Test
    public void contentArrays() throws Exception {
        final var inputs = "Component(@Input String[] strings, @Input boolean[] booleans, @Input byte[] bytes, "
                + "@Input short[] shorts, @Input int[] ints, @Input long[] longs, @Input char[] chars, "
                + "@Input float[] floats, @Input double[] doubles) {}";
        final var generated = valid("", inputs);
        assertEquals(generated.split("InputKeys.arrayKey", -1).length - 1, 9);
        valid("", "Component(@Input @Nullable String @Nullable [] values) {}");
        valid("", "Component(@Input(fromTreeContext=true) String[] values) {}");
        valid(
                "",
                "Component(@Input String[] values) {} @InputDefault static String[] getValuesDefault() {"
                        + " return new String[]{\"default\"}; }");
        assertFalse(valid("", "Component(@Input Object values) {}").contains("InputKeys"));
        assertFalse(valid("", "Component(@Input String[][] values) {}").contains("InputKeys"));
        assertFalse(valid("", "Component(@Input Object[] values) {}").contains("InputKeys"));
        valid(
                "",
                "Component(@Input String[] values, @Input int count, @Input char letter, @Input long id, @Input float"
                        + " rate, @Input boolean flag, @Input double size, @Input byte tiny, @Input short small, @Input"
                        + " @Nullable Long wrapped) {}");
    }

    private String valid(final String declarations, final String inputs) throws Exception {
        String result = "";
        for (final boolean formatted : List.of(false, true)) {
            final var options = new ArrayList<>(getOptions());
            options.add("-Areact4j.format_generated_source=" + formatted);
            final var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                    List.of(source(declarations, inputs)), options, processors(), List.of());
            try {
                StrictGeneratedCode.check(compilation, options);
                result = Files.readString(compilation.sourceOutput().resolve("test/Case_ComponentBuilder.java"));
            } finally {
                cleanup(compilation);
            }
        }
        return result;
    }

    private static void cleanup(final Compilation compilation) throws IOException {
        for (final var directory : List.of(compilation.sourceOutput(), compilation.classOutput())) {
            try (var files = Files.walk(directory)) {
                for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
