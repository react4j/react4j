package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.List;
import org.testng.annotations.Test;

public final class ApplicationConfigurationTest {
    @Test
    public void compilerGrammarAndOverrideOrderingAreSharedWithRuntime() {
        var json = JsonParser.parseString(ApplicationConfiguration.defines(List.of(
                                "flag",
                                "flag=false",
                                "truth=true",
                                "number=1.25",
                                "number=2e2",
                                "text='value=with=equals'",
                                "bare=production",
                                "double=\"quoted\"",
                                "markup='</script>'")
                        .stream()
                        .map(value -> "--define=" + value)
                        .toList()))
                .getAsJsonObject();
        assertFalse(json.get("flag").getAsBoolean());
        assertTrue(json.get("truth").getAsBoolean());
        assertEquals(json.get("number").getAsDouble(), 200.0);
        assertEquals(json.get("text").getAsString(), "value=with=equals");
        assertEquals(json.get("bare").getAsString(), "production");
        assertEquals(json.get("double").getAsString(), "quoted");
        assertEquals(json.get("markup").getAsString(), "</script>");
        assertEquals(ApplicationConfiguration.defines(List.of("--rewrite_polyfills=false")), "{}");
        assertTrue(
                ApplicationConfiguration.defines(List.of("--define=number=NaN")).contains("NaN"));
        for (var invalid : List.of("=true", "empty=")) {
            expectThrows(
                    RuntimeException.class, () -> ApplicationConfiguration.defines(List.of("--define=" + invalid)));
        }
    }

    @Test
    public void onlyTrailingMapCommentsAreRemoved() {
        for (var suffix : List.of("//# sourceMappingURL=app.js.map\n", "/*# sourceMappingURL=app.css.map */\r\n")) {
            assertEquals(ApplicationConfiguration.withoutSourceMap("source;\n" + suffix), "source;\n");
        }
        for (var unchanged : List.of(
                "",
                "const value='//# sourceMappingURL=lookalike';\n",
                "//# sourceMappingURL=earlier\nsource;\n",
                "/* ordinary comment */\n",
                "source;")) {
            assertEquals(ApplicationConfiguration.withoutSourceMap(unchanged), unchanged);
        }
    }
}
