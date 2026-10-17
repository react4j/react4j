package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** Source-map v3 emission for generated CSS. Positions use zero-based UTF-16 columns. */
public final class SourceMap {
    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private SourceMap() {}

    public record Location(String source, String content, int line, int column) {}

    private record Segment(int line, int column, int source, int originalLine, int originalColumn) {}

    public static final class Writer {
        private final StringBuilder css = new StringBuilder();
        private final LinkedHashMap<String, String> sources = new LinkedHashMap<>();
        private final List<Segment> segments = new ArrayList<>();
        private int line;
        private int column;

        public Writer append(String text) {
            return append(text, null);
        }

        public Writer append(String text, @Nullable Location location) {
            if (text.isEmpty()) {
                return this;
            }
            if (null == location) {
                segments.add(new Segment(line, column, -1, 0, 0));
            } else {
                if (location.line() < 0 || location.column() < 0) {
                    throw new IllegalArgumentException("Negative source-map position");
                }
                String previous = sources.putIfAbsent(location.source(), location.content());
                if (null != previous && !previous.equals(location.content())) {
                    throw new IllegalArgumentException("Conflicting source content: " + location.source());
                }
                int index = new ArrayList<>(sources.keySet()).indexOf(location.source());
                segments.add(new Segment(line, column, index, location.line(), location.column()));
            }
            css.append(text);
            for (int i = 0; i < text.length(); i++) {
                if ('\n' == text.charAt(i)) {
                    line++;
                    column = 0;
                } else {
                    column++;
                }
            }
            return this;
        }

        public String css() {
            return css.toString();
        }

        public String map(String file) {
            var map = new JsonObject();
            map.addProperty("version", 3);
            map.addProperty("file", file);
            var names = new JsonArray();
            var contents = new JsonArray();
            sources.forEach((name, content) -> {
                names.add(name);
                contents.add(content);
            });
            map.add("sources", names);
            map.add("sourcesContent", contents);
            map.add("names", new JsonArray());
            var mappings = new StringBuilder();
            int generatedLine = 0;
            int generatedColumn = 0;
            int source = 0;
            int originalLine = 0;
            int originalColumn = 0;
            boolean first = true;
            // An explicit unmapped endpoint bounds the final span when composing map sections.
            var bounded = new ArrayList<>(segments);
            if (!bounded.isEmpty()) {
                bounded.add(new Segment(line, column, -1, 0, 0));
            }
            for (var segment : bounded) {
                while (generatedLine < segment.line()) {
                    mappings.append(';');
                    generatedLine++;
                    generatedColumn = 0;
                    first = true;
                }
                if (!first) {
                    mappings.append(',');
                }
                first = false;
                vlq(mappings, segment.column() - generatedColumn);
                generatedColumn = segment.column();
                if (segment.source() >= 0) {
                    vlq(mappings, segment.source() - source);
                    vlq(mappings, segment.originalLine() - originalLine);
                    vlq(mappings, segment.originalColumn() - originalColumn);
                    source = segment.source();
                    originalLine = segment.originalLine();
                    originalColumn = segment.originalColumn();
                }
            }
            map.addProperty("mappings", mappings.toString());
            return map + "\n";
        }
    }

    public static String reference(String file) {
        return "\n/*# sourceMappingURL=" + file + ".map */\n";
    }

    private static void vlq(StringBuilder result, int value) {
        long remaining = value < 0 ? (-(long) value << 1) | 1 : (long) value << 1;
        do {
            var digit = (int) (remaining & 31);
            remaining >>>= 5;
            result.append(BASE64.charAt(0 == remaining ? digit : digit | 32));
        } while (0 != remaining);
    }
}
