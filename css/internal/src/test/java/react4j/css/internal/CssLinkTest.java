package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

public final class CssLinkTest {
    private static final String JAVASCRIPT_DIGEST = "2".repeat(64);

    private static CssLink link(boolean optimized) {
        return CssLink.create(
                new CssCatalogue(
                        Map.of(
                                "demo.Button",
                                new CssCatalogue.Resource(
                                        new CssResource(
                                                "demo.Button",
                                                CssResource.Kind.COMPONENT,
                                                List.of(),
                                                List.of(),
                                                "demo.Styled_Button",
                                                List.of(new CssResource.Binding(
                                                        "style",
                                                        CssResource.SymbolKind.CLASS,
                                                        "root",
                                                        List.of(),
                                                        List.of(new CssResource.SymbolId(
                                                                "demo.Button", CssResource.SymbolKind.CLASS, "")))),
                                                List.of(new CssResource.Symbol(
                                                        CssResource.SymbolKind.CLASS, "", "root", false)),
                                                List.of()),
                                        "",
                                        JsonParser.parseString("{}").getAsJsonObject())),
                        "1".repeat(64)),
                "//demo:app",
                optimized);
    }

    @Test
    public void configurationsAndReportsRoundTripWithStableBindingIdentity() {
        for (boolean optimized : List.of(true, false)) {
            var link = link(optimized);
            assertEquals(CssLink.read(link.json()), link);
            assertEquals(link.values().get(0).source(), "demo/Styled_Button.impl.java.js");
            var live = Set.of("demo.Button#style");
            assertEquals(link.readReport(link.report(live, JAVASCRIPT_DIGEST), JAVASCRIPT_DIGEST), live);
            assertEquals(link.readReport(link.report(Set.of(), JAVASCRIPT_DIGEST), JAVASCRIPT_DIGEST), Set.of());
            expectThrows(IllegalArgumentException.class, () -> link.report(Set.of("missing"), JAVASCRIPT_DIGEST));
        }
    }

    @Test
    public void missingMismatchedAndMalformedEvidenceCannotMeanAnEmptyLiveSet() {
        var link = link(true);
        String config = link.json();
        String report = link.report(Set.of("demo.Button#style"), JAVASCRIPT_DIGEST);
        for (var bad : List.of(
                "",
                "{",
                "null",
                "[]",
                "{}",
                config.replace("\"version\":3", "\"version\":2"),
                config.replace("\"optimized\":true", "\"optimized\":\"true\""),
                config.replace("\"kind\":\"CLASS\"", "\"kind\":\"UNKNOWN\""),
                config.replace("\"method\":\"style\"", "\"method\":null"),
                config.replace("\"link\":\"//demo:app\"", "\"link\":4"))) {
            expectThrows(IllegalArgumentException.class, () -> CssLink.read(bad));
        }
        for (var bad : List.of(
                "",
                "{",
                "null",
                "[]",
                "{}",
                report.replace("\"version\":3", "\"version\":2"),
                report.replace("//demo:app", "//demo:other"),
                report.replace("1".repeat(64), "2".repeat(64)),
                report.replace("\"optimized\":true", "\"optimized\":false"),
                report.replace("demo.Button#style", "missing"),
                report.replace("[\"demo.Button#style\"]", "[\"demo.Button#style\",\"demo.Button#style\"]"),
                report.replace("[\"demo.Button#style\"]", "[1]"),
                report.replace("[\"demo.Button#style\"]", "[null]"))) {
            expectThrows(IllegalArgumentException.class, () -> link.readReport(bad, JAVASCRIPT_DIGEST));
        }
        for (var field : List.of("compiled", "readable", "owner", "name")) {
            var changed = JsonParser.parseString(report).getAsJsonObject();
            changed.getAsJsonArray("names").get(0).getAsJsonObject().addProperty(field, "foreign");
            expectThrows(IllegalArgumentException.class, () -> link.readReport(changed.toString(), JAVASCRIPT_DIGEST));
        }
        var changedPolicy = JsonParser.parseString(report).getAsJsonObject();
        changedPolicy.getAsJsonObject("policy").addProperty("prefix", "foreign-");
        expectThrows(
                IllegalArgumentException.class, () -> link.readReport(changedPolicy.toString(), JAVASCRIPT_DIGEST));
        expectThrows(IllegalArgumentException.class, () -> link.readReport(report, "3".repeat(64)));
        expectThrows(IllegalArgumentException.class, () -> link.report(Set.of(), "bad"));
        for (var key : List.of("version", "link", "digest", "optimized", "values")) {
            var json = JsonParser.parseString(config).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> CssLink.read(json.toString()));
        }
        expectThrows(
                IllegalArgumentException.class, () -> new CssLink("", "1".repeat(64), true, List.of(), link.names()));
        expectThrows(IllegalArgumentException.class, () -> new CssLink("app", "bad", true, List.of(), link.names()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssLink(
                        "app",
                        "1".repeat(64),
                        true,
                        List.of(link.values().get(0), link.values().get(0)),
                        link.names()));
    }

    @Test
    public void catalogueCommandBindsResourceBytesAndRejectsInvalidOptions() throws Exception {
        Path directory = Files.createTempDirectory("css-catalogue");
        try {
            Path tree = Files.createDirectory(directory.resolve("input"));
            String metadata = new CssResource(
                            "Button",
                            CssResource.Kind.COMPONENT,
                            List.of(),
                            List.of(),
                            "Styled_Button",
                            List.of(new CssResource.Binding(
                                    "style",
                                    CssResource.SymbolKind.CLASS,
                                    "root",
                                    List.of(),
                                    List.of(new CssResource.SymbolId("Button", CssResource.SymbolKind.CLASS, "")))),
                            List.of(new CssResource.Symbol(CssResource.SymbolKind.CLASS, "", "root", false)),
                            List.of())
                    .json();
            Files.writeString(tree.resolve("Button.css"), ".root{color:red;}" + SourceMap.reference("Button.css"));
            Files.writeString(
                    tree.resolve("Button.css.map"),
                    new SourceMap.Writer().append(".root{color:red;}").map("Button.css"));
            Files.writeString(tree.resolve("Button.css.json"), metadata);
            Path output = directory.resolve("link.json");
            var args = new String[] {
                "--output", output.toString(), "--link", "//:app", "--mode", "optimized", "--input", tree.toString()
            };
            CssLink.main(args);
            var original = CssLink.read(Files.readString(output));
            CssLink.main(args);
            assertEquals(Files.readString(output), original.json());
            assertTrue(original.optimized());
            assertEquals(original, CssLink.create(CssCatalogue.read(List.of(tree, tree)), "//:app", true));
            Files.writeString(tree.resolve("Button.css"), ".root{color:blue;}" + SourceMap.reference("Button.css"));
            CssLink.main(args);
            assertNotEquals(CssLink.read(Files.readString(output)).digest(), original.digest());
            args[5] = "retain";
            CssLink.main(args);
            assertFalse(CssLink.read(Files.readString(output)).optimized());
            Path parameters = directory.resolve("arguments");
            Files.writeString(parameters, String.join("\n", args));
            CssLink.main(new String[] {"@" + parameters});
            assertFalse(CssLink.read(Files.readString(output)).optimized());
            for (var invalid : List.of(
                    new String[] {},
                    new String[] {"--input"},
                    new String[] {"--unknown", "x"},
                    new String[] {"--link", "a", "--link", "b"},
                    new String[] {"--output", output.toString(), "--link", "a", "--mode", "bad"})) {
                expectThrows(IllegalArgumentException.class, () -> CssLink.main(invalid));
            }
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
