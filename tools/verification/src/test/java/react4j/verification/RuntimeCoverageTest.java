package react4j.verification;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.gson.JsonArray;
import com.sun.source.tree.MethodTree;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class RuntimeCoverageTest {
    private static final String ROOT = "tools/verification/src/test/java";
    private static final String SOURCE = ROOT + "/react4j/verification/fixture/MeasurementFixture.java";

    private static Map<String, LcovReport.Source> inventory(boolean execute) throws Exception {
        return inventory(execute, "react4j.verification.fixture.MeasurementFixture", SOURCE, "coverage.fixture.jar");
    }

    private static Map<String, LcovReport.Source> inventory(
            boolean execute, String className, String sourceName, String jarProperty) throws Exception {
        byte[] original;
        var nested = new ArrayList<byte[]>();
        try (var jar = new ZipFile(System.getProperty(jarProperty))) {
            for (var entry : jar.stream()
                    .filter(e -> e.getName().startsWith(className.replace('.', '/') + "$")
                            && e.getName().endsWith(".class"))
                    .toList()) {
                nested.add(jar.getInputStream(entry).readAllBytes());
            }
            original = jar.getInputStream(jar.getEntry(className.replace('.', '/') + ".class"))
                    .readAllBytes();
        }
        var runtime = new LoggerRuntime();
        var data = new RuntimeData();
        runtime.startup(data);
        try {
            byte[] instrumented = new Instrumenter(runtime).instrument(original, sourceName);
            var type = new ClassLoader(RuntimeCoverageTest.class.getClassLoader()) {
                Class<?> fixture() {
                    return defineClass(className, instrumented, 0, instrumented.length);
                }
            }.fixture();
            if (execute) {
                var method = type.getMethod("jvmOnly", int.class);
                assertEquals(method.invoke(null, 1), 1);
                assertEquals(method.invoke(null, -1), 1);
            }
            var execution = new ExecutionDataStore();
            data.collect(execution, new SessionInfoStore(), false);
            var coverage = new CoverageBuilder();
            var analyzer = new Analyzer(execution, coverage);
            analyzer.analyzeClass(original, sourceName);
            for (var bytes : nested) {
                analyzer.analyzeClass(bytes, sourceName);
            }
            var lines = new TreeMap<Integer, Long>();
            var branches = new TreeMap<LcovReport.Branch, Long>();
            var functions = new TreeMap<String, LcovReport.Function>();
            for (var file : coverage.getSourceFiles()) {
                for (int line = file.getFirstLine(); line <= file.getLastLine(); line++) {
                    var counter = file.getLine(line);
                    if (ICounter.EMPTY != counter.getStatus()) {
                        lines.put(line, counter.getInstructionCounter().getCoveredCount() > 0 ? 1L : 0L);
                    }
                    var branch = counter.getBranchCounter();
                    for (int edge = 0; edge < branch.getTotalCount(); edge++) {
                        branches.put(new LcovReport.Branch(line, 0, edge), edge < branch.getCoveredCount() ? 1L : 0L);
                    }
                }
            }
            coverage.getClasses()
                    .forEach(c -> c.getMethods()
                            .forEach(m -> functions.put(
                                    c.getName() + "::" + m.getName() + " " + m.getDesc(),
                                    new LcovReport.Function(
                                            Math.max(0, m.getFirstLine()),
                                            m.getInstructionCounter().getCoveredCount() > 0 ? 1L : 0L))));
            return Map.of(sourceName, new LcovReport.Source(lines, branches, functions));
        } finally {
            runtime.shutdown();
        }
    }

    private static Path modules() {
        return Path.of(System.getProperty("coverage.fixture.modules"));
    }

    @Test
    public void actualJ2clDenominatorAndExecution() throws Exception {
        var inventory = inventory(true);
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
                "[17,24,42,\"throw\",5,17,7,\"assert\",5,0,6,\"void throw\"]");
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
        // The same browser evidence cannot hide unexecuted JVM-only bodies.
        var unexecuted = new RuntimeCoverage(
                bundle,
                inventory(false),
                Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                List.of(modules()),
                Set.of(SOURCE));
        String missingJvmHits =
                unexecuted.report(instrumented, counters.getAsJsonArray("lines"), counters.getAsJsonArray("edges"));
        var missing = Objects.requireNonNull(LcovReport.read(missingJvmHits).get(SOURCE));
        assertCovered(missing, source.text, "return value >", false);
        assertTrue(missing.lines().values().stream().filter(n -> n > 0).count()
                < observed.lines().values().stream().filter(n -> n > 0).count());
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
    public void completeBrowserCannotHideMissingJvmExecution() throws Exception {
        String sourceName = ROOT + "/react4j/verification/mixed/MixedPlatformFixture.java";
        String bundle = Files.readString(Path.of(System.getProperty("coverage.fixture.bundle")));
        var sourceJars = Map.of(ROOT, Path.of(System.getProperty("coverage.mixed.sources")));
        var trees = List.of(Path.of(System.getProperty("coverage.mixed.modules")));
        var measured =
                inventory(true, "react4j.verification.mixed.MixedPlatformFixture", sourceName, "coverage.mixed.jar");
        var collector = new RuntimeCoverage(bundle, measured, sourceJars, trees, Set.of(sourceName));
        var instrumented = CoverageInstrumenter.instrument(bundle, collector);
        var result = BrowserCoverage.run(
                Path.of(System.getProperty("react4j.browser.metadata")),
                Map.of(
                        "/index.html",
                        "<script src='/fixture.js'></script>",
                        "/fixture.js",
                        instrumented.javascript() + "\nwindow.runCoverageFixture();window.fixtureDone=true;"),
                "window.fixtureDone",
                "window['" + instrumented.variable() + "']");
        var lines = result.getAsJsonArray("lines");
        var edges = result.getAsJsonArray("edges");
        String config = "{\"roots\":[{\"path\":\"" + ROOT
                + "/react4j/verification/mixed\",\"report\":\"runtime.lcov\",\"lines\":95,\"branches\":85}]}";
        String complete = collector.report(instrumented, lines, edges);
        CoverageCheck.summarize(Map.of("runtime.lcov", complete), config);
        var unexecuted = new RuntimeCoverage(
                bundle,
                inventory(false, "react4j.verification.mixed.MixedPlatformFixture", sourceName, "coverage.mixed.jar"),
                sourceJars,
                trees,
                Set.of(sourceName));
        String incomplete = unexecuted.report(instrumented, lines, edges);
        assertThrows(
                IllegalArgumentException.class,
                () -> CoverageCheck.summarize(Map.of("runtime.lcov", incomplete), config));
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuntimeCoverage(bundle, Map.of(), sourceJars, trees, Set.of(sourceName)));
    }

    @Test
    public void actualReactMultilineMapHolesRemainIndependentObligations() throws Exception {
        var declared = Set.copyOf(Files.readAllLines(Path.of(System.getProperty("coverage.runtime.sources"))));
        var inventory = new TreeMap<String, LcovReport.Source>();
        for (String root : List.of("core", "dom")) {
            var builder = new CoverageBuilder();
            new Analyzer(new ExecutionDataStore(), builder)
                    .analyzeAll(Path.of(System.getProperty("coverage.runtime." + root + ".jar"))
                            .toFile());
            for (var source : builder.getSourceFiles()) {
                String name = root + "/src/main/java/" + source.getPackageName() + "/" + source.getName();
                if (!declared.contains(name)) {
                    continue;
                }
                var lines = new TreeMap<Integer, Long>();
                var branches = new TreeMap<LcovReport.Branch, Long>();
                var functions = new TreeMap<String, LcovReport.Function>();
                for (int line = Math.max(1, source.getFirstLine()); line <= source.getLastLine(); line++) {
                    var counter = source.getLine(line);
                    if (ICounter.EMPTY != counter.getStatus()) {
                        lines.put(line, 0L);
                    }
                    for (int edge = 0; edge < counter.getBranchCounter().getTotalCount(); edge++) {
                        branches.put(new LcovReport.Branch(line, 0, edge), 0L);
                    }
                }
                builder.getClasses().stream()
                        .filter(c -> c.getPackageName().equals(source.getPackageName())
                                && source.getName().equals(c.getSourceFileName()))
                        .forEach(c -> c.getMethods()
                                .forEach(m -> functions.put(
                                        c.getName() + "::" + m.getName() + " " + m.getDesc(),
                                        new LcovReport.Function(Math.max(0, m.getFirstLine()), 0))));
                inventory.put(name, new LcovReport.Source(lines, branches, functions));
            }
        }
        var core = Path.of(System.getProperty("coverage.runtime.core.modules"));
        var original = new SourceMapConsumerV3();
        original.parse(Files.readString(core.resolve("react4j/ReactElement$$Overlay.js.map")));
        var mappedLines = new HashSet<Integer>();
        original.visitMappings((file, symbol, start, generated, end) -> mappedLines.add(start.getLine() + 1));
        assertFalse(mappedLines.contains(120));
        assertFalse(mappedLines.contains(136));
        String element = "core/src/main/java/react4j/ReactElement.java";
        var obligations = Objects.requireNonNull(inventory.get(element));
        assertTrue(obligations.lines().containsKey(120));
        assertTrue(obligations.lines().containsKey(136));
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
        assertEquals(observed.lines().get(120), Long.valueOf(1));
        assertEquals(observed.lines().get(136), Long.valueOf(1));
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
    public void wholeJvmTypeDoesNotRequireBrowserModule() throws Exception {
        String name = ROOT + "/react4j/verification/fixture/JvmOnlyFixture.java";
        var inventory = inventory(true, "react4j.verification.fixture.JvmOnlyFixture", name, "coverage.fixture.jar");
        String bundle = Files.readString(Path.of(System.getProperty("coverage.fixture.bundle")));
        var collector = new RuntimeCoverage(
                bundle,
                inventory,
                Map.of(ROOT, Path.of(System.getProperty("coverage.fixture.sources"))),
                List.of(modules()),
                Set.of(name));
        var instrumented = CoverageInstrumenter.instrument(bundle, collector);
        assertTrue(instrumented.lines().isEmpty());
        assertTrue(instrumented.edges().isEmpty());
        var observed =
                Objects.requireNonNull(LcovReport.read(collector.report(instrumented, new JsonArray(), new JsonArray()))
                        .get(name));
        assertEquals(
                observed.lines(), Objects.requireNonNull(inventory.get(name)).lines());
        assertEquals(
                observed.branches(), Objects.requireNonNull(inventory.get(name)).branches());
        assertTrue(observed.lines().values().stream().allMatch(hits -> hits > 0));
    }

    @Test
    public void corruptOrAbsentMapsAndInventoriesFail() throws Exception {
        var inventory = inventory(true);
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
