package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.SymbolId;
import react4j.css.internal.CssResource.SymbolKind;

public final class CssRenamerTest {
    @Test
    public void parsedClassEditsPreserveStringsAttributesNativeNestingAndAuthoredAnchors() throws Exception {
        String selector = ".readable:is(.modifier,:not(.readable))";
        var writer = new SourceMap.Writer()
                .append("/* 😀 */\n")
                .append(selector, new SourceMap.Location("Button.java", "authored selector and declaration", 10, 3))
                .append("[data-text='readable']{")
                .append(
                        "content:'readable modifier';",
                        new SourceMap.Location("Button.java", "authored selector and declaration", 11, 6))
                .append("&.modifier{color:red;}}\n");
        var metadata = new CssResource(
                "Button", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), Map.of(), List.of());
        var resource = new CssCatalogue.Resource(
                metadata,
                writer.css(),
                JsonParser.parseString(writer.map("Button.css")).getAsJsonObject());
        var table = new CssNames(
                new CssNames.Policy(true, ""),
                List.of(
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, ""), "readable", "a", false, List.of()),
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, "modifier"),
                                "modifier",
                                "bc",
                                false,
                                List.of())));
        var result = new CssRenamer(table).rename(resource);
        assertEquals(
                result.css(),
                "/* 😀 */\n.a:is(.bc,:not(.a))[data-text='readable']{content:'readable modifier';&.bc{color:red;}}\n");
        assertEquals(CssRenamer.names(writer.css()), Set.of("readable", "modifier"));
        var map = new SourceMapConsumerV3();
        map.parse(result.map().toString());
        for (int column : List.of(1, 2, 8, 16)) {
            var location = map.getMappingForLine(2, column);
            assertNotNull(location);
            assert null != location;
            assertEquals(location.getOriginalFile(), "Button.java");
            assertEquals(location.getLineNumber(), 11);
            assertEquals(location.getColumnPosition(), 4);
        }
        int declaration = result.css().substring(result.css().indexOf('\n') + 1).indexOf("content");
        var location = map.getMappingForLine(2, declaration + 1);
        assertNotNull(location);
        assert null != location;
        assertEquals(location.getLineNumber(), 12);
        assertEquals(location.getColumnPosition(), 7);
        assertNull(map.getMappingForLine(1, 2));
        assertEquals(
                result.map().getAsJsonArray("sourcesContent").get(0).getAsString(),
                "authored selector and declaration");
        assertSame(new CssRenamer(new CssNames(new CssNames.Policy(false, ""), List.of())).rename(resource), resource);
    }
}
