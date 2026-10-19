package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Objects;
import org.testng.annotations.Test;

public final class SourceMapTest {
    @Test
    public void closureReadsUtf16ColumnsUnmappedSpansAndSignedDeltas() throws Exception {
        var writer = new SourceMap.Writer();
        writer.append("", new SourceMap.Location("ignored", "", 0, 0));
        writer.append("é😀", new SourceMap.Location("A.java", "a\n😀", 20, 4096));
        writer.append(" ");
        writer.append("\n");
        writer.append("b", new SourceMap.Location("B.java", "b", 4, 100));
        writer.append("c", new SourceMap.Location("A.java", "a\n😀", 1, 2));
        writer.append("\n\n");
        writer.append("d", new SourceMap.Location("A.java", "a\n😀", 0, 0));
        String map = writer.map("output.css");
        var parsed = JsonParser.parseString(map).getAsJsonObject();
        assertEquals(parsed.get("file").getAsString(), "output.css");
        assertEquals(parsed.getAsJsonArray("sources").size(), 2);
        assertEquals(parsed.getAsJsonArray("sourcesContent").get(0).getAsString(), "a\n😀");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        var first = consumer.getMappingForLine(1, 1);
        assertNotNull(first);
        assert null != first;
        assertEquals(first.getOriginalFile(), "A.java");
        assertEquals(first.getLineNumber(), 21);
        assertEquals(first.getColumnPosition(), 4097);
        assertNull(consumer.getMappingForLine(1, 4));
        var second = consumer.getMappingForLine(2, 1);
        assertNotNull(second);
        assert null != second;
        assertEquals(second.getOriginalFile(), "B.java");
        var reversed = consumer.getMappingForLine(2, 2);
        assertNotNull(reversed);
        assert null != reversed;
        assertEquals(reversed.getOriginalFile(), "A.java");
        assertEquals(reversed.getLineNumber(), 2);
        assertEquals(reversed.getColumnPosition(), 3);
        assertEquals(writer.css(), "é😀 \nbc\n\nd");
    }

    @Test
    public void rejectsAmbiguousSourcesAndInvalidLocations() {
        var writer = new SourceMap.Writer();
        writer.append("a", new SourceMap.Location("A.java", "a", 0, 0));
        expectThrows(
                IllegalArgumentException.class, () -> writer.append("b", new SourceMap.Location("A.java", "b", 0, 0)));
        expectThrows(
                IllegalArgumentException.class, () -> writer.append("b", new SourceMap.Location("B.java", "b", -1, 0)));
        expectThrows(
                IllegalArgumentException.class, () -> writer.append("b", new SourceMap.Location("B.java", "b", 0, -1)));
        assertEquals(SourceMap.reference("Button.css"), "\n/*# sourceMappingURL=Button.css.map */\n");
    }

    @Test
    public void projectionPreservesSignedLocationsAndUnmappedUtf16Spans() throws Exception {
        var writer = new SourceMap.Writer()
                .append("drop ")
                .append("é😀\n", new SourceMap.Location("A.java", "source A", 20, 4096))
                .append("b", new SourceMap.Location("B.java", "source B", 4, 100))
                .append("c", new SourceMap.Location("A.java", "source A", 1, 2))
                .append(" ");
        var map = JsonParser.parseString(writer.map("input.css")).getAsJsonObject();
        var projected = SourceMap.project(
                writer.css(), map, List.of(new SourceMap.Span(5, writer.css().length())));
        assertEquals(projected.css(), "é😀\nbc ");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(projected.map("output.css"));
        var first = consumer.getMappingForLine(1, 1);
        assertNotNull(first);
        assert null != first;
        assertEquals(first.getLineNumber(), 21);
        assertEquals(first.getColumnPosition(), 4097);
        var reversed = consumer.getMappingForLine(2, 2);
        assertNotNull(reversed);
        assert null != reversed;
        assertEquals(reversed.getOriginalFile(), "A.java");
        assertEquals(reversed.getLineNumber(), 2);
        assertEquals(reversed.getColumnPosition(), 3);
        assertNull(consumer.getMappingForLine(2, 3));
        assertEquals(SourceMap.project(writer.css(), map, List.of()).css(), "");
        var emptyMap =
                JsonParser.parseString(new SourceMap.Writer().map("input.css")).getAsJsonObject();
        assertEquals(
                SourceMap.project("x", emptyMap, List.of(new SourceMap.Span(0, 1)))
                        .css(),
                "x");
    }

