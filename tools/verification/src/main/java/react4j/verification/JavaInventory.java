package react4j.verification;

import com.google.testing.coverage.jarjar.org.objectweb.asm.ClassReader;
import com.google.testing.coverage.jarjar.org.objectweb.asm.ClassVisitor;
import com.google.testing.coverage.jarjar.org.objectweb.asm.Label;
import com.google.testing.coverage.jarjar.org.objectweb.asm.MethodVisitor;
import com.google.testing.coverage.jarjar.org.objectweb.asm.Opcodes;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class JavaInventory {
    private JavaInventory() {}

    static Map<String, LcovReport.Source> read(Map<String, Path> archives, Set<String> declared) throws IOException {
        var inventory = new TreeMap<String, LcovReport.Source>();
        for (var entry : archives.entrySet()) {
            var builder = new CoverageBuilder();
            new Analyzer(new ExecutionDataStore(), builder)
                    .analyzeAll(entry.getValue().toFile());
            final var constructorLines = constructorEntryLines(entry.getValue());
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
                        .forEach(c -> c.getMethods().forEach(m -> {
                            final var key = c.getName() + "::" + m.getName() + " " + m.getDesc();
                            final int line = constructorLines.getOrDefault(key, m.getFirstLine());
                            functions.put(key, new LcovReport.Function(Math.max(0, line), 0));
                        }));
                inventory.put(name, new LcovReport.Source(lines, Map.of(), functions));
            }
        }
        return inventory;
    }

    private static Map<String, Integer> constructorEntryLines(Path archive) throws IOException {
        final var lines = new HashMap<String, Integer>();
        try (var zip = new ZipFile(archive.toFile())) {
            for (var file : zip.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .toList()) {
                try (var input = zip.getInputStream(file)) {
                    final var reader = new ClassReader(input);
                    reader.accept(
                            new ClassVisitor(Opcodes.ASM9) {
                                @Override
                                public @Nullable MethodVisitor visitMethod(
                                        int access,
                                        String name,
                                        String descriptor,
                                        @Nullable String signature,
                                        String @Nullable [] exceptions) {
                                    if (!"<init>".equals(name)) {
                                        return null;
                                    }
                                    final var key = reader.getClassName() + "::" + name + " " + descriptor;
                                    return new MethodVisitor(Opcodes.ASM9) {
                                        @Override
                                        public void visitLineNumber(int line, Label start) {
                                            // Instance field initializers can have earlier source lines than the
                                            // constructor entry.
                                            lines.putIfAbsent(key, line);
                                        }
                                    };
                                }
                            },
                            ClassReader.SKIP_FRAMES);
                }
            }
        }
        return lines;
    }
}
