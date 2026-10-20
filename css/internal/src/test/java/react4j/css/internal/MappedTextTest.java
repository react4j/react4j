package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.FilePosition;
import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Objects;
import org.testng.annotations.Test;

public final class MappedTextTest {
    private static JsonObject mapped(String text) throws Exception {
        var generator = new SourceMapGeneratorV3();
        generator.addMapping(
                "A.java", "before", new FilePosition(0, 1), new FilePosition(0, 0), new FilePosition(0, 2));
        generator.addMapping("B.java", "asset", new FilePosition(9, 4), new FilePosition(0, 2), new FilePosition(0, 5));
        generator.addMapping(
                "A.java", "after", new FilePosition(1, 2), new FilePosition(0, 5), new FilePosition(0, text.length()));
        var output = new StringBuilder();
        generator.appendTo(output, "app.js");
        var map = JsonParser.parseString(output.toString()).getAsJsonObject();
        var contents = new JsonArray();
        contents.add("source A");
        contents.add("source B");
        map.add("sourcesContent", contents);
        return map;
    }

    @Test
    public void parsedTokenReplacementPreservesNamesContentsAndSubsequentCoordinates() throws Exception {
        String text = "a XYZ tail";
        var map = mapped(text);
        for (var replacement : List.of("q", "a-much-longer-url", "line\nsecond")) {
            var result = new MappedText(text, map).replace(List.of(new SourceMap.Edit(2, 5, replacement)));
            assertEquals(result.text(), "a " + replacement + " tail");
            assertEquals(result.map().get("sources"), map.get("sources"));
            assertEquals(result.map().get("sourcesContent"), map.get("sourcesContent"));
            assertEquals(result.map().get("names"), map.get("names"));
            var consumer = new SourceMapConsumerV3();
            consumer.parse(result.map().toString());
            var asset = Objects.requireNonNull(consumer.getMappingForLine(1, 3));
            assertEquals(asset.getOriginalFile(), "B.java");
            assertEquals(asset.getIdentifier(), "asset");
            assertEquals(asset.getLineNumber(), 10);
            int line = replacement.contains("\n") ? 2 : 1;
            int col = replacement.contains("\n")
                    ? replacement.length() - replacement.indexOf('\n')
                    : 3 + replacement.length();
            var after = Objects.requireNonNull(consumer.getMappingForLine(line, col));
            assertEquals(after.getOriginalFile(), "A.java");
            assertEquals(after.getIdentifier(), "after");
            if (2 == line) {
                assertEquals(
                        Objects.requireNonNull(consumer.getMappingForLine(2, 1)).getOriginalFile(), "B.java");
            }
        }
        assertEquals(new MappedText(text, map).replace(List.of()).text(), text);
        assertEquals(
                new MappedText(text, map)
                        .replace(List.of(new SourceMap.Edit(2, 5, "")))
                        .text(),
                "a  tail");
    }

    @Test
    public void insertedBootstrapAndUtf16EditsAreUnmappedWithoutLosingOriginalAnchors() throws Exception {
        String text = "a XYZ 😀";
        var map = mapped(text);
        String prologue = "var base = '😀';\r\n";
        var result = new MappedText(text, map)
                .replace(List.of(
                        new SourceMap.Edit(0, 0, prologue),
                        new SourceMap.Edit(2, 5, "URL"),
                        new SourceMap.Edit(text.length(), text.length(), "\n// map")));
        var consumer = new SourceMapConsumerV3();
        consumer.parse(result.map().toString());
        assertNull(consumer.getMappingForLine(1, 1));
        assertEquals(Objects.requireNonNull(consumer.getMappingForLine(2, 3)).getIdentifier(), "asset");
        assertNull(consumer.getMappingForLine(3, 1));
        assertEquals(map, mapped(text));
        var empty =
                JsonParser.parseString(new SourceMap.Writer().map("empty.js")).getAsJsonObject();
        assertEquals(
                new MappedText("abc\n", empty)
                        .replace(List.of(new SourceMap.Edit(2, 3, "xyz")))
                        .text(),
                "abxyz\n");
    }

    @Test
    public void indexedCssMapsShiftSectionsAndKeepAuthoredContents() throws Exception {
        var first = new SourceMap.Writer().append(".a{url(XYZ)}\n", new SourceMap.Location("A.java", "author A", 2, 3));
        var second = new SourceMap.Writer().append(".b{url(LMN)}", new SourceMap.Location("B.java", "author B", 4, 5));
        var map = new JsonObject();
        map.addProperty("version", 3);
        map.addProperty("file", "app.css");
        var sections = new JsonArray();
        for (int i = 0; i < 2; i++) {
            var section = new JsonObject();
            var offset = new JsonObject();
            offset.addProperty("line", i);
            offset.addProperty("column", 0);
            section.add("offset", offset);
            section.add("map", JsonParser.parseString((0 == i ? first : second).map("part.css")));
            sections.add(section);
        }
        map.add("sections", sections);
        String css = first.css() + second.css();
        int a = css.indexOf("XYZ");
        int b = css.indexOf("LMN");
        var result = new MappedText(css, map)
                .replace(List.of(
                        new SourceMap.Edit(0, 0, "/* prefix */\n"),
                        new SourceMap.Edit(a, a + 3, "longer\nvalue"),
                        new SourceMap.Edit(b, b + 3, "z")));
        assertEquals(result.text(), "/* prefix */\n.a{url(longer\nvalue)}\n.b{url(z)}");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(result.map().toString());
        assertEquals(Objects.requireNonNull(consumer.getMappingForLine(2, 8)).getOriginalFile(), "A.java");
        assertEquals(Objects.requireNonNull(consumer.getMappingForLine(4, 8)).getOriginalFile(), "B.java");
        expectThrows(
                IllegalArgumentException.class,
                () -> new MappedText(css, map).replace(List.of(new SourceMap.Edit(a, b + 3, "x"))));
        var invalid = map.deepCopy();
        invalid.getAsJsonArray("sections")
                .get(1)
                .getAsJsonObject()
                .getAsJsonObject("offset")
                .addProperty("line", 0);
        expectThrows(IllegalArgumentException.class, () -> new MappedText(css, invalid).replace(List.of()));
    }

    @Test
    public void malformedCoordinatesSegmentsAndEditsFail() throws Exception {
        String text = "a XYZ tail";
        var map = mapped(text);
        for (var edits : List.of(
                List.of(new SourceMap.Edit(-1, 0, "x")),
                List.of(new SourceMap.Edit(2, 1, "x")),
                List.of(new SourceMap.Edit(0, 100, "x")),
                List.of(new SourceMap.Edit(2, 3, "x"), new SourceMap.Edit(1, 2, "y")))) {
            expectThrows(IllegalArgumentException.class, () -> new MappedText(text, map).replace(edits));
        }
        for (var mappings : List.of("!", "g", "AA", "D", "AAAAF", "AEAA", "ADAA", "AADA", "AAAD", "gG")) {
            var invalid = map.deepCopy();
            invalid.addProperty("mappings", mappings);
            expectThrows(IllegalArgumentException.class, () -> new MappedText(text, invalid).replace(List.of()));
        }
        var invalid = map.deepCopy();
        invalid.addProperty("mappings", ";AAAA");
        expectThrows(IllegalArgumentException.class, () -> new MappedText(text, invalid).replace(List.of()));
        invalid.remove("version");
        expectThrows(IllegalArgumentException.class, () -> new MappedText(text, invalid).replace(List.of()));
    }
}
