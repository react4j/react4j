package react4j.css.linker;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.testng.annotations.Test;

public final class BindingSourceMapTest {
    @Test
    public void followsDeclaredSourcesForNestedImplementationsAndPreservesSuppliedContent() throws Exception {
        Path tree = Files.createTempDirectory("binding-source-map");
        try {
            Path source = tree.resolve("demo/View_Styled_Button$Implementation.impl.java.js");
            Files.createDirectories(source.getParent());
            String java = "package demo;\r\n"
                    + "final class View_Styled_Button {\r\n"
                    + "\tString style() { return \"é😀\"; }\r\n"
                    + "}\r\n";
            Files.createDirectories(tree.resolve("java/demo"));
            Files.writeString(tree.resolve("java/demo/View_Styled_Button.java"), java);
            Path map = source.resolveSibling("View_Styled_Button$Implementation.js.map");
            String document = """
                {"version":3,"sourceRoot":"../java/demo","sources":["View_Styled_Button.java","Other.java"],
                "sourcesContent":[null,"final class Other {}"],"names":[],"mappings":"AAAA"}
                """;
            Files.writeString(map, document);
            var loaded = BindingSourceMap.read(source, tree);
            var enriched = JsonParser.parseString(loaded.input().getRawSourceMapContents())
                    .getAsJsonObject();
            assertEquals(enriched.getAsJsonArray("sourcesContent").get(0).getAsString(), java);
            assertEquals(enriched.getAsJsonArray("sourcesContent").get(1).getAsString(), "final class Other {}");
            assertEquals(
                    Objects.requireNonNull(loaded.mapping().getMappingForLine(1, 1))
                            .getOriginalFile(),
                    "View_Styled_Button.java");
            assertEquals(Files.readString(map), document);
            assertEquals(loaded.input().getOriginalPath(), map.toString());
        } finally {
            remove(tree);
        }
    }

    @Test
    public void fillsAbsentAndNullContentsFromAdjacentJava() throws Exception {
        Path tree = Files.createTempDirectory("binding-source-map");
        try {
            Path source = tree.resolve("Styled_Button.impl.java.js");
            String java = "final class Styled_Button { String style() { return \"root\"; } }";
            Files.writeString(tree.resolve("Styled_Button.java"), java);
            for (String field : List.of("", ",\"sourcesContent\":null", ",\"sourcesContent\":[null]")) {
                Files.writeString(
                        tree.resolve("Styled_Button.js.map"),
                        "{\"version\":3,\"sources\":[\"Styled_Button.java\"],\"names\":[],\"mappings\":\"AAAA\"" + field
                                + "}");
                var map = BindingSourceMap.read(source, tree);
                assertEquals(
                        JsonParser.parseString(map.input().getRawSourceMapContents())
                                .getAsJsonObject()
                                .getAsJsonArray("sourcesContent")
                                .get(0)
                                .getAsString(),
                        java);
            }
        } finally {
            remove(tree);
        }
    }

    @Test
    public void rejectsMissingMalformedAndUnresolvableCompanions() throws Exception {
        Path tree = Files.createTempDirectory("binding-source-map");
        try {
            Path source = tree.resolve("Styled_Button.impl.java.js");
            assertInvalid(source, tree);
            for (String document : List.of(
                    "not json",
                    "[]",
                    "{}",
                    "{\"version\":3,\"sources\":[3],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":3,\"sources\":[\"Missing.java\"],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":3,\"sources\":[\"../Outside.java\"],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":3,\"sources\":[\"wrong.js\"],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":3,\"sources\":[\"X.java\"],\"sourcesContent\":[],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":3,\"sources\":[\"X.java\"],\"sourcesContent\":[false],\"names\":[],\"mappings\":\"\"}",
                    "{\"version\":2,\"sources\":[\"X.java\"],\"sourcesContent\":[\"class X"
                            + " {}\"],\"names\":[],\"mappings\":\"\"}")) {
                Files.writeString(tree.resolve("Styled_Button.js.map"), document);
                assertInvalid(source, tree);
            }
        } finally {
            remove(tree);
        }
    }

    private static void assertInvalid(Path source, Path tree) {
        var error = expectThrows(IllegalArgumentException.class, () -> BindingSourceMap.read(source, tree));
        assertTrue(Objects.requireNonNull(error.getMessage())
                .contains("Invalid generated CSS binding source map: " + source));
        assertNotNull(error.getCause());
    }

    private static void remove(Path tree) throws Exception {
        try (var files = Files.walk(tree)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
