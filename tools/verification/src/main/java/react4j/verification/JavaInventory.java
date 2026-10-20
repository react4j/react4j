package react4j.verification;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class JavaInventory {
    private JavaInventory() {}

    static Map<String, LcovReport.Source> read(Map<String, Path> archives, Set<String> declared) throws IOException {
        var inventory = new TreeMap<String, LcovReport.Source>();
        for (var entry : archives.entrySet()) {
            var builder = new CoverageBuilder();
            new Analyzer(new ExecutionDataStore(), builder)
                    .analyzeAll(entry.getValue().toFile());
            for (var source : builder.getSourceFiles()) {
                String name = entry.getKey() + "/" + source.getPackageName() + "/" + source.getName();
                if (!declared.contains(name)) {
                    continue;
                }
                var lines = new TreeMap<Integer, Long>();
                for (int line = Math.max(1, source.getFirstLine()); line <= source.getLastLine(); line++) {
                    if (ICounter.EMPTY != source.getLine(line).getStatus()) {
                        lines.put(line, 0L);
                    }
                }
                var functions = new TreeMap<String, LcovReport.Function>();
                builder.getClasses().stream()
                        .filter(c -> c.getPackageName().equals(source.getPackageName())
                                && source.getName().equals(c.getSourceFileName()))
                        .forEach(c -> c.getMethods()
                                .forEach(m -> functions.put(
                                        c.getName() + "::" + m.getName() + " " + m.getDesc(),
                                        new LcovReport.Function(Math.max(0, m.getFirstLine()), 0))));
                inventory.put(name, new LcovReport.Source(lines, Map.of(), functions));
            }
        }
        return inventory;
    }
}
