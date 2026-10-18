package react4j.css.integration;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import com.helger.css.writer.CSSWriterSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.testng.annotations.Test;

public final class CssLivenessIntegrationTest {
    private static String input(String property) throws Exception {
        return Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.liveness." + property))));
    }

    @Test
    public void realLinksHandleSeparatelyCompiledInterfaceAndFacadeBindings() throws Exception {
        String optimized = input("optimized");
        String development = input("development");
        assertEquals(input("alias").replace("alias.css.map", "optimized.css.map"), optimized);
        assertTrue(optimized.contains("ButtonStyles--success"), optimized);
        assertFalse(optimized.contains("ButtonStyles--cancel"), optimized);
        assertFalse(optimized.contains("MapStyles--dead"), optimized);
        assertTrue(development.contains("ButtonStyles--cancel"), development);
        assertTrue(development.contains("MapStyles--dead"), development);
        assertFalse(input("javascript").contains("$react4j$css$"));
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                optimized, new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        var components = sheet.getAllLayerRules().stream()
                .filter(layer -> layer.getAllSelectors().equals(List.of("components")))
                .findFirst()
                .orElseThrow();
        assertEquals(components.getAllMediaRules().size(), 1);
        assertEquals(components.getAllMediaRules().get(0).getAllStyleRules().size(), 1);
    }

    @Test
    public void globalNativeNestingRetainsContextsForDeadCheckedIds() throws Exception {
        var settings = new CSSWriterSettings(true);
        var optimized = Objects.requireNonNull(CSSReader.readFromStringReader(
                input("optimized"), new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        var development = Objects.requireNonNull(CSSReader.readFromStringReader(
                input("development"),
                new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        var optimizedRules = optimized.getAllLayerRules().get(1).getAllStyleRules();
        var developmentRules = development.getAllLayerRules().get(1).getAllStyleRules();
        for (int i = 0; i < 5; i++) {
            assertEquals(
                    optimizedRules.get(i).getAsCSSString(settings, 0),
                    developmentRules.get(i).getAsCSSString(settings, 0));
        }
        String root = "jcss-react4j-css-integration-LivenessEntry_0024_";
        assertEquals(
                optimizedRules.get(0).getSelectorsAsCSSString(settings, 0),
                ".ordinary,#" + root + "NestingDefaults--id-bound");
        assertEquals(
                ((CSSStyleRule) optimizedRules.get(2).getAllRules().get(0)).getSelectorsAsCSSString(settings, 0),
                ":not(&)");
        assertTrue(optimizedRules.get(3).getAllRules().get(0) instanceof CSSMediaRule);
        assertTrue(optimizedRules.get(4).getAllRules().get(0) instanceof CSSSupportsRule);
    }

    @Test
    public void prunedNestedListMapsSurvivingSelectorAndDeclarationToExactJavaPositions() throws Exception {
        String css = input("optimized");
        String source = input("source");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(input("map"));
        for (var pair : List.of(
                new String[] {"--active", "&.active {"},
                new String[] {"letter-spacing:3px", "letter-spacing: 3px"},
                new String[] {"color:rgb(1,2,3)", "color: rgb(1, 2, 3)"})) {
            int generated = css.indexOf(pair[0]);
            assertTrue(generated >= 0, pair[0]);
            int generatedLine = (int) css.substring(0, generated)
                            .chars()
                            .filter(c -> '\n' == c)
                            .count()
                    + 1;
            int generatedColumn = generated - css.lastIndexOf('\n', generated);
            var location = Objects.requireNonNull(consumer.getMappingForLine(generatedLine, generatedColumn));
            int original = source.indexOf(pair[1]);
            assertTrue(original >= 0);
            assertEquals(location.getOriginalFile(), "react4j/css/integration/LivenessEntry.java");
            assertEquals(
                    location.getLineNumber(),
                    (int) source.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - source.lastIndexOf('\n', original));
        }
    }
}
