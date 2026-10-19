package react4j.css.internal;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/** Applies parsed-token edits while preserving source-map names, source contents and section boundaries. */
public record MappedText(String text, JsonObject map) {
    private record Origin(int source, int line, int column, int name) {}

    private record Position(int line, int column) {}

    private static final Origin UNMAPPED = new Origin(-1, 0, 0, -1);

    private record Lines(String text, List<Integer> starts) {
        Lines(String text) {
            this(text, lineStarts(text));
        }

        int offset(int line, int column) {
            if (line < 0 || line >= starts.size() || column < 0) {
                throw new IllegalArgumentException("Source-map position is outside its text");
            }
            int offset = starts.get(line) + column;
            int end = line + 1 < starts.size() ? starts.get(line + 1) - 1 : text.length();
            if (offset > end) {
                throw new IllegalArgumentException("Source-map column is outside its line");
            }
            return offset;
        }

        Position position(int offset) {
            if (offset < 0 || offset > text.length()) {
                throw new IllegalArgumentException("Source-map offset is outside its text");
            }
            int found = Collections.binarySearch(starts, offset);
            int line = found < 0 ? -found - 2 : found;
            return new Position(line, offset - starts.get(line));
        }
    }

    public MappedText replace(List<SourceMap.Edit> edits) {
        var result = new StringBuilder();
        int cursor = 0;
        for (var edit : edits) {
            if (edit.start() < cursor || edit.end() < edit.start() || edit.end() > text.length()) {
                throw new IllegalArgumentException("Mapped edits must be ordered and within their text");
            }
            result.append(text, cursor, edit.start()).append(edit.value());
            cursor = edit.end();
        }
        result.append(text, cursor, text.length());
        var output = map.deepCopy();
        if (!output.has("version") || !"3".equals(output.get("version").toString())) {
            throw new IllegalArgumentException("Expected a source-map v3 document");
        }
        String replaced = result.toString();
        var before = new Lines(text);
        var after = new Lines(replaced);
        if (output.has("sections")) {
            sections(output, before, after, edits);
        } else {
            var points = points(before, output);
            var shifted = new TreeMap<Integer, Origin>();
            for (var point : points.entrySet()) {
                shifted.put(shift(point.getKey(), edits), point.getValue());
            }
            int delta = 0;
            for (var edit : edits) {
                int start = edit.start() + delta;
                var origin = edit.start() == edit.end() ? UNMAPPED : origin(points, before, edit.start());
                if (!edit.value().isEmpty()) {
                    shifted.put(start, origin);
                    for (int i = 0; i < edit.value().length(); i++) {
                        if ('\n' == edit.value().charAt(i)
                                && i + 1 < edit.value().length()) {
                            shifted.put(start + i + 1, origin);
                        }
                    }
                }
                shifted.put(start + edit.value().length(), origin(points, before, edit.end()));
                delta += edit.value().length() - (edit.end() - edit.start());
            }
            output.addProperty("mappings", encode(shifted, after));
            if (output.has("lineCount")) {
                output.addProperty("lineCount", after.starts().size());
            }
        }
        return new MappedText(replaced, output);
    }

