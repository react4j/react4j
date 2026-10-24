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
    private static final String INPUT = "x_react4j_input";

    private SourceMap() {}

    public record Location(String source, String content, int line, int column) {}

    private record Segment(int line, int column, int source, int originalLine, int originalColumn) {}

    public record Span(int start, int end) {
        public Span {
            if (start < 0 || end < start) {
                throw new IllegalArgumentException("Invalid CSS span");
            }
        }
    }

    private record Mapping(int offset, @Nullable Location location) {}

    /** Copies retained bytes and their original mapping segments without reserializing CSS. */
    public static Writer project(String css, JsonObject map, List<Span> spans) {
        return copy(css, mappings(css, map), spans);
    }

    public record Edit(int start, int end, String value) {}

    /** Replaces parsed tokens, keeping each replacement at its existing authored anchor. */
    public static Writer replace(String css, JsonObject map, List<Edit> edits) {
        var mappings = mappings(css, map);
        var writer = new Writer();
        int offset = 0;
        for (var edit : edits) {
            if (edit.start() < offset || edit.end() < edit.start() || edit.end() > css.length()) {
                throw new IllegalArgumentException("CSS edits must be ordered and within the source");
            }
            copy(css, mappings, new Span(offset, edit.start()), writer);
            Location origin = null;
            for (var mapping : mappings) {
                if (mapping.offset() > edit.start()) {
                    break;
                }
                origin = mapping.location();
            }
            writer.append(edit.value(), origin);
            offset = edit.end();
        }
        copy(css, mappings, new Span(offset, css.length()), writer);
        return writer;
    }

    /** Looks up an authored anchor while composing an earlier compiler's output. */
    public static @Nullable Location location(String css, JsonObject map, int offset) {
        if (offset < 0 || offset > css.length()) {
            throw new IllegalArgumentException("CSS offset is outside the source");
        }
        return location(mappings(css, map), offset);
    }

    private static @Nullable Location location(List<Mapping> mappings, int offset) {
        Location result = null;
        for (var mapping : mappings) {
            if (mapping.offset() > offset) {
                break;
            }
            result = mapping.location();
        }
        return result;
    }

    /** Publishes an intermediate source's authoritative map for independently produced fragments. */
    public static void addInput(JsonObject map, String source, String content, JsonObject inputMap) {
        var input = new JsonObject();
        input.addProperty("source", source);
        input.addProperty("content", content);
        input.add("map", inputMap);
        map.add(INPUT, input);
    }

    private static int offset(Location location) {
        String content = location.content();
        int offset = 0;
        if (location.line() < 0 || location.column() < 0) {
            throw new IllegalArgumentException("Invalid intermediate source-map position");
        }
        for (int line = 0; line < location.line(); line++) {
            int end = content.indexOf('\n', offset);
            if (end < 0) {
                throw new IllegalArgumentException("Invalid intermediate source-map line");
            }
            offset = end + 1;
        }
        int end = content.indexOf('\n', offset);
        if (location.column() > (end < 0 ? content.length() : end) - offset) {
            throw new IllegalArgumentException("Invalid intermediate source-map column");
        }
        return offset + location.column();
    }

    private static List<Mapping> mappings(String css, JsonObject map) {
        var lines = new ArrayList<Integer>();
        lines.add(0);
        for (int i = 0; i < css.length(); i++) {
            if ('\n' == css.charAt(i)) {
                lines.add(i + 1);
            }
        }
        var mappings = new ArrayList<Mapping>();
        int source = 0;
        int originalLine = 0;
        int originalColumn = 0;
        int line = 0;
        for (var row : map.get("mappings").getAsString().split(";", -1)) {
            int column = 0;
            for (var encoded : row.split(",")) {
                if (encoded.isEmpty()) {
                    continue;
                }
                var fields = decode(encoded);
                if (1 != fields.size() && 4 != fields.size()) {
                    throw new IllegalArgumentException("Invalid CSS source-map segment");
                }
                column += fields.get(0);
                Location location = null;
                if (4 == fields.size()) {
                    source += fields.get(1);
                    originalLine += fields.get(2);
                    originalColumn += fields.get(3);
                    location = new Location(
                            map.getAsJsonArray("sources").get(source).getAsString(),
                            map.getAsJsonArray("sourcesContent").get(source).getAsString(),
                            originalLine,
                            originalColumn);
                }
                mappings.add(new Mapping(lines.get(line) + column, location));
            }
            line++;
        }
        return mappings;
    }

    private static Writer copy(String css, List<Mapping> mappings, List<Span> spans) {
        var writer = new Writer();
        int previousEnd = 0;
        for (var span : spans) {
            if (span.start() < previousEnd || span.end() > css.length()) {
                throw new IllegalArgumentException("CSS source-map spans must be ordered and within the source");
            }
            copy(css, mappings, span, writer);
            previousEnd = span.end();
        }
        return writer;
    }

    private static void copy(String css, List<Mapping> mappings, Span span, Writer writer) {
        int mapping = 0;
        int offset = span.start();
        while (offset < span.end()) {
            while (mapping + 1 < mappings.size() && mappings.get(mapping + 1).offset() <= offset) {
                mapping++;
            }
            int end = mapping + 1 < mappings.size()
                    ? Math.min(span.end(), mappings.get(mapping + 1).offset())
                    : span.end();
            var origin = mappings.isEmpty() || mappings.get(mapping).offset() > offset
                    ? null
                    : mappings.get(mapping).location();
            writer.append(css.substring(offset, end), origin);
            offset = end;
        }
    }

    static List<Integer> decode(String encoded) {
        var result = new ArrayList<Integer>();
        int value = 0;
        int shift = 0;
        for (int i = 0; i < encoded.length(); i++) {
            int digit = BASE64.indexOf(encoded.charAt(i));
            if (digit < 0 || shift > 30) {
                throw new IllegalArgumentException("Invalid CSS source-map VLQ");
            }
            value |= (digit & 31) << shift;
            if (0 == (digit & 32)) {
                result.add(0 == (value & 1) ? value >>> 1 : -(value >>> 1));
                value = 0;
                shift = 0;
            } else {
                shift += 5;
            }
        }
        if (0 != shift) {
            throw new IllegalArgumentException("Incomplete CSS source-map VLQ");
        }
        return result;
    }

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

        public Writer appendMapped(String text, JsonObject map) {
            copy(text, mappings(text, map), new Span(0, text.length()), this);
            return this;
        }

        /** Composes header-derived origins through the full provider's published input map. */
        public Writer appendMapped(String text, JsonObject map, JsonObject providerMap) {
            if (!providerMap.has(INPUT)) {
                return appendMapped(text, map);
            }
            var input = providerMap.getAsJsonObject(INPUT);
            String source = input.get("source").getAsString();
            String content = input.get("content").getAsString();
            var originals = mappings(content, input.getAsJsonObject("map"));
            var composed = new ArrayList<Mapping>();
            for (var mapping : mappings(text, map)) {
                var origin = mapping.location();
                if (null != origin && source.equals(origin.source())) {
                    if (!content.equals(origin.content())) {
                        throw new IllegalArgumentException("Conflicting intermediate source content: " + source);
                    }
                    origin = location(originals, offset(origin));
                }
                composed.add(new Mapping(mapping.offset(), origin));
            }
            copy(text, composed, new Span(0, text.length()), this);
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

    static void vlq(StringBuilder result, int value) {
        long remaining = value < 0 ? (-(long) value << 1) | 1 : (long) value << 1;
        do {
            var digit = (int) (remaining & 31);
            remaining >>>= 5;
            result.append(BASE64.charAt(0 == remaining ? digit : digit | 32));
        } while (0 != remaining);
    }
}
