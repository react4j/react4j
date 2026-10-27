package react4j.verification;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.gson.JsonArray;
import com.sun.source.tree.MethodTree;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class RuntimeCoverageTest {
    private static final String ROOT = "tools/verification/src/test/java";
    private static final String SOURCE = ROOT + "/react4j/verification/fixture/MeasurementFixture.java";

    private static Map<String, LcovReport.Source> inventory() throws Exception {
        return JavaInventory.read(Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.jar"))), Set.of(SOURCE));
    }

    @Test
    public void archiveInventoryContainsNoExecutionEvidence() throws Exception {
        var source = Objects.requireNonNull(inventory().get(SOURCE));
        assertFalse(source.lines().isEmpty());
        assertTrue(source.lines().values().stream().allMatch(hits -> 0 == hits));
        assertFalse(source.functions().isEmpty());
        assertTrue(source.functions().values().stream().allMatch(function -> 0 == function.hits()));
        assertTrue(source.branches().isEmpty());
    }

    private static Path modules() {
        return Path.of(System.getProperty("coverage.fixture.modules"));
    }

    @Test
    public void actualJ2clDenominatorAndExecution() throws Exception {
        var inventory = inventory();
        String bundle = Files.readString(Path.of(System.getProperty("coverage.fixture.bundle")));
        var collector = new RuntimeCoverage(
                bundle,
                inventory,
                Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                List.of(modules()),
                Set.of(SOURCE));
        var instrumented = CoverageInstrumenter.instrument(bundle, collector);
        String script = instrumented.javascript()
                + "\nwindow.fixtureValues=window.runCoverageFixture();window.fixtureDone=true;";
        var result = BrowserCoverage.run(
                Path.of(System.getProperty("react4j.browser.metadata")),
                Map.of(
                        "/index.html",
                        "<script>window.fixtureError=null;window.addEventListener('error',e=>window.fixtureError=String(e.error||e.message));</script><script"
                            + " src='/fixture.js'></script>",
                        "/fixture.js",
                        script),
                "window.fixtureError||window.fixtureDone",
                "({error:window.fixtureError,values:window.fixtureValues,counters:window['" + instrumented.variable()
                        + "']})");
        assertTrue(result.get("error").isJsonNull(), result.toString());
        assertEquals(
                result.getAsJsonArray("values").toString(),
                "[17,24,42,\"throw\",5,7,\"assert\",5,0,6,\"void throw\",79,3,89]");
        var counters = result.getAsJsonObject("counters");
        String report =
                collector.report(instrumented, counters.getAsJsonArray("lines"), counters.getAsJsonArray("edges"));
        var observed = Objects.requireNonNull(LcovReport.read(report).get(SOURCE));
        assertEquals(
                observed.lines().keySet(),
                Objects.requireNonNull(inventory.get(SOURCE)).lines().keySet());
        var source = new JavaCoverage(
                SOURCE,
                Files.readString(modules().resolve("react4j/verification/fixture/MeasurementFixture.java")),
                Objects.requireNonNull(inventory.get(SOURCE)));
        // Independent authored tokens identify skipped, throwing and deferred lines.
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"11\")", true);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"13\")", true);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"37\")", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"41\")", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"43\")", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"47\")", false);
        assertCovered(observed, source.text, "return 53;", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"59\")", false);
        assertTrue(observed.branches().values().contains(0L));
        int assertionLine = (int) source.text
                        .substring(0, source.text.indexOf("assert valid;"))
                        .chars()
                        .filter(c -> '\n' == c)
                        .count()
                + 1;
        var assertionEdges = observed.branches().entrySet().stream()
                .filter(entry -> entry.getKey().line() == assertionLine)
                .toList();
        assertEquals(assertionEdges.size(), 2);
        assertTrue(assertionEdges.stream().allMatch(entry -> entry.getValue() > 0));
        assertBranches(observed, source.text, "int result = value > 0", 2, 1);
        assertBranches(observed, source.text, "if (value > 0 &&", 6, 3);
        assertBranches(observed, source.text, "for (int i =", 2, 1);
        assertBranches(observed, source.text, "while (value > 0)", 2, 2);
        assertBranches(observed, source.text, "do {", 2, 1);
        assertBranches(observed, source.text, "switch (value)", 3, 2);
        assertBranches(observed, source.text, "for (int value :", 2, 2);
        assertBranches(observed, source.text, "return (first ?", 4, 0);
        assertBranches(observed, source.text, "if (!(value instanceof Double))", 2, 1);
        assertBranches(observed, source.text, "return switch (value)", 4, 3);
        assertCovered(observed, source.text, "case String text", true);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"83\")", false);
        assertMethodEnd(observed, source, "completedVoid", true);
        assertMethodEnd(observed, source, "throwingVoid", false);
        assertMethodEnd(observed, source, "deferredVoid", true);
        assertCovered(observed, source.text, "// deferred void callback end", false);
        assertCovered(observed, source.text, "value = 73;", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"67\")", false);
        assertCovered(
                observed, source.text, "parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(\"71\")", false);
        String config =
                "{\"roots\":[{\"path\":\"" + ROOT + "\",\"report\":\"runtime.lcov\",\"lines\":95,\"branches\":85}]}";
        assertThrows(
                IllegalArgumentException.class, () -> CoverageCheck.summarize(Map.of("runtime.lcov", report), config));
        var absent = new HashMap<>(inventory);
        var original = Objects.requireNonNull(inventory.get(SOURCE));
        absent.put(SOURCE, new LcovReport.Source(original.lines(), original.branches(), Map.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuntimeCoverage(
                        bundle,
                        absent,
                        Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                        List.of(modules()),
                        Set.of(SOURCE)));
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuntimeCoverage(
                        bundle,
                        inventory,
                        Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                        List.of(),
                        Set.of(SOURCE)));
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuntimeCoverage(
                        "",
                        inventory,
                        Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                        List.of(modules()),
                        Set.of(SOURCE)));
        assertThrows(
                IllegalArgumentException.class,
                () -> collector.report(instrumented, new JsonArray(), counters.getAsJsonArray("edges")));
    }

    @Test
    public void actualReactMultilineMapHolesRemainIndependentObligations() throws Exception {
        var declared = Set.copyOf(Files.readAllLines(Path.of(System.getProperty("coverage.runtime.sources"))));
        var inventory = JavaInventory.read(
                Map.of(
                        "core/src/main/java", Path.of(System.getProperty("coverage.runtime.core.jar")),
                        "dom/src/main/java", Path.of(System.getProperty("coverage.runtime.dom.jar"))),
                declared);
        var core = Path.of(System.getProperty("coverage.runtime.core.modules"));
        var original = new SourceMapConsumerV3();
        original.parse(Files.readString(core.resolve("react4j/ReactElement$$Overlay.js.map")));
        final String source;
        try (var archive = new ZipFile(System.getProperty("coverage.runtime.core.sources"))) {
            source = new String(
                    archive.getInputStream(Objects.requireNonNull(archive.getEntry("react4j/ReactElement.java")))
                            .readAllBytes(),
                    StandardCharsets.UTF_8);
        }
        final var strictMethod = source.indexOf("public static ReactNode createStrictMode(");
        assertTrue(0 <= strictMethod, "Missing strict-mode factory");
        final var strictLine =
                sourceLine(source, source.indexOf("Js.uncheckedCast(JsPropertyMap.of(\"children\"", strictMethod));
        final var suspenseLine = sourceLine(source, source.indexOf("\"ms\", maxTimeToFallback"));
        var mappedLines = new HashSet<Integer>();
        original.visitMappings((file, symbol, start, generated, end) -> mappedLines.add(start.getLine() + 1));
        assertFalse(mappedLines.contains(strictLine));
        assertFalse(mappedLines.contains(suspenseLine));
        String element = "core/src/main/java/react4j/ReactElement.java";
        var obligations = Objects.requireNonNull(inventory.get(element));
        assertTrue(obligations.lines().containsKey(strictLine));
        assertTrue(obligations.lines().containsKey(suspenseLine));
        String bundle = Files.readString(Path.of(System.getProperty("coverage.runtime.bundle")));
        var collector = new RuntimeCoverage(
                bundle,
                inventory,
                Map.of(
                        "core/src/main/java",
                        Path.of(System.getProperty("coverage.runtime.core.sources")),
                        "dom/src/main/java",
                        Path.of(System.getProperty("coverage.runtime.dom.sources"))),
                List.of(core, Path.of(System.getProperty("coverage.runtime.dom.modules"))),
                declared);
        var instrumented = CoverageInstrumenter.instrument(bundle, collector);
        var result = BrowserCoverage.run(
                Path.of(System.getProperty("react4j.browser.metadata")),
                Map.of(
                        "/index.html",
                        "<script src='/react.js'></script><script src='/react-dom.js'></script><script"
                                + " src='/runtime.js'></script>",
                        "/runtime.js",
                        instrumented.javascript() + "\nwindow.runRuntimeMapProbe();window.coverageDone=true;",
                        "/react.js",
                        Files.readString(Path.of(System.getProperty("coverage.runtime.react"))),
                        "/react-dom.js",
                        Files.readString(Path.of(System.getProperty("coverage.runtime.react-dom")))),
                "window.coverageDone",
                "window['" + instrumented.variable() + "']");
        var report = LcovReport.read(
                collector.report(instrumented, result.getAsJsonArray("lines"), result.getAsJsonArray("edges")));
        var observed = Objects.requireNonNull(report.get(element));
        assertEquals(observed.lines().keySet(), obligations.lines().keySet());
        assertEquals(observed.lines().get(strictLine), Long.valueOf(1));
        assertEquals(observed.lines().get(suspenseLine), Long.valueOf(1));
        var zeroLines = new JsonArray();
        instrumented.lines().forEach(unused -> zeroLines.add(false));
        var zeroEdges = new JsonArray();
        instrumented.edges().forEach(unused -> zeroEdges.add(false));
        var zero = Objects.requireNonNull(LcovReport.read(collector.report(instrumented, zeroLines, zeroEdges))
                .get(element));
        assertEquals(zero.lines().keySet(), obligations.lines().keySet());
        assertTrue(zero.lines().values().stream().allMatch(hits -> 0 == hits));
    }

    @Test
    public void corruptOrAbsentMapsAndInventoriesFail() throws Exception {
        var inventory = inventory();
        String bundle = Files.readString(Path.of(System.getProperty("coverage.fixture.bundle")));
        var jars = Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources")));
        var temporary = Files.createTempDirectory(Path.of(System.getenv("TEST_TMPDIR")), "coverage-maps-");
        try {
            try (var files = Files.walk(modules())) {
                for (var file : files.filter(Files::isRegularFile).toList()) {
                    var destination = temporary.resolve(modules().relativize(file));
                    Files.createDirectories(destination.getParent());
                    Files.copy(file, destination);
                }
            }
            var map = temporary.resolve("react4j/verification/fixture/MeasurementFixture.js.map");
            String original = Files.readString(map);
            Files.delete(map);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new RuntimeCoverage(bundle, inventory, jars, List.of(temporary), Set.of(SOURCE)));
            Files.writeString(map, "{broken");
            assertThrows(
                    SourceMapParseException.class,
                    () -> new RuntimeCoverage(bundle, inventory, jars, List.of(temporary), Set.of(SOURCE)));
            Files.writeString(map, original.replace("MeasurementFixture.java", "Missing.java"));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new RuntimeCoverage(bundle, inventory, jars, List.of(temporary), Set.of(SOURCE)));
            Files.writeString(map, original);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new RuntimeCoverage(bundle, Map.of(), jars, List.of(temporary), Set.of(SOURCE)));
            var originalInventory = Objects.requireNonNull(inventory.get(SOURCE));
            var missingMethodLines = new TreeMap<>(originalInventory.lines());
            originalInventory.functions().values().forEach(function -> missingMethodLines.remove(function.line()));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new RuntimeCoverage(
                            bundle,
                            Map.of(
                                    SOURCE,
                                    new LcovReport.Source(
                                            missingMethodLines,
                                            originalInventory.branches(),
                                            originalInventory.functions())),
                            jars,
                            List.of(temporary),
                            Set.of(SOURCE)));
        } finally {
            try (var files = Files.walk(temporary)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }

    private static int sourceLine(final String source, final int offset) {
        assertTrue(0 <= offset, "Missing source expression");
        return (int) source.substring(0, offset).chars().filter(c -> '\n' == c).count() + 1;
    }

    private static void assertMethodEnd(LcovReport.Source report, JavaCoverage source, String name, boolean covered) {
        int line = source.methods.stream()
                .filter(range -> range.tree() instanceof MethodTree method
                        && method.getName().contentEquals(name))
                .findFirst()
                .orElseThrow()
                .lastLine();
        assertTrue(report.lines().containsKey(line), "Missing executable method end: " + name);
        assertEquals(Objects.requireNonNull(report.lines().get(line)) > 0, covered, name);
    }

    private static void assertBranches(
            LcovReport.Source report, String source, String token, int expected, long covered) {
        int offset = source.indexOf(token);
        assertTrue(offset >= 0, token);
        int line =
                (int) source.substring(0, offset).chars().filter(c -> '\n' == c).count() + 1;
        var edges = report.branches().entrySet().stream()
                .filter(entry -> entry.getKey().line() == line)
                .toList();
        assertEquals(edges.size(), expected, token);
        assertEquals(edges.stream().filter(entry -> entry.getValue() > 0).count(), covered, token);
    }

    private static void assertCovered(LcovReport.Source report, String source, String token, boolean covered) {
        int offset = source.indexOf(token);
        assertTrue(offset >= 0, token);
        int line =
                (int) source.substring(0, offset).chars().filter(c -> '\n' == c).count() + 1;
        assertTrue(report.lines().containsKey(line), "Missing independent executable obligation: " + token);
        assertEquals(Objects.requireNonNull(report.lines().get(line)) > 0, covered, token + " at " + line);
    }
}
