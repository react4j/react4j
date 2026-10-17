package react4j.css.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/** Bazel extraction adapter; annotation processing emits both Java and CSS. */
public final class Compile {
    private Compile() {}

    public static void main(String[] args) {
        try {
            if (args.length != 1 || !args[0].startsWith("@")) {
                throw new IllegalArgumentException("Usage: compiler @argument-file");
            }
            compile(Files.readAllLines(Path.of(args[0].substring(1))).toArray(String[]::new));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void compile(String[] args) throws IOException {
        var srcjar = Path.of(args[0]);
        var cssOutput = Path.of(args[1]);
        Path work = Files.createTempDirectory("jcss-compile-");
        try {
            Path generated = Files.createDirectory(work.resolve("sources"));
            Path classes = Files.createDirectory(work.resolve("classes"));
            var compiler = ToolProvider.getSystemJavaCompiler();
            try (var manager = compiler.getStandardFileManager(null, null, null)) {
                var sources = new ArrayList<Path>();
                var shared = new ArrayList<String>();
                var classpath = new ArrayList<Path>();
                for (int i = 2; i < args.length; i += 2) {
                    if (i + 1 == args.length) {
                        throw new IllegalArgumentException("Missing value for " + args[i]);
                    }
                    var path = Path.of(args[i + 1]);
                    switch (args[i]) {
                        case "--source" -> sources.add(path);
                        case "--shared" -> shared.add(Files.readString(path));
                        case "--classpath" -> classpath.add(path);
                        default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
                    }
                }
                manager.setLocationFromPaths(StandardLocation.CLASS_PATH, classpath);
                var options =
                        List.of("--release", "17", "-proc:only", "-s", generated.toString(), "-d", classes.toString());
                var task = compiler.getTask(
                        null, manager, null, options, null, manager.getJavaFileObjectsFromPaths(sources));
                task.setProcessors(List.of(new CssProcessor()));
                if (!task.call()) {
                    throw new IOException("CSS extraction failed");
                }
                try (var zip = new ZipOutputStream(Files.newOutputStream(srcjar));
                        var paths = Files.walk(generated)) {
                    for (var path : paths.filter(Files::isRegularFile).sorted().toList()) {
                        var entry = new ZipEntry(
                                generated.relativize(path).toString().replace('\\', '/'));
                        entry.setTimeLocal(LocalDateTime.of(2010, 1, 1, 0, 0));
                        zip.putNextEntry(entry);
                        Files.copy(path, zip);
                        zip.closeEntry();
                    }
                }
                var fragments = new ArrayList<String>();
                try (var paths = Files.walk(classes)) {
                    for (var path : paths.filter(p -> p.toString().endsWith(".css"))
                            .sorted()
                            .toList()) {
                        fragments.add(Files.readString(path));
                    }
                }
                Files.writeString(cssOutput, CssCompiler.bundle(shared, fragments));
            }
        } finally {
            try (var paths = Files.walk(work)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
