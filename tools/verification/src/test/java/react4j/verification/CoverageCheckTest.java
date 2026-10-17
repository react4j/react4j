package react4j.verification;

import static org.testng.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class CoverageCheckTest {
    private static final String ROOT = "module/src/main/java";
    private static final String SOURCE = ROOT + "/demo/Example.java";

    private static String record(String source, long lines, long hits, long branches, long branchHits) {
        return "SF:" + source + "\nLF:" + lines + "\nLH:" + hits + "\nBRF:" + branches + "\nBRH:" + branchHits
                + "\nend_of_record\n";
    }

    private static String rule(String path, String lines, String branches) {
        return "{\"path\":\"" + path + "\",\"lines\":" + lines + ",\"branches\":" + branches + "}";
    }

    private static String config(String lines, String branches, String... classes) {
        return "{\"roots\":["
                + rule(ROOT, lines, branches).replace("}", ",\"classes\":[" + String.join(",", classes) + "]}")
                + "]}";
    }

    private static IllegalArgumentException failure(String report, String config, String message) {
        final var error = expectThrows(IllegalArgumentException.class, () -> CoverageCheck.summarize(report, config));
        assertTrue(String.valueOf(error.getMessage()).contains(message), error.getMessage());
        return error;
    }

    @Test
    public void aggregatesRootsAndChecksClassRulesSeparately() {
        final var policy = config("50", "40", rule("demo/Example.java", "95", "85"));
        final var report = record(SOURCE, 100, 95, 100, 85)
                + record(ROOT + "/demo/Other.java", 100, 5, 100, 0)
                + record("unowned/Other.java", 100, 0, 0, 0)
                + record("unowned/Other.java", 100, 0, 0, 0)
                + record(ROOT + "s/demo/Other.java", 100, 0, 0, 0);
        assertEquals(
                CoverageCheck.summarize(report, policy),
                ROOT + ": 2 sources, 100/200 lines, 85/200 branches\n" + SOURCE
                        + ": 1 sources, 95/100 lines, 85/100 branches\n");
        failure(report.replace("LH:5\n", "LH:4\n"), policy, ROOT);
        failure(report.replace("BRH:85\n", "BRH:79\n"), policy, ROOT);
        failure(record(SOURCE, 100, 94, 100, 85) + record(ROOT + "/Other.java", 100, 100, 100, 100), policy, SOURCE);
        failure(record(SOURCE, 100, 95, 100, 84) + record(ROOT + "/Other.java", 100, 100, 100, 100), policy, SOURCE);
    }

    @Test
    public void rootsFollowConfiguredOrderAndAcceptReorderedRuleFields() {
        final var policy = """
            {"roots":[
              {"branches":0,"lines":0,"path":"second"},
              {"classes":[],"path":"first","branches":0,"lines":0}
            ]}
            """;
        assertEquals(
                CoverageCheck.summarize(
                        record("first/A.java", 10, 2, 0, 0) + record("second/A.java", 10, 1, 0, 0), policy),
                "second: 1 sources, 1/10 lines, 0/0 branches\nfirst: 1 sources, 2/10 lines, 0/0 branches\n");
    }

    @Test
    public void relativeSourcePathsMayContainUnicodeAndSpaces() {
        final var root = "build sources/源代码";
        final var source = root + "/例/Éxample.java";
        final var policy = "{\"roots\":["
                + rule(root, "0", "0").replace("}", ",\"classes\":[" + rule("例/Éxample.java", "95", "85") + "]}")
                + "]}";
        assertEquals(
                CoverageCheck.summarize(record(source, 100, 95, 100, 85), policy),
                root + ": 1 sources, 95/100 lines, 85/100 branches\n" + source
                        + ": 1 sources, 95/100 lines, 85/100 branches\n");
    }

    @Test
    public void exactAndFractionalPercentageBoundariesAreNotRounded() {
        final var report = record(SOURCE, 1000, 951, 1000, 851);
        for (final var policy : List.of(config("95.1", "85.1"), config("9.51e1", "8.51e1"))) {
            assertTrue(CoverageCheck.summarize(report, policy).contains("951/1000 lines"));
        }
        failure(report, config("95.100000000000000001", "85.1"), "95.100000000000000001% lines");
        failure(report, config("95.1", "85.100000000000000001"), "85.100000000000000001% branches");
        assertTrue(CoverageCheck.summarize(report, config("0", "0", rule("demo/Example.java", "95.1", "85.1")))
                .contains("951/1000 lines"));
        failure(report, config("0", "0", rule("demo/Example.java", "95.100000000000000001", "85.1")), SOURCE);
        failure(report, config("0", "0", rule("demo/Example.java", "95.1", "85.100000000000000001")), SOURCE);
        assertTrue(CoverageCheck.summarize(
                        record(SOURCE, 3, 2, 3, 2), config("66.6666666666666666", "66.6666666666666666"))
                .contains("2/3 lines"));
        failure(record(SOURCE, 3, 2, 3, 2), config("66.6666666666666667", "0"), "Coverage must reach");
        final var large = record(SOURCE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
        assertTrue(
                CoverageCheck.summarize(large, config("100", "100")).contains(Long.MAX_VALUE + "/" + Long.MAX_VALUE));
    }

    @Test
    public void zeroBranchPolicyAllowsBranchlessSourcesAndPositivePolicyRequiresBranches() {
        final var report = record(SOURCE, 10, 1, 0, 0);
        assertTrue(CoverageCheck.summarize(report, config("0", "0")).contains("0/0 branches"));
        assertTrue(CoverageCheck.summarize(report.replace("BRF:0\nBRH:0\n", ""), config("0", "0"))
                .contains("0/0 branches"));
        failure(report, config("0", "0.1"), "Coverage must reach");
        failure(report.replace("BRF:0\nBRH:0\n", ""), config("0", "0.1"), "Coverage must reach");
        failure(report, config("0", "0", rule("demo/Example.java", "0", "0.1")), SOURCE);
    }

    @Test
    public void configuredRootsAndClassesMustHaveNonemptyCoveredLines() {
        for (final var report : List.of(
                "",
                record("other/Example.java", 10, 10, 0, 0),
                record(SOURCE, 0, 0, 0, 0),
                record(SOURCE, 10, 0, 0, 0))) {
            failure(report, config("0", "0"), "Missing or empty owned JVM coverage for " + ROOT);
        }
        final var policy = config("0", "0", rule("demo/Example.java", "0", "0"));
        for (final var report : List.of("", record(SOURCE, 0, 0, 0, 0), record(SOURCE, 10, 0, 0, 0))) {
            failure(record(ROOT + "/Other.java", 10, 10, 0, 0) + report, policy, SOURCE);
        }
    }

    @DataProvider
    public Object[][] malformedConfigurations() {
        return List.of(
                        "",
                        "[]",
                        "null",
                        "{}",
                        "{\"roots\":[]}",
                        "{\"roots\":null}",
                        "{\"roots\":[null]}",
                        "{\"roots\":[{}]}",
                        "{\"roots\":[\"module\"]}",
                        "{\"roots\":[{\"path\":\"a\",\"lines\":0}]}",
                        "{\"roots\":[{\"path\":\"a\",\"branches\":0}]}",
                        "{\"roots\":[{\"lines\":0,\"branches\":0}]}",
                        "{\"roots\":[{\"path\":1,\"lines\":0,\"branches\":0}]}",
                        "{\"roots\":[{\"path\":\"a\",\"path\":\"b\",\"lines\":0,\"branches\":0}]}",
                        "{\"roots\":[{\"path\":\"a\",\"lines\":0,\"lines\":1,\"branches\":0}]}",
                        "{\"roots\":[{\"path\":\"a\",\"lines\":0,\"branches\":0,\"linez\":0}]}",
                        config("0", "0").replace("\"classes\":[]", "\"classes\":null"),
                        config("0", "0").replace("\"classes\":[]", "\"classes\":{}"),
                        config("0", "0", "null"),
                        config("0", "0", rule("Example.java", "0", "0").replace("}", ",\"classes\":[]} ")),
                        config("0", "0").replace("\"roots\":", "\"unknown\":"),
                        config("0", "0").replace("]}", "],\"other\":true}"),
                        config("0", "0").replace("]}", "],\"roots\":[]}"),
                        config("0", "0") + " {}",
                        config("0", "0").replace("\"roots\"", "roots"),
                        config("0", "0").replace("\"roots\"", "'roots'"),
                        "//comment\n" + config("0", "0"),
                        config("0", "0").replace("\"classes\":[]", "\"classes\":[],"))
                .stream()
                .map(value -> new Object[] {value})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "malformedConfigurations")
    public void rejectsMalformedConfiguration(String policy) {
        expectThrows(
                IllegalArgumentException.class, () -> CoverageCheck.summarize(record(SOURCE, 10, 10, 0, 0), policy));
    }

    @Test
    public void rejectsInvalidPercentagesAtEveryRuleLevel() {
        for (final var number :
                List.of("-0.1", "100.00001", "1e100", "\"95\"", "true", "null", "[]", "{}", "NaN", "Infinity")) {
            for (final var policy : List.of(
                    config(number, "0"),
                    config("0", number),
                    config("0", "0", rule("Example.java", number, "0")),
                    config("0", "0", rule("Example.java", "0", number)))) {
                expectThrows(IllegalArgumentException.class, () -> CoverageCheck.summarize("", policy));
            }
        }
    }

    @Test
    public void rejectsUnsafePathsAndDuplicateOrOverlappingRules() {
        for (final var path : List.of(
                "",
                "/src",
                "C:/src",
                "C:src",
                "../src",
                "src/../main",
                "src/./main",
                "src//main",
                "src/",
                "src\\\\main",
                "src\\u0001main",
                ".",
                "..")) {
            final var policy = "{\"roots\":[" + rule(path, "0", "0") + "]}";
            failure("", policy, "Invalid relative coverage path");
            failure("", config("0", "0", rule(path + "/Example.java", "0", "0")), "Invalid relative coverage path");
        }
        failure("", config("0", "0", rule("demo/Example", "0", "0")), "Invalid relative coverage path");
        for (final var other : List.of(ROOT, ROOT + "/demo", "module")) {
            final var policy = "{\"roots\":[" + rule(ROOT, "0", "0") + "," + rule(other, "0", "0") + "]}";
            failure("", policy, "Duplicate or overlapping coverage roots");
        }
        failure(
                "",
                config("0", "0", rule("Example.java", "0", "0"), rule("Example.java", "1", "0")),
                "Duplicate or overlapping coverage classes");
        failure(
                "",
                config("0", "0", rule("Example.java", "0", "0"), rule("Example.java/Nested.java", "1", "0")),
                "Duplicate or overlapping coverage classes");
        failure("", config("50", "40", rule("Example.java", "49", "40")), "must not weaken its root");
        failure("", config("50", "40", rule("Example.java", "50", "39")), "must not weaken its root");
    }

    @Test
    public void rejectsDuplicateSourcesAndCounters() {
        final var report = record(SOURCE, 10, 10, 10, 10);
        failure(report + report, config("0", "0"), "Duplicate coverage source");
        failure(report + report.replace('/', '\\'), config("0", "0"), "Duplicate coverage source");
        for (final var counter : List.of("SF:" + SOURCE, "LF:10", "LH:10", "BRF:10", "BRH:10")) {
            failure(
                    report.replace("end_of_record", counter + "\nend_of_record"),
                    config("0", "0"),
                    "Duplicate coverage counter");
        }
    }

    @Test
    public void rejectsMissingMalformedAndImpossibleCounters() {
        final var report = record(SOURCE, 10, 10, 10, 10);
        for (final var counter : List.of("LF", "LH", "BRF", "BRH")) {
            failure(
                    report.replace(counter + ":10\n", ""),
                    config("0", "0"),
                    "LF".equals(counter) || "LH".equals(counter) ? "Missing " + counter : "Incomplete branch");
            for (final var value : List.of("-1", "", "abc", "1.5", "9223372036854775808")) {
                failure(report.replace(counter + ":10", counter + ":" + value), config("0", "0"), "Invalid");
            }
        }
        failure(record(SOURCE, 10, 11, 10, 10), config("0", "0"), "Invalid coverage counts");
        failure(record(SOURCE, 10, 10, 10, 11), config("0", "0"), "Invalid coverage counts");
        expectThrows(
                ArithmeticException.class,
                () -> CoverageCheck.summarize(
                        record(SOURCE, Long.MAX_VALUE, 1, 0, 0) + record(ROOT + "/Other.java", 1, 1, 0, 0),
                        config("0", "0")));
    }

    @Test
    public void checkedInConfigurationIsValid() throws IOException {
        final var policy = Files.readString(Path.of("tools/verification/coverage.json"));
        failure("", policy, "Missing or empty owned JVM coverage");
    }

    @Test
    public void commandLineRequiresExplicitConfigurationAndDeletesStaleOutputOnFailure() throws IOException {
        final var directory = Files.createTempDirectory("coverage-check-test");
        try {
            final var policy = directory.resolve("coverage.json");
            final var report = directory.resolve("jvm.lcov");
            final var output = directory.resolve("nested/summary.txt");
            Files.writeString(policy, config("50", "0"));
            Files.writeString(report, record(SOURCE, 10, 5, 0, 0));
            CoverageCheck.main(new String[] {policy.toString(), report.toString(), output.toString()});
            assertEquals(Files.readString(output), ROOT + ": 1 sources, 5/10 lines, 0/0 branches\n");
            for (final var args :
                    List.of(new String[] {}, new String[] {report.toString(), output.toString()}, new String[] {
                        "a", "b", "c", "d"
                    })) {
                expectThrows(IllegalArgumentException.class, () -> CoverageCheck.main(args));
            }
            for (final var invalid : List.of("{}", config("60", "0"))) {
                Files.writeString(output, "stale");
                Files.writeString(policy, invalid);
                expectThrows(
                        IllegalArgumentException.class,
                        () -> CoverageCheck.main(
                                new String[] {policy.toString(), report.toString(), output.toString()}));
                assertFalse(Files.exists(output));
            }
            Files.writeString(policy, config("50", "0"));
            Files.writeString(report, "");
            Files.writeString(output, "stale");
            expectThrows(
                    IllegalArgumentException.class,
                    () -> CoverageCheck.main(new String[] {policy.toString(), report.toString(), output.toString()}));
            assertFalse(Files.exists(output));
            Files.writeString(output, "stale");
            Files.delete(report);
            expectThrows(
                    IOException.class,
                    () -> CoverageCheck.main(new String[] {policy.toString(), report.toString(), output.toString()}));
            assertFalse(Files.exists(output));
            Files.writeString(output, "stale");
            Files.writeString(report, record(SOURCE, 10, 5, 0, 0));
            Files.delete(policy);
            expectThrows(
                    IOException.class,
                    () -> CoverageCheck.main(new String[] {policy.toString(), report.toString(), output.toString()}));
            assertFalse(Files.exists(output));
        } finally {
            try (var files = Files.walk(directory)) {
                for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
