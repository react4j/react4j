package react4j.verification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CoverageCheck {
    private CoverageCheck() {}

    public static void main(String[] args) throws IOException {
        if (2 != args.length) {
            throw new IllegalArgumentException("Expected LCOV report and summary output");
        }
        final var output = Path.of(args[1]);
        Files.deleteIfExists(output);
        final var summary = summarize(Files.readString(Path.of(args[0])));
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.writeString(output, summary);
        System.out.print(summary);
    }

    static String summarize(String report) {
        final var modules = new LinkedHashMap<String, Counts>();
        for (final var module : List.of("core", "dom", "processor", "css/processor", "css/internal", "css/bundler")) {
            modules.put(module, new Counts());
        }
        final var cssUnits = new LinkedHashMap<String, Counts>();
        for (final var name : List.of("Names", "TextBlock", "CssCompiler")) {
            cssUnits.put("css/processor/src/main/java/react4j/css/processor/" + name + ".java", new Counts());
        }
        cssUnits.put("css/internal/src/main/java/react4j/css/internal/SourceMap.java", new Counts());
        cssUnits.put("css/internal/src/main/java/react4j/css/internal/CssResource.java", new Counts());
        cssUnits.put("css/bundler/src/main/java/react4j/css/bundler/Bundle.java", new Counts());
        final var sources = new HashSet<String>();
        for (final var record : report.split("end_of_record")) {
            final var fields = new HashMap<String, String>();
            for (final var line : record.split("\\R")) {
                final var separator = line.indexOf(':');
                if (separator > 0 && Set.of("SF", "LF", "LH", "BRF", "BRH").contains(line.substring(0, separator))) {
                    if (null != fields.putIfAbsent(line.substring(0, separator), line.substring(separator + 1))) {
                        throw new IllegalArgumentException("Duplicate coverage counter");
                    }
                }
            }
            final var source = fields.getOrDefault("SF", "").replace('\\', '/');
            for (final var module : modules.entrySet()) {
                final var prefix = module.getKey() + "/src/main/java/";
                if (source.startsWith(prefix)) {
                    if (!sources.add(source)) {
                        throw new IllegalArgumentException("Duplicate coverage source: " + source);
                    }
                    module.getValue().add(fields, source);
                    final var unit = cssUnits.get(source);
                    if (null != unit) {
                        unit.add(fields, source);
                    }
                }
            }
        }
        final var result = new StringBuilder();
        for (final var entry : modules.entrySet()) {
            appendSummary(result, entry.getKey(), entry.getValue());
        }
        for (final var entry : cssUnits.entrySet()) {
            final var counts = entry.getValue();
            appendSummary(result, entry.getKey(), counts);
            if (counts.hits * 100 < counts.lines * 95
                    || counts.branchHits * 100 < counts.branches * 85
                    || 0 == counts.branches) {
                throw new IllegalArgumentException(
                        "CSS unit coverage must reach 95% lines and 85% branches: " + entry.getKey());
            }
        }
        return result.toString();
    }

    private static void appendSummary(StringBuilder result, String label, Counts counts) {
        if (0 == counts.files || 0 == counts.lines || 0 == counts.hits) {
            throw new IllegalArgumentException("Missing or empty owned JVM coverage for " + label);
        }
        result.append(label)
                .append(": ")
                .append(counts.files)
                .append(" sources, ")
                .append(counts.hits)
                .append('/')
                .append(counts.lines)
                .append(" lines, ")
                .append(counts.branchHits)
                .append('/')
                .append(counts.branches)
                .append(" branches\n");
    }

    private static final class Counts {
        private long files;
        private long lines;
        private long hits;
        private long branches;
        private long branchHits;

        private void add(Map<String, String> fields, String source) {
            final var found = counter(fields, "LF", source);
            final var hit = counter(fields, "LH", source);
            final var branch = Long.parseLong(fields.getOrDefault("BRF", "0"));
            final var branchHit = Long.parseLong(fields.getOrDefault("BRH", "0"));
            if (found < 0 || hit < 0 || hit > found || branch < 0 || branchHit < 0 || branchHit > branch) {
                throw new IllegalArgumentException("Invalid coverage counts for " + source);
            }
            files++;
            lines += found;
            hits += hit;
            branches += branch;
            branchHits += branchHit;
        }

        private static long counter(Map<String, String> fields, String key, String source) {
            final var value = fields.get(key);
            if (null == value) {
                throw new IllegalArgumentException("Missing " + key + " for " + source);
            }
            return Long.parseLong(value);
        }
    }
}
