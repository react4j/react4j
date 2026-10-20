package react4j.testing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/** Copies changed goldens after checking declared paths and concurrent edits. */
@NullMarked
public final class GoldenUpdater {
    private GoldenUpdater() {}

    public static void main(String[] args) throws IOException {
        if (3 != args.length) {
            throw new IllegalArgumentException("Expected regenerated, baseline and maintained fixture roots");
        }
        var source = Path.of(args[0]);
        var baseline = Path.of(args[1]);
        var destination = Path.of(args[2]);
        var changed = new ArrayList<Path>();
        for (var tree : List.of("expected", "expectedFormatted")) {
            try (var files = Files.walk(source.resolve(tree))) {
                for (var file : files.filter(Files::isRegularFile).sorted().toList()) {
                    var relative = source.relativize(file);
                    var original = baseline.resolve(relative);
                    var target = destination.resolve(relative);
                    if (!Files.isRegularFile(original) || !Files.isRegularFile(target)) {
                        throw new IllegalArgumentException(
                                "Declare and review the new golden before copying: " + target);
                    }
                    if (-1 != Files.mismatch(file, original) && -1 != Files.mismatch(file, target)) {
                        if (-1 != Files.mismatch(original, target)) {
                            throw new IllegalArgumentException("Golden changed during generation: " + target);
                        }
                        changed.add(relative);
                    }
                }
            }
        }
        for (var relative : changed) {
            Files.copy(source.resolve(relative), destination.resolve(relative), StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Updated " + changed.size() + " golden files");
    }
}
