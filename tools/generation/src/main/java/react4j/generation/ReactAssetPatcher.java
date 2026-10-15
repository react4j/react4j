package react4j.generation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jspecify.annotations.NullMarked;

/** Exposes React.Element in the pinned React UMD assets. */
@NullMarked
public final class ReactAssetPatcher {
    private ReactAssetPatcher() {}

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected input asset, output asset and patch mode");
        }
        var source = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        var needle =
                switch (args[2]) {
                    case "development" -> "Fragment: REACT_FRAGMENT_TYPE,";
                    case "production" -> "Fragment:q,";
                    case "copy" -> "";
                    default -> throw new IllegalArgumentException("Unknown patch mode: " + args[2]);
                };
        if (!needle.isEmpty()) {
            var offset = source.indexOf(needle);
            if (offset < 0 || source.indexOf(needle, offset + needle.length()) >= 0) {
                throw new IllegalArgumentException("Expected exactly one React.Element patch location");
            }
            var replacement = args[2].equals("development")
                    ? "Fragment: REACT_FRAGMENT_TYPE,\n  Element: REACT_ELEMENT_TYPE,"
                    : "Fragment:q,Element:y,";
            source = source.replace(needle, replacement);
        }
        Files.writeString(Path.of(args[1]), source, StandardCharsets.UTF_8);
    }
}
