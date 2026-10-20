package react4j.css.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Unicode Default Caseless Matching (C/F mappings), without normalization or locale-specific casing. */
final class UnicodeCaseFold {
    private static final Map<Integer, String> MAPPINGS = load();

    private UnicodeCaseFold() {}

    static String fold(String text) {
        var result = new StringBuilder();
        for (int offset = 0; offset < text.length(); ) {
            int codepoint = text.codePointAt(offset);
            String mapped = MAPPINGS.get(codepoint);
            if (null == mapped) {
                result.appendCodePoint(codepoint);
            } else {
                result.append(mapped);
            }
            offset += Character.charCount(codepoint);
        }
        return result.toString();
    }

    private static Map<Integer, String> load() {
        var mappings = new HashMap<Integer, String>();
        try (var reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(UnicodeCaseFold.class.getResourceAsStream("CaseFolding.txt")),
                StandardCharsets.UTF_8))) {
            for (var line : reader.lines().toList()) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                var fields = line.split(";");
                if ("C".equals(fields[1].trim()) || "F".equals(fields[1].trim())) {
                    var value = new StringBuilder();
                    for (var codepoint : fields[2].trim().split(" +")) {
                        value.appendCodePoint(Integer.parseInt(codepoint, 16));
                    }
                    mappings.put(Integer.parseInt(fields[0].trim(), 16), value.toString());
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        return Map.copyOf(mappings);
    }
}
