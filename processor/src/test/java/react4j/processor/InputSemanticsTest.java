package react4j.processor;

import static org.testng.Assert.*;

import arez.processor.ArezProcessor;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
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

    @Test
    public void customKeys() throws Exception {
        final var keys = "public static final class Keys implements InputKeyGenerator<String[]> { "
                + "public String getKey(String[] value) { return String.valueOf(value.length); } }";
        assertTrue(valid(keys, "Component(@Input(keyGenerator=Keys.class) String[] values) {}")
                .contains("$keyGenerator_values.getKey"));
        valid(keys, "Component(@Input(keyGenerator=Keys.class,fromTreeContext=true) String[] values) {}");
        valid(
                keys,
                "Component(@Input(keyGenerator=Keys.class) String[] values) {} @InputDefault "
                        + "static String[] getValuesDefault() { return new String[0]; }");
        valid(
                "public static class Base<T extends @Nullable Object> implements InputKeyGenerator<T> { "
                        + "public String getKey(T value) { return String.valueOf(value); } } "
                        + "public static final class Keys extends Base<String[]> {}",
                "Component(@Input(keyGenerator=Keys.class) String[] values) {}");
        valid(
                "public static final class Keys implements InputKeyGenerator<Integer> { "
                        + "public String getKey(Integer value) { return value.toString(); } }",
                "Component(@Input(keyGenerator=Keys.class) int value) {}");
        valid(
                "public static final class Keys implements InputKeyGenerator<@Nullable String @Nullable []> { public"
                        + " String getKey(@Nullable String @Nullable [] value) { return null == value ? \"null\" :"
                        + " String.valueOf(value.length); } }",
                "Component(@Input(keyGenerator=Keys.class) @Nullable String @Nullable [] values) {}");
    }

    @DataProvider(name = "badKeys")
    public Object[][] badKeys() {
        final var implementation =
                "implements InputKeyGenerator<String[]> { public String getKey(String[] value) { return \"k\"; } }";
        return new Object[][] {
            {
                "public static final class Keys implements InputKeyGenerator { public String getKey(Object value) {"
                        + " return \"k\"; } }",
                "String[]",
                "resolved type argument"
            },
            {
                "public static final class Keys implements InputKeyGenerator<String[]> { public String getKey(String"
                        + " @Nullable [] value) { return \"k\"; } }",
                "String @Nullable []",
                "incompatible"
            },
            {
                "public static final class Keys implements InputKeyGenerator<Integer> { public String getKey(Integer"
                        + " value) { return \"k\"; } }",
                "String[]",
                "incompatible"
            },
            {"public static final class Keys " + implementation, "String @Nullable []", "nullness"},
            {"private static final class Keys " + implementation, "String[]", "accessible"},
            {"public abstract static class Keys implements InputKeyGenerator<String[]> {}", "String[]", "concrete"},
            {"public interface Keys extends InputKeyGenerator<String[]> {}", "String[]", "concrete"},
            {"public final class Keys " + implementation, "String[]", "static"},
            {
                "public static final class Keys implements InputKeyGenerator<String[]> { private Keys() {} public"
                        + " String getKey(String[] value) { return \"k\"; } }",
                "String[]",
                "constructor"
            },
            {
                "public static final class Keys implements InputKeyGenerator<String[]> { public Keys(int x) {} public"
                        + " String getKey(String[] value) { return \"k\"; } }",
                "String[]",
                "constructor"
            },
            {
                "public static final class Keys implements InputKeyGenerator<String[]> { public Keys() throws"
                        + " java.io.IOException {} public String getKey(String[] value) { return \"k\"; } }",
                "String[]",
                "checked exceptions"
            },
            {"public static final class Keys {}", "String[]", "must implement"},
            {
                "public static class Keys<T> implements InputKeyGenerator<T> { public String getKey(T value) { return"
                        + " \"k\"; } }",
                "String[]",
                "type parameters"
            },
            {
                "public static final class Keys implements InputKeyGenerator<String[]> { public @Nullable String"
                        + " getKey(String[] value) { return null; } }",
                "String[]",
                "non-null String"
            }
        };
    }

    @Test(dataProvider = "badKeys")
    public void rejectsBadKeyProvider(final String declarations, final String type, final String expected)
            throws Exception {
        invalid(declarations, "Component(@Input(keyGenerator=Keys.class) " + type + " values) {}", expected);
    }

    @Test
    public void rejectsKeyOnMutableInput() throws Exception {
        invalid(
                "public static final class Keys implements InputKeyGenerator<String[]> { public String getKey(String[]"
                        + " value) { return \"k\"; } }",
                "@Input(keyGenerator=Keys.class) abstract String[] values();",
                "immutable constructor");
    }

    @Test
    public void compiledKeyProvider() throws Exception {
        final var contract = sourceFile("Keys", """
            package test;
            @org.jspecify.annotations.NullMarked
            public final class Keys implements react4j.InputKeyGenerator<String[]> {
                public String getKey(String[] values) { return String.valueOf(values.length); }
            }
            """);
        final var compiled =
                CompileTestUtil.assertCompilesWithoutWarnings(List.of(contract), getOptions(), List.of(), List.of());
        try {
            final var classpath = buildClasspath(compiled.classOutput().toFile());
            final var options = new ArrayList<>(getOptions());
            options.addAll(List.of(
                    "-classpath",
                    classpath.stream().map(File::toString).collect(Collectors.joining(File.pathSeparator))));
            final var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                    List.of(source("", "Component(@Input(keyGenerator=Keys.class) String[] values) {}")),
                    options,
                    processors(),
                    classpath);
            try {
                StrictGeneratedCode.check(compilation, options);
            } finally {
                cleanup(compilation);
            }
        } finally {
            cleanup(compiled);
        }
    }

    private void invalid(final String declarations, final String inputs, final String expected) throws Exception {
        final var compilation =
                CompileTestUtil.compile(List.of(source(declarations, inputs)), getOptions(), processors(), List.of());
        try {
            assertFalse(compilation.success());
            final var messages = CompileTestUtil.asMessage(compilation.diagnostics());
            assertTrue(messages.contains("@Input policy") && messages.contains(expected), messages);
        } finally {
            cleanup(compilation);
        }
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
