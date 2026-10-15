package react4j.release;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ReleaseBundleTest {
    private ReleaseBundleTest() {}

    public static void main(String[] args) throws Exception {
        final var outputs = Objects.requireNonNull(Path.of(args[0]).getParent());
        final var version = Files.readString(Path.of(args[1])).strip();
        final var arezVersion = Files.readString(Path.of(args[2])).strip();
        final var work = Files.createTempDirectory("react4j-bundle-test");
        try {
            final var first = work.resolve("first.zip");
            final var second = work.resolve("second.zip");
            ReleaseBundle.pack(outputs, version, arezVersion, first, false);
            ReleaseBundle.pack(outputs, version, arezVersion, second, false);
            require(Files.mismatch(first, second) == -1, "Bundle bytes must be reproducible");
            final var files = ReleaseBundle.read(first);
            require(files.size() == 60, "Expected twelve artifacts and four checksums per artifact");
            final var checksum = files.keySet().stream()
                    .filter(name -> name.endsWith(".sha256"))
                    .findFirst()
                    .orElseThrow();
            files.put(checksum, "invalid\n".getBytes(StandardCharsets.US_ASCII));
            ReleaseBundle.write(second, files);
            rejects(() -> ReleaseBundle.verify(second, version, arezVersion, false), "Checksum mismatch");
            final var missing = ReleaseBundle.read(first);
            missing.remove(missing.keySet().stream()
                    .filter(name -> name.endsWith(".pom"))
                    .findFirst()
                    .orElseThrow());
            ReleaseBundle.write(second, missing);
            rejects(() -> ReleaseBundle.verify(second, version, arezVersion, false), "Missing or empty");
            rejects(() -> ReleaseBundle.verify(first, version, "123.456", false), "Wrong migrated Arez");
            final var extra = ReleaseBundle.read(first);
            extra.put("unexpected.txt", new byte[] {1});
            ReleaseBundle.write(second, extra);
            rejects(() -> ReleaseBundle.verify(second, version, arezVersion, false), "Unexpected bundle entries");
            rejects(() -> ReleaseBundle.version("0.231-SNAPSHOT", true), "publication version");
            rejects(() -> ReleaseBundle.version("../0.231", false), "Invalid version");
            final var project = work.resolve("project");
            Files.createDirectories(project.resolve("docs"));
            Files.writeString(
                    project.resolve("CHANGELOG.md"),
                    "# Change Log\n\n### Unreleased\n\n* New behavior.\n\n### [v0.230](old)\n\nOld notes.\n");
            Files.writeString(project.resolve("docs/project_setup.md"), "<react4j.version>0.230</react4j.version>\n");
            ReleaseBundle.prepareNotes(project, "0.231", LocalDate.of(2026, 10, 15));
            final var changelog = Files.readString(project.resolve("CHANGELOG.md"));
            require(
                    changelog.contains("(2026-10-15)") && changelog.contains("compare/v0.230...v0.231"),
                    "Release header");
            require(ReleaseBundle.notes(changelog, "0.231").contains("* New behavior."), "Release notes");
            require(Files.readString(project.resolve("docs/project_setup.md")).contains(">0.231<"), "Maven property");
            rejects(
                    () -> ReleaseBundle.prepareNotes(project, "0.231", LocalDate.of(2026, 10, 15)),
                    "release must be new");
            require(
                    Files.readString(project.resolve("CHANGELOG.md")).equals(changelog),
                    "Rejected preparation must not modify notes");
            System.out.println(
                    "Real publication bundle repeatability, corruption, completeness, prerequisites and release notes"
                            + " pass");
        } finally {
            try (var paths = Files.walk(work)) {
                for (final var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    @FunctionalInterface
    private interface Operation {
        void run() throws Exception;
    }

    private static void rejects(Operation operation, String text) throws Exception {
        try {
            operation.run();
        } catch (IOException | IllegalArgumentException exception) {
            require(exception.getMessage() != null && exception.getMessage().contains(text), exception.toString());
            return;
        }
        throw new AssertionError("Expected rejection: " + text);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
