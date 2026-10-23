package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class UtilityCssProcessorTest {
    private static final String IMPORTS = "package demo;\nimport react4j.css.*;\n";
    private static final String LIBRARY = IMPORTS + """
        @UtilityCssLibrary(value=\
        """ + "\"\"\"\n" + """
        .flex { display:flex; }
        .p-2 { padding:.5rem; }
        .hover\\\\:red:hover { color:red; }
        .group .child { opacity:.7; }
        .unused { margin:17px; }
        """ + "\"\"\")\nclass Utilities {}\n";

    private static String styles(String annotations, String methods) {
        return IMPORTS + "@UtilityStyles(Utilities.class)\n" + annotations + "\nabstract class Button {\n"
                + "static Button of() { return Styled_Button.get(); }\n" + methods + "\n}";
    }

    private static String text(CssProcessorTest.Result result, String path) {
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void listsKeepAuthoredOrderAndLocalMarkersWithNoImplicitBaseUtilities() throws Exception {
        var result = CssProcessorTest.compile(
                Map.of(
                        "Utilities.java",
                        LIBRARY,
                        "Button.java",
                        styles(
                                "",
                                "@UtilityCss(\"p-2 flex hover:red\") abstract String style();"
                                        + "@UtilityCss(\"group\") abstract String grouped();")),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        assertEquals(
                result.values().get("demo.Button.style"),
                "jcss-demo-Utilities--class-p_002d_2 jcss-demo-Utilities--class-flex"
                        + " jcss-demo-Utilities--class-hover_003a_red jcss-demo-Button");
        assertEquals(
                result.values().get("demo.Button.grouped"),
                "jcss-demo-Utilities--class-group jcss-demo-Button jcss-demo-Button--grouped");
        var css = text(result, "classes/demo/Utilities.css");
        assertTrue(css.contains(".jcss-demo-Utilities--class-hover_003a_red:hover"), css);
        var metadata = JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                .getAsJsonObject();
        assertEquals(metadata.getAsJsonArray("dependencies").toString(), "[\"demo.Utilities\"]");
        assertEquals(
                metadata.getAsJsonArray("bindings").asList().stream()
                        .map(item -> item.getAsJsonObject())
                        .filter(item -> "style".equals(item.get("method").getAsString()))
                        .findFirst()
                        .orElseThrow()
                        .getAsJsonArray("symbols")
                        .size(),
                4);
        assertEquals(
                metadata.getAsJsonArray("bindings")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonArray("requires")
                        .toString(),
                "[\"demo.Utilities\"]");
    }

    @Test
    public void hybridBindingsKeepLocalChecksAndEffectiveInterfaceMetadata() throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                LIBRARY,
                "Button.java",
                styles(
                                        "@Css(\"\"\"\n"
                                                + "color:blue; &.local { opacity:.2; } &.active { border-width:3px; }\n"
                                                + "\"\"\")",
                                        "abstract String style(); abstract String local(); @UtilityCss(\"p-2\")"
                                                + " abstract String active();@Override public abstract String"
                                                + " inherited();")
                                .replace("class Button {", "class Button implements Contract {")
                        + "interface Contract { @UtilityCss(\"flex\") String inherited(); }"));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("inherited() has no matching local selector"), result.diagnostics());
        var inherited = CssProcessorTest.compile(
                Map.of(
                        "Utilities.java",
                        LIBRARY,
                        "Button.java",
                        styles(
                                                "@Css(\"\"\"\n"
                                                        + "color:blue; &.local { opacity:.2; } &.active {"
                                                        + " border-width:3px; }\n"
                                                        + "\"\"\")",
                                                "abstract String style(); abstract String local();"
                                                        + " @UtilityCss(\"p-2\") abstract String active();")
                                        .replace("class Button {", "class Button implements Contract {")
                                + "interface Contract { @UtilityCss(\"flex\") String inherited(); }"),
                "demo.Button");
        assertTrue(inherited.success(), inherited.diagnostics());
        assertEquals(
                inherited.values().get("demo.Button.inherited"),
                "jcss-demo-Utilities--class-flex jcss-demo-Button jcss-demo-Button--inherited");
        assertEquals(
                inherited.values().get("demo.Button.active"),
                "jcss-demo-Utilities--class-p_002d_2 jcss-demo-Button jcss-demo-Button--active");
        assertTrue(text(inherited, "classes/demo/Button.css").contains("--active{border-width:3px;"));
    }

    @Test
    public void invalidUnusedDeclarationsAreCheckedDuringJavaCompilation() throws Exception {
        var cases = new LinkedHashMap<String, String>();
        cases.put("@UtilityCss(\"\") abstract String style();", "nonempty class list");
        cases.put("@UtilityCss(\" \\t \\n\") abstract String style();", "nonempty class list");
        cases.put("@UtilityCss(\"missing\") abstract String style();", "Unknown managed utility class: missing");
        cases.put("@UtilityCss(\"flex flex\") abstract String style();", "classes must be distinct");
        cases.put(
                "abstract String style(); @UtilityCss(\"flex\") String helper() { return \"x\"; }", "must be abstract");
        cases.put("abstract String style(); @UtilityCss(\"flex\") abstract int wrong();", "return String");
        cases.put("@UtilityCss(\"flex\") @CssId abstract String style();", "only one symbol annotation");
        for (var entry : cases.entrySet()) {
            var result = CssProcessorTest.compile(
                    Map.of("Utilities.java", LIBRARY, "Button.java", styles("", entry.getKey())));
            assertFalse(result.success(), entry.getKey());
            assertTrue(result.diagnostics().contains(entry.getValue()), result.diagnostics());
        }
        var orphan = CssProcessorTest.compile(Map.of(
                "Button.java",
                IMPORTS
                        + "@Css(\"\"\"\n"
                        + "color:red;\n"
                        + "\"\"\") abstract class Button { @UtilityCss(\"flex\") abstract String style(); }"));
        assertFalse(orphan.success());
        assertTrue(orphan.diagnostics().contains("require @UtilityStyles"), orphan.diagnostics());
    }

    @Test
    public void externalsRemainLiteralAndCannotEnterManagedLists() throws Exception {
        String library = LIBRARY.replace("value=", "externalClasses={\"group\"}, value=");
        var result = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                library,
                "Button.java",
                styles("", "@UtilityCss(\"child\") abstract String style();")));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(text(result, "classes/demo/Utilities.css").contains(".group .jcss-demo-Utilities--class-child"));
        var external = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                library,
                "Button.java",
                styles("", "@UtilityCss(\"group\") abstract String style();")));
        assertFalse(external.success());
        assertTrue(external.diagnostics().contains("Unknown managed utility class: group"), external.diagnostics());
        for (var names : List.of("\"missing\"", "\"group\",\"group\"")) {
            var invalid = CssProcessorTest.compile(
                    Map.of("Utilities.java", LIBRARY.replace("value=", "externalClasses={" + names + "}, value=")));
            assertFalse(invalid.success());
            assertTrue(invalid.diagnostics().contains("external class"), invalid.diagnostics());
        }
    }

    @Test
    public void stableLocalMarkersAndPeerRootsKeepTheirExistingMeaning() throws Exception {
        var result = CssProcessorTest.compile(
                Map.of(
                        "Utilities.java",
                        LIBRARY,
                        "Button.java",
                        styles(
                                "@CssStable @Css(\"\"\"\n--accent:gold;color:var(--accent);\n\"\"\")",
                                "@UtilityCss(\"flex\") abstract String style(); @CssCustomProperty abstract String"
                                        + " accent();"),
                        "Peer.java",
                        IMPORTS
                                + "@CssImport(name=\"target\",value=Button.class) @Css(\"\"\"\n"
                                + "&:hover > :css(target) { color:navy; }\n"
                                + "\"\"\") abstract class Peer { abstract String style(); }"),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.accent"), "--jcss-demo-Button--accent");
        assertTrue(text(result, "classes/demo/Peer.css").contains(".jcss-demo-Peer:hover>.jcss-demo-Button"));
        var provider = JsonParser.parseString(text(result, "classes/demo/Utilities.css.json"))
                .getAsJsonObject();
        assertTrue(provider.getAsJsonArray("symbols").asList().stream()
                .noneMatch(item -> item.getAsJsonObject().get("stable").getAsBoolean()));
        var local = JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                .getAsJsonObject();
        assertTrue(local.getAsJsonArray("symbols").asList().stream()
                .allMatch(item -> item.getAsJsonObject().get("stable").getAsBoolean()));
    }

    @Test
    public void classRetainedContractsValidateConsumersWithoutProviderSources() throws Exception {
        var dependency = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                LIBRARY.replace("class Utilities", "public class Utilities"),
                "Contract.java",
                IMPORTS + "public interface Contract { @UtilityCss(\"p-2 flex\") String style(); }"));
        assertTrue(dependency.success(), dependency.diagnostics());
        var directory = Files.createTempDirectory("utility-contract-");
        try {
            for (var entry : dependency.files().entrySet()) {
                if (entry.getKey().startsWith("classes/")) {
                    var path = directory.resolve(entry.getKey().substring("classes/".length()));
                    Files.createDirectories(path.getParent());
                    Files.write(path, Base64.getDecoder().decode(entry.getValue()));
                }
            }
            String classpath = directory + File.pathSeparator + CssProcessorTest.applicationClasspath();
            String source = styles("", "").replace("class Button {", "class Button implements Contract {");
            var result = CssProcessorTest.compile(
                    Map.of("Button.java", source), List.of("-classpath", classpath), "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(
                    result.values().get("demo.Button.style"),
                    "jcss-demo-Utilities--class-p_002d_2 jcss-demo-Utilities--class-flex jcss-demo-Button");
            var invalid = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source.replace(
                                    "class Button implements Contract {",
                                    "class Button implements Contract { @UtilityCss(\"missing\") public abstract"
                                            + " String style();")),
                    List.of("-classpath", classpath));
            assertFalse(invalid.success());
            assertTrue(invalid.diagnostics().contains("Unknown managed utility class: missing"), invalid.diagnostics());
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    private static Path inputs(Path directory, String logical, String css) throws Exception {
        var source = directory.resolve("source.css");
        var jar = directory.resolve("inputs.jar");
        Files.writeString(source, css);
        String classpath = Boolean.getBoolean("css.processor.implementation")
                ? System.getProperty("java.class.path")
                : CssProcessorTest.processorJar().toString();
        var process = new ProcessBuilder(
                        Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                        "-classpath",
                        classpath,
                        "react4j.css.processor.CssInputJar",
                        jar.toString(),
                        logical,
                        source.toString())
                .redirectErrorStream(true)
                .start();
        var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(process.waitFor(), 0, output);
        return jar;
    }

    @Test
    public void explicitFilesSupplyUtilityCataloguesAndGlobalOrigins() throws Exception {
        var directory = Files.createTempDirectory("css-file-input-");
        String css = "/* 😀 */\r.flex { display:flex; }\r.p-2 { padding:8px; }\r";
        try {
            var jar = inputs(directory, "demo/nested-dir/utilities.css", css);
            var options = List.of("-classpath", jar + File.pathSeparator + CssProcessorTest.applicationClasspath());
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Utilities.java",
                            IMPORTS + "@UtilityCssLibrary(file=\"nested-dir/utilities.css\") class Utilities {}",
                            "Defaults.java",
                            IMPORTS + "@GlobalCss(file=\"nested-dir/utilities.css\") class Defaults {}",
                            "Button.java",
                            styles("", "@UtilityCss(\"flex p-2\") abstract String style();")),
                    options,
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(
                    result.values().get("demo.Button.style"),
                    "jcss-demo-Utilities--class-flex jcss-demo-Utilities--class-p_002d_2 jcss-demo-Button");
            for (var owner : List.of("Utilities", "Defaults")) {
                var map = JsonParser.parseString(text(result, "classes/demo/" + owner + ".css.map"))
                        .getAsJsonObject();
                assertEquals(map.getAsJsonArray("sources").get(0).getAsString(), "demo/nested-dir/utilities.css");
                assertEquals(map.getAsJsonArray("sourcesContent").get(0).getAsString(), css);
            }
            for (var kind : List.of("UtilityCssLibrary", "GlobalCss")) {
                for (var form : List.of(
                        "",
                        "(file=\"\")",
                        "(file=\"../unsafe.css\")",
                        "(file=\"/absolute.css\")",
                        "(file=\"nested-dir//utilities.css\")",
                        "(file=\"missing.css\")",
                        "(value=\"\"\"\n.flex{}\n\"\"\",file=\"nested-dir/utilities.css\")")) {
                    var invalid = CssProcessorTest.compile(
                            Map.of("Utilities.java", IMPORTS + "@" + kind + form + " class Utilities {}"), options);
                    assertFalse(invalid.success(), kind + form);
                    assertTrue(
                            invalid.diagnostics().contains("exactly one")
                                    || invalid.diagnostics().contains("path")
                                    || invalid.diagnostics().contains("Missing declared CSS input"),
                            invalid.diagnostics());
                }
            }
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void providerKindsAndLiteralSourcesAreChecked() throws Exception {
        var wrong = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                IMPORTS + "@GlobalCss(\"\"\"\nbody{margin:0;}\n\"\"\") class Utilities {}",
                "Button.java",
                styles("", "@UtilityCss(\"flex\") abstract String style();")));
        assertFalse(wrong.success());
        assertTrue(wrong.diagnostics().contains("must reference a @UtilityCssLibrary"), wrong.diagnostics());
        for (var library : List.of(
                LIBRARY.replace("class Utilities", "interface Utilities"),
                LIBRARY.replace("class Utilities {}", "class Utilities { int state; }"),
                LIBRARY.replace("class Utilities {}", "@GlobalCss(\"\"\"\nbody{margin:0;}\n\"\"\") class Utilities {}"),
                LIBRARY.replace("display:flex;", "display:flex!important;"))) {
            var invalid = CssProcessorTest.compile(Map.of("Utilities.java", library));
            assertFalse(invalid.success(), library);
        }
    }
}
