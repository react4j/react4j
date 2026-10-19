package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class AssetProcessorTest {
    private static final String IMPORTS = "package demo;\nimport org.jspecify.annotations.NullMarked;\n"
            + "import react4j.css.Asset;\nimport react4j.css.Assets;\n";

    private static String source(String declaration, String members) {
        return IMPORTS + "@NullMarked @Assets\n" + declaration + " { " + members + " }";
    }

    private static String text(CssProcessorTest.Result result, String path) {
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void publicAndPackageDefinitionsHaveLazyPackageAccessBindings() throws Exception {
        for (var declaration : List.of("abstract class Media", "public abstract class Media")) {
            for (boolean formatted : List.of(false, true)) {
                var result = CssProcessorTest.compile(
                        Map.of(
                                "Media.java",
                                source(
                                        declaration,
                                        "static Media get() { return Assets_Media.get(); } static Media of() { return"
                                                + " get(); }@Asset(\"card/icon.svg\") public abstract String"
                                                + " icon();@Asset(\"portrait.png\") abstract String portrait();static"
                                                + " String helper() { return \"helper\"; } String concrete() { return"
                                                + " helper(); }")),
                        List.of("-Areact4j.css.format_generated_source=" + formatted),
                        "demo.Media");
                assertTrue(result.success(), result.diagnostics());
                assertEquals(result.diagnostics(), "");
                assertEquals(
                        result.values(),
                        Map.of(
                                "demo.Media.icon",
                                "asset:demo/card/icon.svg",
                                "demo.Media.portrait",
                                "asset:demo/portrait.png"));
                String generated = text(result, "generated/demo/Assets_Media.java");
                assertTrue(generated.contains("final class Assets_Media extends Media"), generated);
                assertTrue(generated.contains("private Assets_Media()"), generated);
                assertTrue(generated.contains("private static final class Internal"), generated);
                assertFalse(generated.contains("public final class Assets_Media"), generated);
                var metadata = JsonParser.parseString(text(result, "classes/demo/Media.assets.json"))
                        .getAsJsonObject();
                assertEquals(metadata.get("implementation").getAsString(), "demo.Assets_Media");
                assertEquals(metadata.getAsJsonArray("bindings").size(), 2);
                assertFalse(result.files().keySet().stream().anyMatch(path -> path.endsWith(".css")));
            }
        }
    }

    @Test
    public void interfacesGenericsHelpersAndOverridesFollowEffectiveMethodDeclarations() throws Exception {
        var result = CssProcessorTest.compile(
                Map.of(
                        "Media.java",
                        source(
                                        "abstract class Media implements Contract<String>",
                                        "static Media of() { return Assets_Media.get(); }@Override"
                                            + " @Asset(\"override.svg\") public abstract String inherited();@Override"
                                            + " public String helper() { return \"ok\"; }")
                                + "interface Contract<T> { @Asset(\"base.svg\") T inherited();"
                                + "@Asset(\"icon.svg\") String icon(); String helper();"
                                + "int CONSTANT = 1; default String decorate() { return icon(); }"
                                + "static String staticHelper() { return \"static\"; } }"),
                "demo.Media");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(
                result.values(),
                Map.of("demo.Media.icon", "asset:demo/icon.svg", "demo.Media.inherited", "asset:demo/override.svg"));
        var missing = CssProcessorTest.compile(Map.of(
                "Media.java",
                source("abstract class Media implements Contract", "@Override public abstract String inherited();")
                        + "interface Contract { @Asset(\"base.svg\") String inherited(); }"));
        assertFalse(missing.success());
        assertTrue(missing.diagnostics().contains("abstract methods require @Asset"), missing.diagnostics());
    }

    @Test
    public void separatelyCompiledInterfacesRetainAssetAnnotations() throws Exception {
        var contract = CssProcessorTest.compile(Map.of(
                "Contract.java",
                "package library;\n"
                        + "import org.jspecify.annotations.NullMarked; import react4j.css.Asset;\n"
                        + "@NullMarked public interface Contract { @Asset(\"icon.svg\") String icon(); }"));
        assertTrue(contract.success(), contract.diagnostics());
        var directory = Files.createTempDirectory("asset-interface");
        try {
            var jar = directory.resolve("contract.jar");
            try (var output = new JarOutputStream(Files.newOutputStream(jar))) {
                for (var file : contract.files().entrySet()) {
                    if (file.getKey().endsWith(".class")) {
                        output.putNextEntry(new JarEntry(file.getKey().substring("classes/".length())));
                        output.write(Base64.getDecoder().decode(file.getValue()));
                        output.closeEntry();
                    }
                }
            }
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Media.java",
                            source(
                                    "abstract class Media implements library.Contract",
                                    "static Media of() { return Assets_Media.get(); }")),
                    List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + jar),
                    "demo.Media");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Media.icon"), "asset:demo/icon.svg");
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void nestedNamesDefaultPackagesDeprecationAndShadowedTypesUseTheSharedGenerator() throws Exception {
        var nested = CssProcessorTest.compile(Map.of(
                "Owner.java",
                IMPORTS
                        + "@NullMarked class Owner { @Assets public abstract static class Media {"
                        + "public static Media get() { return Owner_Assets_Media.get(); }"
                        + "@Asset(\"icon.svg\") abstract String icon(); } }"));
        assertTrue(nested.success(), nested.diagnostics());
        assertTrue(nested.files().containsKey("generated/demo/Owner_Assets_Media.java"));
        assertTrue(text(nested, "generated/demo/Owner_Assets_Media.java").contains("public static Owner.Media get()"));
        var defaults = CssProcessorTest.compile(Map.of(
                "Media.java",
                source("abstract class Media", "@Asset(\"icon.svg\") abstract String icon();")
                        .replace("package demo;\n", "")));
        assertTrue(defaults.success(), defaults.diagnostics());
        assertTrue(text(defaults, "classes/Media.assets.json").contains("\"path\":\"icon.svg\""));
        var deprecated = CssProcessorTest.compile(Map.of(
                "Owner.java",
                IMPORTS
                        + "@NullMarked @Deprecated class Owner { @Assets abstract static class Media {"
                        + "@Deprecated @Asset(\"icon.svg\") abstract String icon(); } }"));
        assertTrue(deprecated.success(), deprecated.diagnostics());
        assertEquals(deprecated.diagnostics(), "");
        var shadowed = CssProcessorTest.compile(Map.of(
                "Media.java",
                source(
                        "abstract class Media",
                        "static class String {} static class Internal {}"
                                + "@Asset(\"icon.svg\") abstract java.lang.String icon();")));
        assertTrue(shadowed.success(), shadowed.diagnostics());
        assertTrue(text(shadowed, "generated/demo/Assets_Media.java").contains("java.lang.String icon()"));
    }

    @DataProvider
    public Object[][] invalid() {
        var cases = new ArrayList<Object[]>();
        String accessor = "@Asset(\"icon.svg\") abstract String icon();";
        for (var declaration : List.of(
                "class Media",
                "final class Media",
                "interface Media",
                "@interface Media",
                "abstract class Media<T>",
                "abstract class Media extends Parent")) {
            String members = declaration.contains("interface") ? "" : accessor;
            String text = source(declaration, members);
            if (declaration.contains("Parent")) {
                text += "class Parent {}";
            }
            cases.add(new Object[] {text, "require"});
        }
        for (var member : List.of(
                "String field;",
                "static String field;",
                "static final int VALUE=1;",
                "Media() {}",
                "private Media() {}",
                "Media(int x) {}")) {
            cases.add(new Object[] {source("abstract class Media", member + accessor), "fields or constructors"});
        }
        for (var block : List.of("{}", "static {}")) {
            cases.add(new Object[] {source("abstract class Media", block + accessor), "initializers"});
        }
        cases.add(new Object[] {source("abstract class Media", ""), "at least one @Asset accessor"});
        cases.add(new Object[] {
            source("abstract class Media", "abstract String icon();"), "abstract methods require @Asset"
        });
        for (var method : List.of(
                "String icon() { return \"x\"; }",
                "static String icon() { return \"x\"; }",
                "abstract int icon();",
                "abstract String icon(int value);",
                "abstract <T> String icon();",
                "abstract String icon() throws Exception;")) {
            cases.add(new Object[] {
                source("abstract class Media", "@Asset(\"icon.svg\") " + method), "accessors must be abstract"
            });
        }
        for (var path : List.of(
                "",
                "/icon.svg",
                "../icon.svg",
                "a/../icon.svg",
                "a//icon.svg",
                "a/./icon.svg",
                "a\\\\icon.svg",
                "icon.svg#fragment",
                "icon.svg?size=2",
                "https://example.org/icon.svg",
                "a%20b.svg")) {
            cases.add(new Object[] {
                source("abstract class Media", "@Asset(\"" + path + "\") abstract String icon();"), "path"
            });
        }
        for (var annotation : List.of("CssId", "CssAnimation", "CssCustomProperty", "CssStable")) {
            cases.add(new Object[] {
                source("abstract class Media", "@react4j.css." + annotation + " " + accessor), "must use only @Asset"
            });
        }
        cases.add(new Object[] {
            source("abstract class Media", "String get() { return \"wrong\"; }" + accessor),
            "conflicts with the generated singleton"
        });
        cases.add(new Object[] {
            source("abstract class Media", "static final Media get() { return null; }" + accessor),
            "conflicts with the generated singleton"
        });
        cases.add(new Object[] {
            IMPORTS + "@NullMarked class Outer { @Assets abstract class Media {" + accessor + "}}",
            "nested classes must be static"
        });
        cases.add(new Object[] {
            IMPORTS + "@NullMarked class Outer { @Assets private abstract static class Media {" + accessor + "}}",
            "accessible"
        });
        cases.add(new Object[] {
            IMPORTS + "@NullMarked class Outer { private static class Hidden { @Assets abstract static class Media {"
                    + accessor + "}}}",
            "accessible from their package"
        });
        cases.add(new Object[] {
            source("sealed abstract class Media permits Child", accessor)
                    + "final class Child extends Media { String icon() { return \"x\"; } }",
            "non-generic class"
        });
        for (var annotation : List.of(
                "@react4j.css.Css(\"\"\"\ncolor:red;\n\"\"\")",
                "@react4j.css.GlobalCss(\"\"\"\nbody{}\n\"\"\")",
                "@react4j.css.Keyframes(\"\"\"\nfrom{}\n\"\"\")",
                "@react4j.css.CssStable",
                "@react4j.css.CssImport(Media.class)")) {
            cases.add(new Object[] {
                source("abstract class Media", accessor).replace("@Assets", annotation + " @Assets"),
                "cannot be combined"
            });
        }
        cases.add(new Object[] {
            source("abstract class Media", accessor).replace("@Assets", ""),
            "@Asset methods require an @Assets definition"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalid")
    public void invalidDeclarationsHaveDiagnosticsAndEmitNothing(String source, String diagnostic) throws Exception {
        var result = CssProcessorTest.compile(Map.of("Media.java", source));
        assertFalse(result.success(), source);
        assertTrue(result.diagnostics().contains(diagnostic), result.diagnostics());
        assertTrue(result.files().isEmpty(), result.files().keySet().toString());
    }
}