    private void sections(JsonObject output, Lines before, Lines after, List<SourceMap.Edit> edits) {
        var sections = output.getAsJsonArray("sections");
        int previous = -1;
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i).getAsJsonObject();
            int start = sectionStart(section, before);
            int end = i + 1 < sections.size()
                    ? sectionStart(sections.get(i + 1).getAsJsonObject(), before)
                    : text.length();
            if (start <= previous || end < start) {
                throw new IllegalArgumentException("Source-map sections must be ordered");
            }
            previous = start;
            var local = new ArrayList<SourceMap.Edit>();
            for (var edit : edits) {
                // An insertion at a boundary precedes that section and has no authored origin.
                if (edit.start() == edit.end() && edit.start() == start) {
                    continue;
                }
                if (edit.start() >= start && edit.start() < end) {
                    if (edit.end() > end) {
                        throw new IllegalArgumentException("Mapped edit crosses a source-map section");
                    }
                    local.add(new SourceMap.Edit(edit.start() - start, edit.end() - start, edit.value()));
                } else if (edit.start() < start && edit.end() > start) {
                    throw new IllegalArgumentException("Mapped edit crosses a source-map section");
                }
            }
            section.add(
                    "map",
                    new MappedText(text.substring(start, end), section.getAsJsonObject("map"))
                            .replace(local)
                            .map());
            var position = after.position(shift(start, edits));
            var offset = new JsonObject();
            offset.addProperty("line", position.line());
            offset.addProperty("column", position.column());
            section.add("offset", offset);
        }
    }

    private static int sectionStart(JsonObject section, Lines lines) {
        var offset = section.getAsJsonObject("offset");
        return lines.offset(offset.get("line").getAsInt(), offset.get("column").getAsInt());
    }

    private static NavigableMap<Integer, Origin> points(Lines text, JsonObject map) {
        var points = new TreeMap<Integer, Origin>();
        int source = 0;
        int originalLine = 0;
        int originalColumn = 0;
        int name = 0;
        int line = 0;
        for (var row : map.get("mappings").getAsString().split(";", -1)) {
            int column = 0;
            for (var encoded : row.split(",")) {
                if (encoded.isEmpty()) {
                    continue;
                }
                var fields = SourceMap.decode(encoded);
                if (1 != fields.size() && 4 != fields.size() && 5 != fields.size()) {
                    throw new IllegalArgumentException("Invalid source-map segment");
                }
                if (fields.get(0) < 0) {
                    throw new IllegalArgumentException("Source-map columns must be ordered");
                }
                column += fields.get(0);
                var origin = UNMAPPED;
                if (fields.size() > 1) {
                    source += fields.get(1);
                    originalLine += fields.get(2);
                    originalColumn += fields.get(3);
                    if (5 == fields.size()) {
                        name += fields.get(4);
                    }
                    if (source < 0
                            || source >= map.getAsJsonArray("sources").size()
                            || originalLine < 0
                            || originalColumn < 0
                            || (5 == fields.size()
                                    && (name < 0
                                            || name
                                                    >= map.getAsJsonArray("names")
                                                            .size()))) {
                        throw new IllegalArgumentException("Invalid source-map source or name index");
                    }
                    origin = new Origin(source, originalLine, originalColumn, 5 == fields.size() ? name : -1);
                }
                points.put(text.offset(line, column), origin);
            }
            line++;
        }
        return points;
    }

    private static Origin origin(NavigableMap<Integer, Origin> points, Lines lines, int offset) {
        var previous = points.floorEntry(offset);
        return null == previous
                        || lines.position(previous.getKey()).line()
                                != lines.position(offset).line()
                ? UNMAPPED
                : previous.getValue();
    }

    private static int shift(int offset, List<SourceMap.Edit> edits) {
        int delta = 0;
        for (var edit : edits) {
            if (offset < edit.start()) {
                break;
            }
            if (offset < edit.end()) {
                return edit.start() + delta;
            }
            delta += edit.value().length() - (edit.end() - edit.start());
        }
        return offset + delta;
    }

    private static String encode(NavigableMap<Integer, Origin> points, Lines lines) {
        var result = new StringBuilder();
        int line = 0;
        int column = 0;
        int source = 0;
        int originalLine = 0;
        int originalColumn = 0;
        int name = 0;
        boolean first = true;
        for (var point : points.entrySet()) {
            var position = lines.position(point.getKey());
            while (line < position.line()) {
                result.append(';');
                line++;
                column = 0;
                first = true;
            }
            if (!first) {
                result.append(',');
            }
            first = false;
            SourceMap.vlq(result, position.column() - column);
            column = position.column();
            var origin = point.getValue();
            if (origin.source() >= 0) {
                SourceMap.vlq(result, origin.source() - source);
                SourceMap.vlq(result, origin.line() - originalLine);
                SourceMap.vlq(result, origin.column() - originalColumn);
                source = origin.source();
                originalLine = origin.line();
                originalColumn = origin.column();
                if (origin.name() >= 0) {
                    SourceMap.vlq(result, origin.name() - name);
                    name = origin.name();
                }
            }
        }
        return result.toString();
    }

    private static List<Integer> lineStarts(String text) {
        var starts = new ArrayList<Integer>();
        starts.add(0);
        for (int i = 0; i < text.length(); i++) {
            if ('\n' == text.charAt(i)) {
                starts.add(i + 1);
            }
        }
        return starts;
    }
}