    @Test
    public void replacementAndPruningPreserveAnchorsAcrossCrLfTabsAndUtf16() throws Exception {
        String java = "class Styles {\r\n\t// 😀\r\n}\r\n";
        var before = new SourceMap.Location("Styles.java", java, 1, 1);
        var token = new SourceMap.Location("Styles.java", java, 2, 4);
        var after = new SourceMap.Location("Styles.java", java, 0, 0);
        var original = new SourceMap.Writer()
                .append("discard\r\n")
                .append("\t😀.", before)
                .append("longName", token)
                .append(" {\r\n", after)
                .append("  ")
                .append("color:red", after)
                .append(";}");
        var map = JsonParser.parseString(original.map("original.css")).getAsJsonObject();
        int start = original.css().indexOf("longName");
        for (String replacement : List.of("a", "muchLongerThanTheOriginalName")) {
            var renamed =
                    SourceMap.replace(original.css(), map, List.of(new SourceMap.Edit(start, start + 8, replacement)));
            var pruned = SourceMap.project(
                    renamed.css(),
                    JsonParser.parseString(renamed.map("renamed.css")).getAsJsonObject(),
                    List.of(new SourceMap.Span(9, renamed.css().length())));
            var consumer = new SourceMapConsumerV3();
            consumer.parse(pruned.map("final.css"));
            assertEquals(pruned.css(), "\t😀." + replacement + " {\r\n  color:red;}");
            assertEquals(
                    Objects.requireNonNull(consumer.getMappingForLine(1, 1)).getLineNumber(), 2);
            for (int col = 5; col < 5 + replacement.length(); col++) {
                var origin = consumer.getMappingForLine(1, col);
                assertNotNull(origin);
                assert null != origin;
                assertEquals(origin.getLineNumber(), 3);
                assertEquals(origin.getColumnPosition(), 5);
            }
            assertEquals(
                    Objects.requireNonNull(consumer.getMappingForLine(1, 5 + replacement.length()))
                            .getLineNumber(),
                    1);
            assertNull(consumer.getMappingForLine(2, 1));
            assertEquals(
                    Objects.requireNonNull(consumer.getMappingForLine(2, 3)).getLineNumber(), 1);
            assertEquals(
                    JsonParser.parseString(pruned.map("final.css"))
                            .getAsJsonObject()
                            .getAsJsonArray("sourcesContent")
                            .get(0)
                            .getAsString(),
                    java);
        }
    }

    @Test
    public void projectionRejectsInvalidSpansAndMalformedVlqSegments() {
        var writer = new SourceMap.Writer().append("abc");
        var map = JsonParser.parseString(writer.map("input.css")).getAsJsonObject();
        expectThrows(IllegalArgumentException.class, () -> new SourceMap.Span(-1, 0));
        expectThrows(IllegalArgumentException.class, () -> new SourceMap.Span(2, 1));
        for (var spans : List.of(
                List.of(new SourceMap.Span(0, 4)), List.of(new SourceMap.Span(1, 2), new SourceMap.Span(0, 1)))) {
            expectThrows(IllegalArgumentException.class, () -> SourceMap.project(writer.css(), map, spans));
        }
        for (var encoded : List.of("!", "g", "ggggggggA", "AA")) {
            map.addProperty("mappings", encoded);
            expectThrows(
                    IllegalArgumentException.class,
                    () -> SourceMap.project(writer.css(), map, List.of(new SourceMap.Span(0, 3))));
        }
    }
}
