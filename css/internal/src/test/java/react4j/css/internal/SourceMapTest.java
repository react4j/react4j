package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
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
}
