package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.List;
import org.testng.annotations.Test;

public final class CssInputTest {
    @Test
    public void cataloguesInferCompleteDecodedInventoriesAndTieThemToSourceBytes() {
        String css = ".group :is(.flex,.hover\\:red){display:flex;}";
        String source = "demo/utility.css";
        for (var newline : List.of("\n", "\r\n", "\r")) {
            String multiline = ".one{}" + newline + ".two{}";
            assertEquals(
                    CssInput.classes(source, multiline, CssInput.catalogue(source, multiline)), List.of("one", "two"));
            assertEquals(CssInput.normalize(multiline), ".one{}\n.two{}");
        }
        var catalogue = CssInput.catalogue(source, css);
        assertEquals(CssInput.path(source), "react4j_css_inputs/64656d6f2f7574696c6974792e637373.css");
        assertEquals(CssInput.classes(source, css, catalogue), List.of("flex", "group", "hover:red"));
        assertEquals(CssInput.classes(source, "", CssInput.catalogue(source, "")), List.of());
        for (var field : List.of("version", "digest", "source", "classes")) {
            for (var value : List.of("null", "{}", "[]", "false", "1.5", "\"wrong\"")) {
                if ("classes".equals(field) && "[]".equals(value)) {
                    continue;
                }
                var invalid = JsonParser.parseString(catalogue).getAsJsonObject();
                invalid.add(field, JsonParser.parseString(value));
                expectThrows(IllegalArgumentException.class, () -> CssInput.classes(source, css, invalid.toString()));
            }
        }
        for (var value : List.of(
                "{",
                "null",
                "[]",
                "{\"version\":1}",
                catalogue.replace("[\"flex\",\"group\",\"hover:red\"]", "[\"flex\",\"flex\"]"),
                catalogue.replace("[\"flex\",\"group\",\"hover:red\"]", "[1]"))) {
            expectThrows(IllegalArgumentException.class, () -> CssInput.classes(source, css, value));
        }
        expectThrows(IllegalArgumentException.class, () -> CssInput.classes(source, css + " ", catalogue));
        expectThrows(IllegalArgumentException.class, () -> CssInput.catalogue("../unsafe.css", css));
        expectThrows(IllegalArgumentException.class, () -> CssInput.path("/absolute.css"));
    }
}
