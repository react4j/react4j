package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import com.helger.css.writer.CSSWriterSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource;
import react4j.css.internal.SourceMap;

public final class BundleTest {
    private @Nullable Path directory;

    @BeforeMethod
    public void prepare() throws Exception {
        directory = Files.createTempDirectory("css-bundle-test-");
    }

    @AfterMethod
    public void clean() throws Exception {
        try (var files = Files.walk(dir())) {
            for (var path : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private Path dir() {
        return Objects.requireNonNull(directory);
    }

    private Path tree(String name) throws Exception {
        return Files.createDirectories(dir().resolve(name));
    }

    private static void component(Path tree, String name, String css) throws Exception {
        var mapped = new SourceMap.Writer()
                .append(css, new SourceMap.Location(name + ".java", "class A {\n  // " + css + "\n}\n", 1, 5));
        Path path = tree.resolve(name + ".css");
        Files.createDirectories(Objects.requireNonNull(path.getParent()));
        Files.writeString(path, css + SourceMap.reference(path.getFileName().toString()));
        Files.writeString(Path.of(path + ".map"), mapped.map(path.getFileName().toString()));
        Files.writeString(
                Path.of(path + ".json"),
                new CssResource(name.replace('/', '.'), CssResource.Kind.COMPONENT, List.of()).json());
    }

    @Test
    public void assemblyDeduplicatesTreesSortsOwnersAndComposesReadableMaps() throws Exception {
        Path one = tree("one");
        Path two = tree("two");
        component(one, "demo/Z", ".z{color:green;}");
        component(two, "demo/A", ".a{color:red;}");
        Files.writeString(one.resolve("unused.js"), "ignored");
        Files.writeString(one.resolve("unused.js.map"), "ignored");
        Path shared = dir().resolve("base.css");
        String original = "button {\r\n  color: blue;\r\n}\r\n";
        Files.writeString(shared, original);
        var result = Bundle.assemble(List.of(one, two, one), List.of(shared), List.of(), "application.css");
        assertEquals(result, Bundle.assemble(List.of(two, one), List.of(shared), List.of(), "application.css"));
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                result.css(), new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        var layers = sheet.getAllLayerRules();
        assertEquals(layers.size(), 3);
        assertEquals(layers.get(0).getAllSelectors(), List.of("shared", "components"));
        assertEquals(layers.get(1).getAllSelectors(), List.of("shared"));
        assertEquals(layers.get(1).getRuleCount(), 1);
        assertEquals(layers.get(2).getAllSelectors(), List.of("components"));
        assertEquals(layers.get(2).getRuleCount(), 2);
    }

    @Test
    public void layersAndSourceMapSectionsRetainInputOrder() throws Exception {
        Path one = tree("one");
        component(one, "demo/Z", ".z{color:green;}");
        component(one, "demo/A", ".a{color:red;}");
        Path first = dir().resolve("first.css");
        Path second = dir().resolve("second.css");
        Files.writeString(first, "button { color: red; }\r\n");
        Files.writeString(second, "@media screen { button { color: blue; } }\n");
        var result = Bundle.assemble(List.of(one), List.of(first, second), List.of(), "app.css");
        assertTrue(result.css().indexOf("color: red") < result.css().indexOf("@media"));
        assertTrue(result.css().indexOf(".a{") < result.css().indexOf(".z{"));
        var parsed = JsonParser.parseString(result.map()).getAsJsonObject();
        assertEquals(parsed.get("file").getAsString(), "app.css");
        assertEquals(parsed.getAsJsonArray("sections").size(), 4);
        assertEquals(
                parsed.getAsJsonArray("sections")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("map")
                        .getAsJsonArray("sourcesContent")
                        .get(0)
                        .getAsString(),
                Files.readString(first));
        var consumer = new SourceMapConsumerV3();
        consumer.parse(result.map());
        int start = result.css().indexOf(".z{");
        int line = (int) result.css()
                        .substring(0, start)
                        .chars()
                        .filter(c -> '\n' == c)
                        .count()
                + 1;
        var original = Objects.requireNonNull(consumer.getMappingForLine(line, 1));
        assertEquals(original.getOriginalFile(), "demo/Z.java");
        assertEquals(original.getLineNumber(), 2);
        assertEquals(original.getColumnPosition(), 6);
    }

    @Test
    public void emptyInputsRetainPrecedenceWithoutEmptyLayers() throws Exception {
        Path empty = tree("empty");
        Path shared = dir().resolve("comments.css");
        Files.writeString(shared, "/* comments */");
        component(empty, "Empty", "");
        for (var result : List.of(
                Bundle.assemble(List.of(), List.of(), List.of(), "empty.css"),
                Bundle.assemble(List.of(empty), List.of(shared), List.of(), "empty.css"))) {
            assertEquals(result.css(), "@layer shared,components;\n");
            assertEquals(
                    JsonParser.parseString(result.map())
                            .getAsJsonObject()
                            .getAsJsonArray("sections")
                            .size(),
                    0);
        }
    }

    @Test
    public void sharedRulesCannotOverrideTheLayerContract() throws Exception {
        Path shared = dir().resolve("bad.css");
        for (var css : List.of(
                "button { color: red !important; }",
                "@import 'missing.css';",
                "@layer components { body { color: red; } }",
                "button { color red; }")) {
            Files.writeString(shared, css);
            var error = expectThrows(
                    IllegalArgumentException.class,
                    () -> Bundle.assemble(List.of(), List.of(shared), List.of(), "app.css"));
            assertTrue(String.valueOf(error.getMessage()).startsWith(shared.toString()), error.getMessage());
        }
    }

    @Test
    public void rejectsMissingConflictingAndMalformedResourcePairs() throws Exception {
        Path one = tree("one");
        Path two = tree("two");
        component(one, "A", ".a{color:red;}");
        component(two, "A", ".a{color:red;}");
        var duplicate = expectThrows(
                IllegalArgumentException.class,
                () -> Bundle.assemble(List.of(one, two), List.of(), List.of(), "app.css"));
        assertTrue(String.valueOf(duplicate.getMessage()).contains("Duplicate CSS resource"));
        Files.delete(two.resolve("A.css.map"));
        expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(two), List.of(), List.of(), "app.css"));
        Files.delete(two.resolve("A.css"));
        Files.writeString(two.resolve("A.css.map"), "{}");
        expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(two), List.of(), List.of(), "app.css"));
        Path map = one.resolve("A.css.map");
        for (var invalid : List.of("{", "[]", "{}", "{\"version\":2}", "{\"version\":3,\"file\":\"wrong.css\"}")) {
            Files.writeString(map, invalid);
            expectThrows(
                    IllegalArgumentException.class,
                    () -> Bundle.assemble(List.of(one), List.of(), List.of(), "app.css"));
        }
        component(one, "A", ".a{color:red;}");
        Files.writeString(one.resolve("A.css"), ".a{color:red;}");
        expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(one), List.of(), List.of(), "app.css"));
    }

    @Test
    public void commandUsesExplicitOutputNamesAndRejectsInvalidArguments() throws Exception {
        Path css = dir().resolve("theme.css");
        Path map = dir().resolve("theme.map");
        Path input = tree("input");
        component(input, "A", ".a{color:red;}");
        Path shared = dir().resolve("base.css");
        Files.writeString(shared, "button { color: blue; }");
        Path arguments = dir().resolve("arguments");
        Files.writeString(
                arguments,
                String.join(
                        "\n",
                        "--css",
                        css.toString(),
                        "--map",
                        map.toString(),
                        "--input",
                        input.toString(),
                        "--shared",
                        shared.toString()));
        Bundle.main(new String[] {"@" + arguments});
        assertTrue(Files.readString(css).endsWith("/*# sourceMappingURL=theme.map */\n"));
        assertEquals(
                JsonParser.parseString(Files.readString(map))
                        .getAsJsonObject()
                        .get("file")
                        .getAsString(),
                "theme.css");
        for (var args : List.of(
                List.of("--css"),
                List.<String>of(),
                List.of("--unknown", "x"),
                List.of("--css", "x", "--css", "y"),
                List.of("--css", "x", "--map", "x"))) {
            expectThrows(IllegalArgumentException.class, () -> Bundle.run(args));
        }
    }

    private static void resource(Path tree, String owner, CssResource.Kind kind, String css, String... dependencies)
            throws Exception {
        component(tree, owner.replace('.', '/'), css);
        Files.writeString(
                tree.resolve(CssResource.path(owner) + ".json"),
                new CssResource(owner, kind, List.of(dependencies)).json());
    }

    @Test
    public void sharedKeyframesAreIncludedOnceOnlyWhenReferenced() throws Exception {
        Path input = tree("shared");
        resource(input, "demo.Pulse", CssResource.Kind.KEYFRAMES, "@keyframes pulse{from{opacity:0;}to{opacity:1;}}");
        resource(input, "demo.Unused", CssResource.Kind.KEYFRAMES, "@keyframes unused{from{opacity:0;}}");
        resource(input, "demo.A", CssResource.Kind.COMPONENT, ".a{animation-name:pulse;}", "demo.Pulse");
        resource(input, "demo.B", CssResource.Kind.COMPONENT, ".b{animation:pulse 1s;}", "demo.Pulse");
        var result = Bundle.assemble(List.of(input, input), List.of(), List.of(), "app.css");
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(result.css(), new CSSReaderSettings()));
        var layer = sheet.getAllLayerRules().get(1);
        assertEquals(
                layer.getAllRules().stream()
                        .filter(rule -> rule instanceof CSSKeyframesRule)
                        .count(),
                1L);
        assertEquals(layer.getAllStyleRules().size(), 2);
        assertFalse(result.css().contains("unused"));
        assertEquals(
                JsonParser.parseString(result.map())
                        .getAsJsonObject()
                        .getAsJsonArray("sections")
                        .size(),
                3);
        assertEquals(result, Bundle.assemble(List.of(input), List.of(), List.of(), "app.css"));
        Files.delete(input.resolve("demo/A.css"));
        Files.delete(input.resolve("demo/A.css.map"));
        Files.delete(input.resolve("demo/A.css.json"));
        Files.delete(input.resolve("demo/B.css"));
        Files.delete(input.resolve("demo/B.css.map"));
        Files.delete(input.resolve("demo/B.css.json"));
        assertEquals(
                Bundle.assemble(List.of(input), List.of(), List.of(), "app.css").css(), "@layer shared,components;\n");
    }

    @Test
    public void missingResourcesAndGlobalInclusionCyclesFail() throws Exception {
        Path input = tree("graph");
        resource(input, "A", CssResource.Kind.COMPONENT, ".a{color:red;}", "Missing");
        var missing = expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(input), List.of(), List.of(), "app.css"));
        assertTrue(String.valueOf(missing.getMessage()).contains("Missing CSS dependency: Missing"));
        resource(input, "Missing", CssResource.Kind.COMPONENT, ".b{color:blue;}");
        assertTrue(Bundle.assemble(List.of(input), List.of(), List.of(), "app.css")
                .css()
                .contains(".b{color:blue;}"));
        resource(input, "Missing", CssResource.Kind.GLOBAL, "body{color:red;}", "Other");
        resource(input, "Other", CssResource.Kind.GLOBAL, "body{color:blue;}", "Missing");
        var cycle = expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(input), List.of(), List.of(), "app.css"));
        assertTrue(String.valueOf(cycle.getMessage()).contains("Cyclic global CSS inclusion"));
        Files.writeString(input.resolve("A.css.json"), "{}");
        expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(input), List.of(), List.of(), "app.css"));
        Files.delete(input.resolve("A.css.json"));
        var absent = expectThrows(
                IllegalArgumentException.class, () -> Bundle.assemble(List.of(input), List.of(), List.of(), "app.css"));
        assertTrue(String.valueOf(absent.getMessage()).contains("Missing CSS source map or metadata"));
    }

    @Test
    public void globalsAreExplicitOrderedRootsWithDependencyFirstDeduplication() throws Exception {
        Path input = tree("globals");
        resource(input, "Base", CssResource.Kind.GLOBAL, "body{color:red;}");
        resource(input, "Theme", CssResource.Kind.GLOBAL, "body{color:blue;}", "Base");
        resource(input, "Other", CssResource.Kind.GLOBAL, "body{color:green;}");
        resource(input, "Empty", CssResource.Kind.GLOBAL, "");
        Path legacy = dir().resolve("legacy.css");
        Files.writeString(legacy, "body{color:black;}");
        assertEquals(
                Bundle.assemble(List.of(input), List.of(), List.of(), "app.css").css(), "@layer shared,components;\n");
        var result =
                Bundle.assemble(List.of(input), List.of(legacy), List.of("Theme", "Base", "Theme", "Empty"), "app.css");
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(result.css(), new CSSReaderSettings()));
        var layers = sheet.getAllLayerRules();
        assertEquals(layers.size(), 2);
        var rules = layers.get(1).getAllStyleRules();
        assertEquals(
                rules.stream()
                        .map(rule -> Objects.requireNonNull(rule.getDeclarationOfPropertyName("color"))
                                .getExpression()
                                .getAsCSSString(new CSSWriterSettings(true), 0))
                        .toList(),
                List.of("black", "red", "blue"));
        var alternate = Bundle.assemble(List.of(input), List.of(), List.of("Other"), "app.css");
        assertTrue(alternate.css().contains("color:green"));
        assertFalse(alternate.css().contains("color:blue"));
        assertFalse(alternate.css().contains("color:red"));
        assertEquals(
                Bundle.assemble(List.of(input), List.of(), List.of("Empty"), "app.css")
                        .css(),
                "@layer shared,components;\n");
        for (var owner : List.of("Missing", "../Base")) {
            var error = expectThrows(
                    IllegalArgumentException.class,
                    () -> Bundle.assemble(List.of(input), List.of(), List.of(owner), "app.css"));
            assertTrue(String.valueOf(error.getMessage()).contains("Global CSS root"));
        }
        resource(input, "Component", CssResource.Kind.COMPONENT, ".component{color:white;}", "Theme");
        resource(input, "Animation", CssResource.Kind.KEYFRAMES, "@keyframes a{}");
        for (var owner : List.of("Component", "Animation")) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> Bundle.assemble(List.of(input), List.of(), List.of(owner), "app.css"));
        }
        var implicit = Bundle.assemble(List.of(input), List.of(), List.of(), "app.css");
        assertTrue(implicit.css().contains("color:red"));
        assertTrue(implicit.css().contains("color:blue"));
        assertFalse(implicit.css().contains("color:green"));
        resource(input, "Base", CssResource.Kind.GLOBAL, "body{color:red;}", "Theme");
        var cycle = expectThrows(
                IllegalArgumentException.class,
                () -> Bundle.assemble(List.of(input), List.of(), List.of("Theme"), "app.css"));
        assertTrue(String.valueOf(cycle.getMessage()).contains("Theme -> Base -> Theme"));
    }

    @Test
    public void peerCyclesAndMixedRetentionPathsDoNotDefineGlobalCascadeOrder() throws Exception {
        Path input = tree("peers");
        resource(input, "A", CssResource.Kind.COMPONENT, ".a+.b{color:red;}", "B", "Theme");
        resource(input, "B", CssResource.Kind.COMPONENT, ".b+.a{color:blue;}", "A", "Other");
        resource(input, "Base", CssResource.Kind.GLOBAL, "body{color:black;}");
        resource(input, "Theme", CssResource.Kind.GLOBAL, "body{color:red;}", "Base", "B");
        resource(input, "Other", CssResource.Kind.GLOBAL, "body{color:blue;}", "Base", "A");
        resource(input, "Unused", CssResource.Kind.GLOBAL, "body{color:pink;}", "Missing");
        var result = Bundle.assemble(List.of(input), List.of(), List.of("Other"), "app.css");
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(result.css(), new CSSReaderSettings()));
        var globals = sheet.getAllLayerRules().get(1).getAllStyleRules();
        assertEquals(
                globals.stream()
                        .map(r -> Objects.requireNonNull(r.getDeclarationOfPropertyName("color"))
                                .getExpression()
                                .getAsCSSString(new CSSWriterSettings(true), 0))
                        .toList(),
                List.of("black", "blue", "red"));
        var peers = sheet.getAllLayerRules().get(2).getAllStyleRules();
        assertEquals(
                peers.stream()
                        .map(r -> r.getSelectorsAsCSSString(new CSSWriterSettings(true), 0))
                        .toList(),
                List.of(".a+.b", ".b+.a"));
        assertEquals(
                JsonParser.parseString(result.map())
                        .getAsJsonObject()
                        .getAsJsonArray("sections")
                        .size(),
                5);
        assertFalse(result.css().contains("pink"));
        assertEquals(
                result,
                Bundle.assemble(List.of(input, input), List.of(), List.of("Other", "Base", "Other"), "app.css"));
        resource(input, "Base", CssResource.Kind.GLOBAL, "body{color:black;}", "Theme");
        var cycle = expectThrows(
                IllegalArgumentException.class,
                () -> Bundle.assemble(List.of(input), List.of(), List.of("Other"), "app.css"));
        assertTrue(String.valueOf(cycle.getMessage()).contains("Base -> Theme -> Base"), cycle.getMessage());
    }

    @Test
    public void selfPeerReferencesAndGlobalDiamondsEmitOnce() throws Exception {
        Path input = tree("self");
        resource(input, "A", CssResource.Kind.COMPONENT, ".a+.a{color:red;}", "A", "Theme");
        resource(input, "Base", CssResource.Kind.GLOBAL, "body{color:black;}");
        resource(input, "Left", CssResource.Kind.GLOBAL, "body{color:red;}", "Base");
        resource(input, "Right", CssResource.Kind.GLOBAL, "body{color:blue;}", "Base");
        resource(input, "Theme", CssResource.Kind.GLOBAL, "body{color:green;}", "Right", "Left");
        var result = Bundle.assemble(List.of(input), List.of(), List.of(), "app.css");
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(result.css(), new CSSReaderSettings()));
        assertEquals(
                sheet.getAllLayerRules().get(1).getAllStyleRules().stream()
                        .map(r -> Objects.requireNonNull(r.getDeclarationOfPropertyName("color"))
                                .getExpression()
                                .getAsCSSString(new CSSWriterSettings(true), 0))
                        .toList(),
                List.of("black", "blue", "red", "green"));
        assertEquals(sheet.getAllLayerRules().get(2).getRuleCount(), 1);
    }

    @Test
    public void globalRootsKeepReferencedKeyframesAndCommandLineSelection() throws Exception {
        Path input = tree("global-animation");
        resource(input, "demo.Pulse", CssResource.Kind.KEYFRAMES, "@keyframes pulse{to{opacity:0;}}");
        resource(input, "demo.Outer$Defaults", CssResource.Kind.GLOBAL, "body{animation:pulse 1s;}", "demo.Pulse");
        var result = Bundle.assemble(List.of(input), List.of(), List.of("demo.Outer$Defaults"), "app.css");
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(result.css(), new CSSReaderSettings()));
        assertEquals(sheet.getAllLayerRules().size(), 3);
        assertEquals(sheet.getAllLayerRules().get(1).getAllStyleRules().size(), 1);
        assertEquals(sheet.getAllLayerRules().get(2).getAllRules().size(), 1);
        Path css = dir().resolve("selected.css");
        Path map = dir().resolve("selected.map");
        Bundle.run(List.of(
                "--input",
                input.toString(),
                "--global",
                "demo.Outer$Defaults",
                "--css",
                css.toString(),
                "--map",
                map.toString()));
        assertTrue(Files.readString(css).contains("@keyframes pulse"));
        assertTrue(Files.readString(css).contains("animation:pulse"));
        var consumer = new SourceMapConsumerV3();
        consumer.parse(Files.readString(map));
        int index = result.css().indexOf("body{");
        int line = (int) result.css()
                        .substring(0, index)
                        .chars()
                        .filter(c -> '\n' == c)
                        .count()
                + 1;
        assertEquals(
                Objects.requireNonNull(consumer.getMappingForLine(line, 1)).getOriginalFile(),
                "demo/Outer$Defaults.java");
    }
}
