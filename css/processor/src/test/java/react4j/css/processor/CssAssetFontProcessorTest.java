package react4j.css.processor;

import static org.testng.Assert.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssAssetFontProcessorTest {
    private static final String IMPORTS = "package demo;\nimport react4j.css.Asset;\nimport react4j.css.Assets;\n"
            + "import react4j.css.Css;\nimport react4j.css.GlobalCss;\nimport react4j.css.Keyframes;\n"
            + "import react4j.css.CssImport;\nimport react4j.css.CssStable;\n";
    private static final String MEDIA = IMPORTS + "@Assets public abstract class Media {@Asset(\"icon.svg\") public"
            + " abstract String icon(); @Asset(\"heading.woff2\") public abstract"
            + " String font();}";
    private static final String FONTS = IMPORTS + "@CssStable"
            + " @CssImport(value=Media.class,name=\"media\")@GlobalCss(\"\"\"\n"
            + "@font-face{font-family:'My"
            + " Heading';src:url(asset:media.font);font-display:swap;}\n"
            + "\"\"\")public final class Fonts {}";

    private static String consumer(String kind, String annotation, String css) {
        return IMPORTS + annotation + "\n@" + kind + "(\"\"\"\n" + css + "\n\"\"\")\nabstract class Button {"
                + ("Css".equals(kind) ? "abstract String style();" : "") + "}";
    }

    private static String text(CssProcessorTest.Result result, String file) {
        return new String(
                Base64.getDecoder().decode(Objects.requireNonNull(result.files().get(file))), StandardCharsets.UTF_8);
    }

    @Test
    public void unicodeCaseFoldingDataIsIncludedInBothProcessorDistributions() throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Media.java",
                MEDIA,
                "Fonts.java",
                FONTS.replace("My Heading", "Straße"),
                "Button.java",
                consumer(
                        "Css",
                        "@CssImport(value=Fonts.class,font=\"STRASSE\",name=\"heading\")",
                        "font-family:heading;")));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(text(result, "classes/demo/Button.css").contains("jcss-demo-Fonts--font-strasse"));
    }

    @Test
    public void sourceAndSeparatelyCompiledAssetAndFontImportsResolveInEveryDefinitionKind() throws Exception {
        var dependencies = CssProcessorTest.compile(Map.of("Media.java", MEDIA, "Fonts.java", FONTS));
        assertTrue(dependencies.success(), dependencies.diagnostics());
        var directory = Files.createTempDirectory("css-asset-font-library");
        try {
            for (var entry : dependencies.files().entrySet()) {
                if (entry.getKey().startsWith("classes/") && entry.getKey().endsWith(".class")) {
                    var path = directory.resolve(entry.getKey().substring(8));
                    Files.createDirectories(Objects.requireNonNull(path.getParent()));
                    Files.write(path, Base64.getDecoder().decode(entry.getValue()));
                }
            }
            for (boolean binary : List.of(false, true)) {
                for (boolean formatted : List.of(false, true)) {
                    for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
                        String declarations =
                                "background:url(asset:media.icon?mode=2#shape);font:italic 700 2rem heading;";
                        String css = "Css".equals(kind)
                                ? declarations
                                : ("Keyframes".equals(kind) ? "to" : "body") + "{" + declarations + "}";
                        String source = consumer(
                                kind,
                                "@CssImport(value=Media.class,name=\"media\")"
                                        + "@CssImport(value=Fonts.class,font=\"MY HEADING\",name=\"heading\")",
                                css);
                        var result = CssProcessorTest.compile(
                                binary
                                        ? Map.of("Button.java", source)
                                        : Map.of("Button.java", source, "Media.java", MEDIA, "Fonts.java", FONTS),
                                binary
                                        ? List.of(
                                                "-Areact4j.css.format_generated_source=" + formatted,
                                                "-classpath",
                                                CssProcessorTest.applicationClasspath()
                                                        + File.pathSeparator
                                                        + directory)
                                        : List.of("-Areact4j.css.format_generated_source=" + formatted));
                        assertTrue(result.success(), result.diagnostics());
                        String output = text(result, "classes/demo/Button.css");
                        assertTrue(output.contains("asset:demo/icon.svg?mode=2#shape"), output);
                        assertTrue(output.contains("jcss-demo-Fonts--font-my_0020_heading"), output);
                        assertTrue(text(result, "classes/demo/Button.css.json").contains("\"kind\":\"FONT\""));
                    }
                }
            }
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void transitiveSelectedFamiliesKeepTheirOriginalOwner() throws Exception {
        String alias = IMPORTS + "@CssImport(value=Fonts.class,font=\"My Heading\",name=\"alias\")"
                + "@GlobalCss(\"\"\"\nbody{font-family:alias;}\n\"\"\") final class Alias {}";
        var result = CssProcessorTest.compile(Map.of(
                "Media.java",
                MEDIA,
                "Fonts.java",
                FONTS,
                "Alias.java",
                alias,
                "Button.java",
                consumer("Css", "@CssImport(value=Alias.class,font=\"alias\",name=\"local\")", "font-family:local;")));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(text(result, "classes/demo/Button.css").contains("jcss-demo-Fonts--font-my_0020_heading"));
    }

    @Test
    public void assetSelectionAliasAndReferenceMistakesFailBeforeGeneration() throws Exception {
        var cases = Map.of(
                "@CssImport(value=Media.class)", "Invalid imported CSS name",
                "@CssImport(value=Media.class,name=\"media\",id=\"x\")", "without id, property or font",
                "@CssImport(value=Media.class,name=\"media\",property=\"x\")", "without id, property or font",
                "@CssImport(value=Media.class,name=\"media\",font=\"x\")", "without id, property or font",
                "@CssImport(value=Media.class,name=\"media\") @CssImport(value=Media.class,name=\"media\")",
                        "Duplicate imported asset");
        for (var entry : cases.entrySet()) {
            var result = CssProcessorTest.compile(
                    Map.of("Media.java", MEDIA, "Button.java", consumer("Css", entry.getKey(), "color:red;")));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains(entry.getValue()), result.diagnostics());
        }
        for (var url : List.of("asset:media.missing", "asset:other.icon", "../icon.svg")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Media.java",
                    MEDIA,
                    "Button.java",
                    consumer("Css", "@CssImport(value=Media.class,name=\"media\")", "background:url('" + url + "');")));
            assertFalse(result.success(), url);
            assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
        }
    }

    @Test
    public void fontImportsRejectEmptyUnknownDuplicateUnusedReservedAndConflictingSelections() throws Exception {
        var cases = Map.of(
                "font=\"\"",
                "font must not be empty",
                "font=\"absent\"",
                "Unknown owned font family",
                "font=\"My Heading\",id=\"x\"",
                "mutually exclusive",
                "font=\"My Heading\",property=\"x\"",
                "mutually exclusive",
                "font=\"My Heading\",name=\"serif\"",
                "Reserved imported font",
                "font=\"My Heading\"",
                "Unused font import");
        for (var entry : cases.entrySet()) {
            var result = CssProcessorTest.compile(Map.of(
                    "Media.java",
                    MEDIA,
                    "Fonts.java",
                    FONTS,
                    "Button.java",
                    consumer("Css", "@CssImport(value=Fonts.class," + entry.getKey() + ")", "color:red;")));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains(entry.getValue()), result.diagnostics());
        }
        var duplicate = CssProcessorTest.compile(Map.of(
                "Media.java",
                MEDIA,
                "Fonts.java",
                FONTS,
                "Button.java",
                consumer(
                        "Css",
                        "@CssImport(value=Fonts.class,font=\"My Heading\",name=\"heading\")"
                                + "@CssImport(value=Fonts.class,font=\"my heading\",name=\"HEADING\")",
                        "font-family:heading;")));
        assertFalse(duplicate.success());
        assertTrue(duplicate.diagnostics().contains("Duplicate imported font family"), duplicate.diagnostics());
        String a = IMPORTS
                + "@CssImport(value=B.class,font=\"heading\") @GlobalCss(\"\"\"\n"
                + "body{font-family:heading;}\n"
                + "\"\"\") class A {}";
        String b = a.replace("B.class", "A.class").replace("class A", "class B");
        var cyclic = CssProcessorTest.compile(Map.of("A.java", a, "B.java", b));
        assertFalse(cyclic.success());
        assertTrue(cyclic.diagnostics().contains("Cyclic font import"), cyclic.diagnostics());
    }
}
