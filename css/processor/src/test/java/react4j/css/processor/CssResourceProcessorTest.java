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
            + "import react4j.css.CssImports;\nimport react4j.css.GlobalCss;\n"
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
        var globals = new ArrayList<Object[]>();
        for (var entry : cases) {
            globals.add(new Object[] {
                ((String) entry[0]).replace("@Keyframes", "@GlobalCss").replace(FRAMES, "body { margin: 0; }"), entry[1]
            });
        }
        cases.addAll(globals);
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
                "@CssImport(name=\"" + alias + "\", value=Pulse.class)", "color: red;", "Invalid imported CSS name"
            });
        }
        cases.add(new Object[] {
            "@CssImport(name=\"beat\", value=Pulse.class)\n@CssImport(name=\"beat\", value=Pulse.class)",
            "color: red;",
            "Duplicate imported CSS name"
        });
        for (var type : List.of("int", "String", "String[]")) {
            cases.add(new Object[] {
                "@CssImport(name=\"beat\", value=" + type + ".class)",
                "color: red;",
                "must reference a @Css, @GlobalCss or @Keyframes class"
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

    @Test
    public void componentImportsResolveThroughFullAndResourceFreeHeaderJars() throws Exception {
        var dependency = CssProcessorTest.compile(Map.of("Button.java", consumer("", "color: red;")));
        assertTrue(dependency.success(), dependency.diagnostics());
        Path directory = Files.createTempDirectory("css-component-header-");
        try {
            Path full = directory.resolve("full.jar");
            Path header = directory.resolve("header.jar");
            for (var jarPath : List.of(full, header)) {
                try (var jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
                    for (var entry : dependency.files().entrySet()) {
                        if (entry.getKey().startsWith("classes/")
                                && (full.equals(jarPath) || entry.getKey().endsWith(".class"))) {
                            jar.putNextEntry(new JarEntry(entry.getKey().substring(8)));
                            jar.write(Base64.getDecoder().decode(entry.getValue()));
                            jar.closeEntry();
                        }
                    }
                }
                String source = consumer("@CssImport(name=\"peer\", value=Button.class)", "color: blue;")
                        .replace("abstract class Button", "abstract class Consumer");
                var result = CssProcessorTest.compile(
                        Map.of("Consumer.java", source),
                        List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + jarPath));
                assertTrue(result.success(), result.diagnostics());
                assertEquals(result.diagnostics(), "");
                assertEquals(
                        JsonParser.parseString(text(result, "classes/demo/Consumer.css.json"))
                                .getAsJsonObject()
                                .getAsJsonArray("dependencies")
                                .size(),
                        0);
                var used = CssProcessorTest.compile(
                        Map.of("Consumer.java", source.replace("color: blue;", "& + :css(peer) { color: blue; }")),
                        List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + jarPath));
                assertTrue(used.success(), used.diagnostics());
                assertEquals(used.diagnostics(), "");
                assertTrue(text(used, "classes/demo/Consumer.css").contains(".jcss-demo-Consumer+.jcss-demo-Button"));
                assertEquals(
                        JsonParser.parseString(text(used, "classes/demo/Consumer.css.json"))
                                .getAsJsonObject()
                                .getAsJsonArray("dependencies")
                                .toString(),
                        "[\"demo.Button\"]");
            }
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    @Test
    public void namedImportsShareOneNamespaceAndAnimationReferencesCheckKinds() throws Exception {
        for (var entry : Map.of(
                        "@CssImport(name=\"peer\", value=Button.class)\n@CssImport(name=\"peer\", value=Pulse.class)",
                                "Duplicate imported CSS name",
                        "@CssImport(name=\"peer\", value=Button.class)", "must reference imported @Keyframes")
                .entrySet()) {
            var result = CssProcessorTest.compile(
                    Map.of("Pulse.java", frames(), "Button.java", consumer(entry.getKey(), "animation-name: peer;")));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains(entry.getValue()), result.diagnostics());
            assertFalse(result.files().containsKey("classes/demo/Button.css"));
        }
        for (var alias : List.of("none", "default", "INITIAL", "revert-layer")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Button.java", consumer("@CssImport(name=\"" + alias + "\", value=Button.class)", "color: red;")));
            assertTrue(result.success(), result.diagnostics());
        }
    }

    @Test
    public void checkedPeersMayReferenceEachOtherAndRequireGlobals() throws Exception {
        String button = consumer(
                "@CssImport(name=\"label\", value=Label.class)\n@CssImport(Defaults.class)",
                "&:hover + :css(label) { color: red; }");
        String label = consumer("@CssImport(name=\"button\", value=Button.class)", "& ~ :css(button) { color: blue; }")
                .replace("abstract class Button", "abstract class Label");
        var result = CssProcessorTest.compile(Map.of(
                "Button.java",
                button,
                "Label.java",
                label,
                "Defaults.java",
                global("Defaults", "", "body { margin: 0; }")));
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        for (var entry : Map.of("Button", "[\"demo.Defaults\",\"demo.Label\"]", "Label", "[\"demo.Button\"]")
                .entrySet()) {
            assertEquals(
                    JsonParser.parseString(text(result, "classes/demo/" + entry.getKey() + ".css.json"))
                            .getAsJsonObject()
                            .getAsJsonArray("dependencies")
                            .toString(),
                    entry.getValue());
        }
    }

    @DataProvider
    public Object[][] invalidPeerReferences() {
        return List.of(
                        "& > :css(missing)",
                        "& > :css(beat)",
                        "& > :css('peer')",
                        "& > :css(peer, peer)",
                        "& > :css(peer other)",
                        "& > :css()",
                        "& > :css",
                        "&:css(peer)",
                        ":css(peer)",
                        "& > :css(peer).local",
                        "&:is(:css(peer))",
                        "& > :css(peer) .raw",
                        "& > .raw",
                        "& > &")
                .stream()
                .map(selector -> new Object[] {selector + " { color: red; }"})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidPeerReferences")
    public void invalidPeerReferencesReportAuthoredLocationsWithoutOwnerOutputs(String css) throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Pulse.java",
                frames(),
                "Button.java",
                consumer(
                        "@CssImport(name=\"peer\", value=Button.class)\n@CssImport(name=\"beat\", value=Pulse.class)",
                        css)));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Button.java:"), result.diagnostics());
        assertFalse(result.files().containsKey("classes/demo/Button.css"));
        assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
    }

    @Test
    public void authoredTypesCannotBeOverwrittenByGeneratedBindings() throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Button.java",
                consumer("", "color: red;"),
                "Styled_Button.java",
                "package demo; final class Styled_Button {}"));
        assertFalse(result.success());
        assertTrue(
                result.diagnostics().contains("Attempt to recreate a file for type demo.Styled_Button"),
                result.diagnostics());
        assertTrue(result.files().isEmpty(), result.files().keySet().toString());
    }

    private static String global(String name, String annotations, String css) {
        return IMPORTS + "@NullMarked\n" + annotations + "\n@GlobalCss(\"\"\"\n" + css
                + "\n\"\"\")\npublic final class " + name + " {}";
    }

    @Test
    public void globalDeclarationsAndTypedImportsProduceOnlyResources() throws Exception {
        String defaults = global("Defaults", "", ":root { --accent: blue; } body { margin: 0; }");
        String theme = global(
                "Theme",
                "@CssImport(Defaults.class)\n@CssImport(name=\"pulse\", value=Pulse.class)",
                "button:hover { animation: pulse 1s linear infinite; }");
        var result = CssProcessorTest.compile(Map.of(
                "Pulse.java",
                frames(),
                "Defaults.java",
                defaults,
                "Theme.java",
                theme,
                "Button.java",
                consumer("@CssImport(Theme.class)\n@CssImport(Defaults.class)", "color: blue;")));
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        assertEquals(
                JsonParser.parseString(text(result, "classes/demo/Theme.css.json"))
                        .getAsJsonObject()
                        .getAsJsonArray("dependencies")
                        .toString(),
                "[\"demo.Defaults\",\"demo.Pulse\"]");
        assertEquals(
                JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                        .getAsJsonObject()
                        .getAsJsonArray("dependencies")
                        .toString(),
                "[\"demo.Theme\",\"demo.Defaults\"]");
        assertTrue(text(result, "classes/demo/Theme.css").contains("animation:jcss-demo-Pulse--keyframes"));
        assertTrue(text(result, "classes/demo/Defaults.css").startsWith(":root{--accent:blue;}body{margin:0;}"));
        assertFalse(result.files().keySet().stream().anyMatch(p -> p.startsWith("generated/demo/Styled_Theme")));
        assertFalse(result.files().keySet().stream().anyMatch(p -> p.startsWith("generated/demo/Styled_Defaults")));
    }

    @DataProvider
    public Object[][] invalidGlobalImports() {
        var cases = new ArrayList<Object[]>();
        for (var type : List.of("int", "void", "int[]", "String[]", "String")) {
            cases.add(new Object[] {
                consumer("@CssImport(" + type + ".class)", "color: red;"),
                "@CssImport must reference a @Css, @GlobalCss or @Keyframes class"
            });
        }
        for (var type : List.of("Button", "Pulse")) {
            cases.add(
                    new Object[] {consumer("@CssImport(" + type + ".class)", "color: red;"), "Invalid imported CSS name"
                    });
        }
        cases.add(new Object[] {
            consumer("@CssImport(name=\"defaults\", value=Defaults.class)", "color: red;"), "must not declare a name"
        });
        cases.add(new Object[] {
            consumer("@CssImport(Defaults.class)\n@CssImport(Defaults.class)", "color: red;"),
            "Duplicate imported global stylesheet"
        });
        cases.add(new Object[] {
            global("Button", "@CssImport(Button.class)", "body { margin: 0; }"), "must not import themselves"
        });
        cases.add(new Object[] {
            frames().replace("class Pulse", "class Button")
                    .replace("@NullMarked", "@NullMarked\n@CssImport(Defaults.class)"),
            "only supported on stylesheet definitions"
        });
        cases.add(new Object[] {IMPORTS + "@CssImport(Defaults.class) class Button {}", "Declare exactly one"});
        cases.add(new Object[] {
            global("Button", "@Css(\"\"\"\ncolor:red;\n\"\"\")", "body { margin: 0; }"), "Declare exactly one"
        });
        cases.add(new Object[] {
            global("Button", "@Keyframes(\"\"\"\nfrom {opacity:0;}\n\"\"\")", "body { margin: 0; }"),
            "Declare exactly one"
        });
        for (var expression : List.of("\"body {}\"", "Constants.CSS", "\"body\" + \" {}\"")) {
            cases.add(new Object[] {
                IMPORTS + "@GlobalCss(" + expression
                        + ") class Button {} class Constants { static final String CSS=\"body {}\"; }",
                "text block"
            });
        }
        for (var css : List.of(
                "body { color: red !important; }",
                "@keyframes pulse { to { opacity:0; } }",
                "@import 'external.css';",
                "@font-face { font-family: font; src: url(font.woff); }",
                "@layer x { body {} }",
                "@container (width > 1px) { body {} }",
                "body { animation-name: unknown; }",
                "body { animation: var(--animation); }",
                "body { @media screen { color: red; } }",
                "body { @supports (display: grid) { color: red; } }",
                "body { color red; }",
                "color: red;")) {
            cases.add(new Object[] {global("Button", "", css), "ERROR"});
        }
        return cases.toArray(Object[][]::new);
    }

    @Test
    public void nestedGlobalConditionalsAcceptExplicitSelectors() throws Exception {
        for (var condition : List.of("@media screen", "@supports (display: grid)")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Defaults.java", global("Defaults", "", "body { " + condition + " { & { color: red; } } }")));
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertTrue(text(result, "classes/demo/Defaults.css").contains("&{color:red;}"));
        }
    }

    @Test(dataProvider = "invalidGlobalImports")
    public void globalAndImportFailuresDoNotEmitTheInvalidOwnersResources(String source, String diagnostic)
            throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Button.java", source, "Defaults.java", global("Defaults", "", "body {}"), "Pulse.java", frames()));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains(diagnostic), result.diagnostics());
        assertFalse(result.files().containsKey("classes/demo/Button.css"));
    }

    @Test
    public void globalImportsResolveThroughAResourceFreeHeaderJar() throws Exception {
        var dependency =
                CssProcessorTest.compile(Map.of("Defaults.java", global("Defaults", "", "body { margin: 0; }")));
        assertTrue(dependency.success(), dependency.diagnostics());
        Path directory = Files.createTempDirectory("css-global-header-");
        try {
            Path header = directory.resolve("header.jar");
            try (var jar = new JarOutputStream(Files.newOutputStream(header))) {
                jar.putNextEntry(new JarEntry("demo/Defaults.class"));
                jar.write(Base64.getDecoder().decode(dependency.files().get("classes/demo/Defaults.class")));
                jar.closeEntry();
            }
            var result = CssProcessorTest.compile(
                    Map.of("Button.java", consumer("@CssImport(Defaults.class)", "color: red;")),
                    List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + header));
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertEquals(
                    JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                            .getAsJsonObject()
                            .getAsJsonArray("dependencies")
                            .toString(),
                    "[\"demo.Defaults\"]");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
