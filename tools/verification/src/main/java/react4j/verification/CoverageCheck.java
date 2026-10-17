package react4j.verification;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CoverageCheck {
    private CoverageCheck() {}

    public static void main(String[] args) throws IOException {
        if (3 != args.length) {
            throw new IllegalArgumentException("Expected coverage config, LCOV report and summary output");
        }
        final var output = Path.of(args[2]);
        Files.deleteIfExists(output);
        final var summary = summarize(Files.readString(Path.of(args[1])), Files.readString(Path.of(args[0])));
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.writeString(output, summary);
        System.out.print(summary);
    }

    static String summarize(String report, String configuration) {
        final var roots = readConfiguration(configuration);
        final var totals = new LinkedHashMap<String, Counts>();
        for (final var root : roots) {
            totals.put(root.path(), new Counts());
            for (final var rule : root.classes()) {
                totals.put(root.path() + "/" + rule.path(), new Counts());
            }
        }
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
            for (final var root : roots) {
                if (source.startsWith(root.path() + "/")) {
                    if (!sources.add(source)) {
                        throw new IllegalArgumentException("Duplicate coverage source: " + source);
                    }
                    Objects.requireNonNull(totals.get(root.path())).add(fields, source);
                    final var unit = totals.get(source);
                    if (null != unit) {
                        unit.add(fields, source);
                    }
                }
            }
        }
        final var result = new StringBuilder();
        for (final var root : roots) {
            appendSummary(result, root.path(), root, Objects.requireNonNull(totals.get(root.path())));
            for (final var rule : root.classes()) {
                final var path = root.path() + "/" + rule.path();
                appendSummary(result, path, rule, Objects.requireNonNull(totals.get(path)));
            }
        }
        return result.toString();
    }

    private static List<Rule> readConfiguration(String configuration) {
        try (var reader = new JsonReader(new StringReader(configuration))) {
            reader.setLenient(false);
            reader.beginObject();
            if (!reader.hasNext() || !"roots".equals(reader.nextName())) {
                throw new IllegalArgumentException("Coverage config requires roots");
            }
            final var roots = new ArrayList<Rule>();
            reader.beginArray();
            while (reader.hasNext()) {
                final var root = readRule(reader, true);
                for (final var previous : roots) {
                    if (root.path().equals(previous.path())
                            || root.path().startsWith(previous.path() + "/")
                            || previous.path().startsWith(root.path() + "/")) {
                        throw new IllegalArgumentException("Duplicate or overlapping coverage roots: " + root.path());
                    }
                }
                roots.add(root);
            }
            reader.endArray();
            if (roots.isEmpty() || reader.hasNext()) {
                throw new IllegalArgumentException("Coverage config requires nonempty roots and no other fields");
            }
            reader.endObject();
            if (JsonToken.END_DOCUMENT != reader.peek()) {
                throw new IllegalArgumentException("Unexpected content after coverage config");
            }
            return roots;
        } catch (IOException | IllegalStateException | NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coverage config", e);
        }
    }

    private static Rule readRule(JsonReader reader, boolean root) throws IOException {
        String path = null;
        BigDecimal lines = null;
        BigDecimal branches = null;
        final var classes = new ArrayList<Rule>();
        final var fields = new HashSet<String>();
        reader.beginObject();
        while (reader.hasNext()) {
            final var field = reader.nextName();
            if (!fields.add(field)) {
                throw new IllegalArgumentException("Duplicate coverage config field: " + field);
            }
            switch (field) {
                case "path" -> {
                    if (JsonToken.STRING != reader.peek()) {
                        throw new IllegalArgumentException("Coverage path must be a string");
                    }
                    path = reader.nextString();
                }
                case "lines" -> lines = percentage(reader);
                case "branches" -> branches = percentage(reader);
                case "classes" -> {
                    if (!root) {
                        throw new IllegalArgumentException("Only coverage roots may declare classes");
                    }
                    reader.beginArray();
                    while (reader.hasNext()) {
                        final var rule = readRule(reader, false);
                        for (final var previous : classes) {
                            if (rule.path().equals(previous.path())
                                    || rule.path().startsWith(previous.path() + "/")
                                    || previous.path().startsWith(rule.path() + "/")) {
                                throw new IllegalArgumentException(
                                        "Duplicate or overlapping coverage classes: " + rule.path());
                            }
                        }
                        classes.add(rule);
                    }
                    reader.endArray();
                }
                default -> throw new IllegalArgumentException("Unknown coverage config field: " + field);
            }
        }
        reader.endObject();
        if (null == path || null == lines || null == branches) {
            throw new IllegalArgumentException("Coverage rules require path, lines and branches");
        }
        if (path.startsWith("/")
                || path.matches("^[A-Za-z]:.*")
                || path.chars().anyMatch(c -> '\\' == c || Character.isISOControl(c))
                || List.of(path.split("/", -1)).stream()
                        .anyMatch(segment -> segment.isEmpty() || ".".equals(segment) || "..".equals(segment))
                || !root && !path.endsWith(".java")) {
            throw new IllegalArgumentException("Invalid relative coverage path: " + path);
        }
        for (final var rule : classes) {
            if (rule.lines().compareTo(lines) < 0 || rule.branches().compareTo(branches) < 0) {
                throw new IllegalArgumentException("Class coverage minima must not weaken its root: " + rule.path());
            }
        }
        return new Rule(path, lines, branches, List.copyOf(classes));
    }

    private static BigDecimal percentage(JsonReader reader) throws IOException {
        if (JsonToken.NUMBER != reader.peek()) {
            throw new IllegalArgumentException("Coverage percentages must be numbers between 0 and 100");
        }
        final var value = new BigDecimal(reader.nextString());
        if (value.signum() < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Coverage percentages must be numbers between 0 and 100");
        }
        return value;
    }

    private static void appendSummary(StringBuilder result, String path, Rule rule, Counts counts) {
        if (0 == counts.files || 0 == counts.lines || 0 == counts.hits) {
            throw new IllegalArgumentException("Missing or empty owned JVM coverage for " + path);
        }
        if (belowMinimum(counts.hits, counts.lines, rule.lines())
                || belowMinimum(counts.branchHits, counts.branches, rule.branches())) {
            throw new IllegalArgumentException("Coverage must reach " + rule.lines() + "% lines and " + rule.branches()
                    + "% branches for " + path);
        }
        result.append(path)
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

    private static boolean belowMinimum(long hits, long total, BigDecimal minimum) {
        return minimum.signum() > 0
                && (0 == total
                        || BigDecimal.valueOf(hits)
                                        .multiply(BigDecimal.valueOf(100))
                                        .compareTo(BigDecimal.valueOf(total).multiply(minimum))
                                < 0);
    }

    private record Rule(String path, BigDecimal lines, BigDecimal branches, List<Rule> classes) {}

    private static final class Counts {
        private long files;
        private long lines;
        private long hits;
        private long branches;
        private long branchHits;

        private void add(Map<String, String> fields, String source) {
            final var found = counter(fields, "LF", source);
            final var hit = counter(fields, "LH", source);
            if (fields.containsKey("BRF") != fields.containsKey("BRH")) {
                throw new IllegalArgumentException("Incomplete branch coverage counters for " + source);
            }
            final var branch = fields.containsKey("BRF") ? counter(fields, "BRF", source) : 0;
            final var branchHit = fields.containsKey("BRH") ? counter(fields, "BRH", source) : 0;
            if (found < 0 || hit < 0 || hit > found || branch < 0 || branchHit < 0 || branchHit > branch) {
                throw new IllegalArgumentException("Invalid coverage counts for " + source);
            }
            files++;
            lines = Math.addExact(lines, found);
            hits = Math.addExact(hits, hit);
            branches = Math.addExact(branches, branch);
            branchHits = Math.addExact(branchHits, branchHit);
        }

        private static long counter(Map<String, String> fields, String key, String source) {
            final var value = fields.get(key);
            if (null == value) {
                throw new IllegalArgumentException("Missing " + key + " for " + source);
            }
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid " + key + " for " + source, e);
            }
        }
    }
}
