package react4j.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SiteDeploy {
    static final Set<String> EXTERNAL_PATHS = Set.of("todomvc", "heart-rate-monitor");

    private SiteDeploy() {}

    static void prepare(Path source, Path destination) throws IOException {
        final var root = source.toRealPath();
        Files.createDirectories(destination);
        final var target = destination.toRealPath();
        if (root.startsWith(target) || target.startsWith(root) || !Files.isRegularFile(root.resolve("index.html"))) {
            throw new IOException("Expected separate site and deployment directories");
        }
        try (var entries = Files.list(root)) {
            for (final var entry : entries.toList()) {
                if (EXTERNAL_PATHS.contains(entry.getFileName().toString())
                        || entry.getFileName().toString().equals(".git")) {
                    throw new IOException("Site output would replace an externally owned path: " + entry);
                }
            }
        }
        try (var entries = Files.list(target)) {
            for (final var entry : entries.toList()) {
                final var name = entry.getFileName().toString();
                if (!name.equals(".git") && !EXTERNAL_PATHS.contains(name)) {
                    try (var paths = Files.walk(entry)) {
                        for (final var path :
                                paths.sorted(Comparator.reverseOrder()).toList()) {
                            Files.delete(path);
                        }
                    }
                }
            }
        }
        try (var paths = Files.walk(root)) {
            for (final var path : paths.toList()) {
                final var output = target.resolve(root.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(output);
                } else {
                    Files.copy(path, output);
                }
            }
        }
    }
}
