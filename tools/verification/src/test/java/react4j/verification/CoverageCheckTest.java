package react4j.verification;

import static org.testng.Assert.*;

import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CoverageCheckTest {
    private static final String CSS = "css/processor/src/main/java/react4j/css/processor/";

    private static String record(String source, int lines, int hits, int branches, int branchHits) {
        return "SF:" + source + "\nLF:" + lines + "\nLH:" + hits + "\nBRF:" + branches + "\nBRH:" + branchHits
                + "\nend_of_record\n";
    }

    private static String report(String textBlockRecord) {
        var result = new StringBuilder();
        for (var module : List.of("core", "dom", "processor")) {
            result.append(record(module + "/src/main/java/demo/Example.java", 10, 1, 2, 1));
        }
        result.append(record("css/internal/src/main/java/react4j/css/internal/SourceMap.java", 100, 100, 100, 100));
        result.append(record("css/bundler/src/main/java/react4j/css/bundler/Bundle.java", 100, 100, 100, 100));
        result.append(record(CSS + "Names.java", 12, 12, 16, 16));
        result.append(record(CSS + "CssCompiler.java", 176, 169, 120, 107));
        return result.append(textBlockRecord).toString();
    }

    @Test
    public void reportsModulesAndEnforcesTheCssUnitBoundary() {
        String summary = CoverageCheck.summarize(report(record(CSS + "TextBlock.java", 100, 95, 100, 85)));
        assertTrue(summary.contains("core: 1 sources, 1/10 lines, 1/2 branches\n"));
        assertTrue(summary.contains("css/processor: 3 sources, 276/288 lines, 208/236 branches\n"));
        assertTrue(summary.contains(CSS + "TextBlock.java: 1 sources, 95/100 lines, 85/100 branches\n"));
        for (var counters :
                List.of(new int[] {100, 94, 100, 85}, new int[] {100, 95, 100, 84}, new int[] {100, 100, 0, 0})) {
            var error = expectThrows(
                    IllegalArgumentException.class,
                    () -> CoverageCheck.summarize(report(
                            record(CSS + "TextBlock.java", counters[0], counters[1], counters[2], counters[3]))));
            assertTrue(String.valueOf(error.getMessage()).contains("CSS unit coverage must reach"), error.getMessage());
            assertTrue(String.valueOf(error.getMessage()).contains("TextBlock.java"), error.getMessage());
        }
    }

    @Test
    public void missingOrDuplicateUnitSourcesCannotPassUsingOtherCssCoverage() {
        for (var extra : List.of("", record(CSS + "CssProcessor.java", 100, 100, 100, 100))) {
            var error = expectThrows(IllegalArgumentException.class, () -> CoverageCheck.summarize(report(extra)));
            assertTrue(String.valueOf(error.getMessage()).contains("TextBlock.java"), error.getMessage());
        }
        String unit = record(CSS + "TextBlock.java", 100, 100, 100, 100);
        var duplicate =
                expectThrows(IllegalArgumentException.class, () -> CoverageCheck.summarize(report(unit + unit)));
        assertTrue(
                String.valueOf(duplicate.getMessage()).contains("Duplicate coverage source"), duplicate.getMessage());
    }
}
