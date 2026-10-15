package react4j.browser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BrowserAssets {
    private BrowserAssets() {}

    public static void main(String[] args) throws IOException {
        if (args.length == 3 && args[0].equals("copy")) {
            Files.copy(Path.of(args[1]), Path.of(args[2]));
        } else if (args.length == 5 && args[0].equals("bundle")) {
            try (var output = Files.newOutputStream(Path.of(args[4]))) {
                for (int i = 1; i <= 3; i++) {
                    if (i > 1) {
                        output.write("\n;\n".getBytes(StandardCharsets.UTF_8));
                    }
                    Files.copy(Path.of(args[i]), output);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Expected copy <source> <output> or bundle <react> <dom> <compiled> <output>");
        }
    }
}
