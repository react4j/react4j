package react4j.verification;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class LcovReport {
    record Branch(int line, int block, int edge) implements Comparable<Branch> {
        @Override
        public int compareTo(Branch other) {
            int result = Integer.compare(line, other.line);
            if (0 == result) {
                result = Integer.compare(block, other.block);
            }
            return 0 == result ? Integer.compare(edge, other.edge) : result;
        }
    }

    record Function(int line, long hits) {}

    record Source(Map<Integer, Long> lines, Map<Branch, Long> branches, Map<String, Function> functions) {}

    private LcovReport() {}

    static Map<String, Source> read(String text) {
        if (!text.isBlank() && !text.stripTrailing().endsWith("end_of_record")) {
            throw new IllegalArgumentException("Unterminated LCOV record");
        }
        var result = new TreeMap<String, Source>();
        for (var record : text.split("end_of_record", -1)) {
            if (record.isBlank()) {
                continue;
            }
            String source = null;
            var lines = new TreeMap<Integer, Long>();
            var branches = new TreeMap<Branch, Long>();
            var functions = new TreeMap<String, Integer>();
            var functionHits = new HashMap<String, Long>();
            var totals = new HashMap<String, Long>();
            for (var row : record.lines().toList()) {
                int separator = row.indexOf(':');
                if (separator < 0) {
                    if (!row.isBlank()) {
                        throw new IllegalArgumentException("Malformed LCOV row: " + row);
                    }
                    continue;
                }
                String kind = row.substring(0, separator);
                String value = row.substring(separator + 1);
                switch (kind) {
                    case "SF" -> {
                        if (null != source || value.isBlank()) {
                            throw new IllegalArgumentException("Missing or repeated LCOV source");
                        }
                        source = value.replace('\\', '/');
                    }
                    case "DA" -> {
                        var parts = value.split(",", -1);
                        if (2 > parts.length || 3 < parts.length) {
                            throw new IllegalArgumentException("Invalid LCOV line: " + row);
                        }
                        unique(lines, positive(parts[0]), count(parts[1]));
                    }
                    case "BRDA" -> {
                        var parts = value.split(",", -1);
                        if (4 != parts.length) {
                            throw new IllegalArgumentException("Invalid LCOV branch: " + row);
                        }
                        unique(
                                branches,
                                new Branch(positive(parts[0]), nonnegative(parts[1]), nonnegative(parts[2])),
                                "-".equals(parts[3]) ? 0L : count(parts[3]));
                    }
                    case "FN", "FNDA" -> {
                        var parts = value.split(",", 2);
                        if (2 != parts.length || parts[1].isEmpty()) {
                            throw new IllegalArgumentException("Invalid LCOV function: " + row);
                        }
                        if ("FN".equals(kind)) {
                            unique(functions, parts[1], nonnegative(parts[0]));
                        } else {
                            unique(functionHits, parts[1], count(parts[0]));
                        }
                    }
                    case "LF", "LH", "BRF", "BRH", "FNF", "FNH" -> unique(totals, kind, count(value));
                    default -> {
                        // LCOV test names and producer metadata do not describe execution obligations.
                    }
                }
            }
            if (null == source) {
                throw new IllegalArgumentException("Missing LCOV source");
            }
            if (!functions.keySet().equals(functionHits.keySet())) {
                throw new IllegalArgumentException("Missing LCOV function execution evidence: " + source);
            }
            total(totals, "LF", lines.size(), true);
            total(totals, "LH", lines.values().stream().filter(n -> n > 0).count(), true);
            total(totals, "BRF", branches.size(), !branches.isEmpty());
            total(totals, "BRH", branches.values().stream().filter(n -> n > 0).count(), !branches.isEmpty());
            total(totals, "FNF", functions.size(), !functions.isEmpty());
            total(
                    totals,
                    "FNH",
                    functionHits.values().stream().filter(n -> n > 0).count(),
                    !functions.isEmpty());
            var methods = new TreeMap<String, Function>();
            functions.forEach((name, line) ->
                    methods.put(name, new Function(line, Objects.requireNonNull(functionHits.get(name)))));
            unique(result, source, new Source(Map.copyOf(lines), Map.copyOf(branches), Map.copyOf(methods)));
        }
        return Map.copyOf(result);
    }

    static String write(Map<String, Source> sources) {
        var output = new StringBuilder();
        new TreeMap<>(sources).forEach((name, source) -> {
            output.append("SF:").append(name).append('\n');
            new TreeMap<>(source.functions())
                    .forEach((function, data) -> output.append("FN:")
                            .append(data.line())
                            .append(',')
                            .append(function)
                            .append('\n'));
            new TreeMap<>(source.functions())
                    .forEach((function, data) -> output.append("FNDA:")
                            .append(data.hits())
                            .append(',')
                            .append(function)
                            .append('\n'));
            output.append("FNF:").append(source.functions().size()).append('\n');
            output.append("FNH:")
                    .append(source.functions().values().stream()
                            .filter(f -> f.hits() > 0)
                            .count())
                    .append('\n');
            new TreeMap<>(source.lines())
                    .forEach((line, hits) -> output.append("DA:")
                            .append(line)
                            .append(',')
                            .append(hits)
                            .append('\n'));
            output.append("LF:").append(source.lines().size()).append('\n');
            output.append("LH:")
                    .append(source.lines().values().stream().filter(n -> n > 0).count())
                    .append('\n');
            new TreeMap<>(source.branches())
                    .forEach((branch, hits) -> output.append("BRDA:")
                            .append(branch.line())
                            .append(',')
                            .append(branch.block())
                            .append(',')
                            .append(branch.edge())
                            .append(',')
                            .append(hits)
                            .append('\n'));
            output.append("BRF:").append(source.branches().size()).append('\n');
            output.append("BRH:")
                    .append(source.branches().values().stream()
                            .filter(n -> n > 0)
                            .count())
                    .append('\n');
            output.append("end_of_record\n");
        });
        return output.toString();
    }

    private static void total(Map<String, Long> totals, String name, long expected, boolean required) {
        var actual = totals.get(name);
        if (null == actual ? required : actual != expected) {
            throw new IllegalArgumentException("LCOV " + name + " does not match its execution records");
        }
    }

    private static int positive(String text) {
        int value = nonnegative(text);
        if (0 == value) {
            throw new IllegalArgumentException("LCOV executable lines must be positive");
        }
        return value;
    }

    private static int nonnegative(String text) {
        return Math.toIntExact(count(text));
    }

    private static long count(String text) {
        long value = Long.parseLong(text);
        if (0 > value) {
            throw new IllegalArgumentException("LCOV counters must not be negative");
        }
        return value;
    }

    private static <K, V> void unique(Map<K, V> values, K key, V value) {
        if (null != values.putIfAbsent(key, value)) {
            throw new IllegalArgumentException("Duplicate LCOV entry: " + key);
        }
    }
}
