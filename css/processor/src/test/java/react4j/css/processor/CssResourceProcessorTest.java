package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class CssResourceProcessorTest {
    private static final String IMPORTS = "package demo;\n"
            + "import react4j.css.Css;\n"
            + "import react4j.css.Keyframes;\n"
            + "import react4j.css.CssImport;\n"
            + "import react4j.css.CssImports;\n"
            + "import org.jspecify.annotations.NullMarked;\n";
    private static final String FRAMES = "from { opacity: 0.4; } to { opacity: 1; }";

    private static String frames(String declaration, String members, String css) {
        return IMPORTS + "@NullMarked\n@Keyframes(\"\"\"\n" + css + "\n\"\"\")\n" + declaration + " {" + members + "}";
    }

    private static String frames() {
        return frames("public abstract class Pulse", "", FRAMES);
    }

    private static String consumer(String annotations, String css) {
        return IMPORTS + "@NullMarked\n" + annotations + "\n@Css(\"\"\"\n" + css
                + "\n\"\"\")\nabstract class Button { abstract String style(); }";
    }

    private static String text(CssProcessorTest.Result result, String path) {
        assertTrue(result.files().containsKey(path), result.files().keySet().toString());
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void aliasesResolveParsedNamesAndOnlyUsedImportsBecomeDependencies() throws Exception {
        for (var css : List.of(
                "animation-name: beat;",
                "animation: beat 1s linear infinite;",
                "animation: 1s linear beat, beat 2s ease;",
                "@media screen { &:hover { animation-name: beat; } }")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Pulse.java",
                    frames(),
                    "Button.java",
                    consumer(
                            "@CssImport(name=\"beat\", value=Pulse.class)\n"
                                    + "@CssImport(name=\"unused\", value=Pulse.class)",
                            css)));
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertTrue(text(result, "classes/demo/Button.css").contains("jcss-demo-Pulse--keyframes"));
            var metadata = JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                    .getAsJsonObject();
            assertEquals(metadata.getAsJsonArray("dependencies").toString(), "[\"demo.Pulse\"]");
            assertEquals(
                    text(result, "classes/demo/Pulse.css"),
                    "@keyframes jcss-demo-Pulse--keyframes{from{opacity:0.4;}to{opacity:1;}}\n"
                            + "/*# sourceMappingURL=Pulse.css.map */\n");
            assertFalse(result.files().keySet().stream().anyMatch(p -> p.startsWith("generated/demo/Styled_Pulse")));
        }
        var unused = CssProcessorTest.compile(Map.of(
                "Pulse.java",
                frames(),
                "Button.java",
                consumer("@CssImport(name=\"beat\", value=Pulse.class)", "content: 'beat'; color: red;")));
        assertTrue(unused.success(), unused.diagnostics());
        assertEquals(
                JsonParser.parseString(text(unused, "classes/demo/Button.css.json"))
                        .getAsJsonObject()
                        .getAsJsonArray("dependencies")
                        .size(),
                0);
    }

    @Test
    public void publicPackageAndNestedResourceDefinitionsNeedNoGeneratedJava() throws Exception {
        for (var declaration : List.of(
                "public class Pulse",
                "public final class Pulse",
                "public abstract class Pulse",
                "class Pulse",
                "final class Pulse",
                "abstract class Pulse")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Pulse.java",
                    frames(
                            declaration,
                            "static String helper() { return \"value\"; } String other() { return helper(); }",
                            FRAMES)));
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertFalse(result.files().keySet().stream().anyMatch(p -> p.startsWith("generated/")));
            assertTrue(result.files().containsKey("classes/demo/Pulse.css.json"));
        }
        var nested =
                frames("abstract static class Pulse", "", FRAMES).replace("@NullMarked", "class Outer { @NullMarked")
                        + "}";
        var result = CssProcessorTest.compile(Map.of("Outer.java", nested));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(text(result, "classes/demo/Outer$Pulse.css").contains("jcss-demo-Outer_0024_Pulse--keyframes"));
    }

    @DataProvider
    public Object[][] invalidResourceOwners() {
        var cases = new ArrayList<Object[]>();
        for (var declaration : List.of(
                "interface Pulse",
                "@interface Pulse",
                "enum Pulse",
                "record Pulse()",
                "class Pulse<T>",
                "abstract class Pulse implements Runnable",
                "class Pulse extends Parent")) {
            String source = frames(declaration, "", FRAMES);
            if (declaration.startsWith("enum")) {
                source = source.replace("enum Pulse {", "enum Pulse {;");
            }
            if (declaration.contains("Parent")) {
                source += "class Parent {}";
            }
            cases.add(new Object[] {source, "CSS resource definitions require"});
        }
        for (var member : List.of(
                "String field;",
                "static String field;",
                "static final int VALUE=1;",
                "Pulse() {}",
                "private Pulse() {}",
                "public Pulse(int value) {}",
                "abstract String name();")) {
            cases.add(new Object[] {
                frames("abstract class Pulse", member, FRAMES),
                "must not declare fields, constructors or abstract methods"
            });
        }
        for (var member : List.of("{}", "static {}")) {
            cases.add(new Object[] {frames("class Pulse", member, FRAMES), "must not declare initializers"});
        }
        String nested = frames("class Pulse", "", FRAMES).replace("@NullMarked", "class Outer { @NullMarked") + "}";
        cases.add(new Object[] {nested, "nested classes must be static"});
        cases.add(new Object[] {
            nested.replace("class Pulse", "private static class Pulse"), "CSS resource definitions require"
        });
        cases.add(new Object[] {
            nested.replace("class Outer {", "class Outer { private static class Hidden {")
                            .replace("class Pulse", "static class Pulse")
                    + "}",
            "accessible from their package"
        });
        cases.add(new Object[] {
            frames("sealed class Pulse permits Child", "", FRAMES) + "final class Child extends Pulse {}",
            "CSS resource definitions require"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidResourceOwners")
    public void invalidResourceOwnersProduceDiagnosticsAndNoResources(String source, String message) throws Exception {
        var result = CssProcessorTest.compile(Map.of("Pulse.java", source));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains(message), result.diagnostics());
        assertTrue(result.files().isEmpty(), result.files().keySet().toString());
    }

    @DataProvider
    public Object[][] invalidImports() {
        var cases = new ArrayList<Object[]>();
        for (var alias : List.of(
                "",
                "1pulse",
                "pulse other",
                "pulse;",
                "pulse\\\\name",
                "'pulse'",
                "none",
                "default",
                "INITIAL",
                "revert-layer")) {
            cases.add(new Object[] {
                "@CssImport(name=\"" + alias + "\", value=Pulse.class)",
                "color: red;",
                "Invalid imported animation name"
            });
        }
        cases.add(new Object[] {
            "@CssImport(name=\"beat\", value=Pulse.class)\n@CssImport(name=\"beat\", value=Pulse.class)",
            "color: red;",
            "Duplicate imported animation name"
        });
        for (var type : List.of("int", "String", "String[]", "Button")) {
            cases.add(new Object[] {
                "@CssImport(name=\"beat\", value=" + type + ".class)",
                "color: red;",
                "must reference a @Keyframes class"
            });
        }
        cases.add(new Object[] {
            "@CssImport(name=\"beat\", value=Pulse.class)",
            "@keyframes beat { from { opacity: 0; } }",
            "conflicts with imported animation"
        });
        cases.add(new Object[] {
            "@CssImport(name=\"beat\", value=Pulse.class)", "animation-name: missing;", "Unknown local animation name"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidImports")
    public void invalidImportsAreCheckedEvenWhenUnused(String annotation, String css, String message) throws Exception {
        var result = CssProcessorTest.compile(Map.of("Pulse.java", frames(), "Button.java", consumer(annotation, css)));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains(message), result.diagnostics());
        assertFalse(result.files().containsKey("classes/demo/Button.css"));
    }

    @Test
    public void annotationPlacementAndLiteralContractsAreChecked() throws Exception {
        for (var extra : List.of(
                "@CssImport(name=\"beat\", value=Pulse.class)",
                "@CssImports({})",
                "@Css(\"\"\"\ncolor: red;\n\"\"\")")) {
            var result = CssProcessorTest.compile(
                    Map.of("Pulse.java", frames().replace("@NullMarked", "@NullMarked\n" + extra)));
            assertFalse(result.success());
            assertTrue(result.files().isEmpty());
        }
        var orphan = CssProcessorTest.compile(
                Map.of("Pulse.java", IMPORTS + "@CssImport(name=\"beat\", value=String.class) class Pulse {}"));
        assertFalse(orphan.success());
        assertTrue(orphan.diagnostics().contains("Declare exactly one"), orphan.diagnostics());
        for (var expression : List.of("\"from {opacity:0;}\"", "BODY", "\"from\" + \"{opacity:0;}\"")) {
            String source = IMPORTS + "@Keyframes(" + expression + ") class Pulse { static final String BODY=\"\"; }";
            // A constant on a separate owner preserves the literal failure instead of violating the field contract.
            source = source.replace("class Pulse { static final String BODY=\"\"; }", "class Pulse {}")
                            .replace("@Keyframes(BODY)", "@Keyframes(Constants.BODY)")
                    + "class Constants { static final String BODY=\"from {opacity:0;}\"; }";
            var result = CssProcessorTest.compile(Map.of("Pulse.java", source));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains("text block"), result.diagnostics());
            assertFalse(result.files().containsKey("classes/demo/Pulse.css"));
        }
    }

    @Test
    public void importsResolveRetainedDeclarationsFromFullAndHeaderJars() throws Exception {
        var dependency = CssProcessorTest.compile(Map.of("Pulse.java", frames()));
        assertTrue(dependency.success(), dependency.diagnostics());
        Path directory = Files.createTempDirectory("css-dependency-");
        try {
            for (var entry : dependency.files().entrySet()) {
                if (entry.getKey().startsWith("classes/")) {
                    Path file = directory.resolve(entry.getKey().substring(8));
                    Files.createDirectories(file.getParent());
                    Files.write(file, Base64.getDecoder().decode(entry.getValue()));
                }
            }
            var options =
                    List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + directory);
            var sources = Map.of(
                    "Button.java", consumer("@CssImport(name=\"beat\", value=Pulse.class)", "animation-name: beat;"));
            var result = CssProcessorTest.compile(sources, options);
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertTrue(text(result, "classes/demo/Button.css").contains("jcss-demo-Pulse--keyframes"));
            Path header = directory.resolve("header.jar");
            try (var jar = new JarOutputStream(Files.newOutputStream(header))) {
                var path = directory.resolve("demo/Pulse.class");
                jar.putNextEntry(new JarEntry("demo/Pulse.class"));
                Files.copy(path, jar);
                jar.closeEntry();
            }
            var headerOptions =
                    List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + header);
            result = CssProcessorTest.compile(sources, headerOptions);
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertTrue(text(result, "classes/demo/Button.css").contains("jcss-demo-Pulse--keyframes"));
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
