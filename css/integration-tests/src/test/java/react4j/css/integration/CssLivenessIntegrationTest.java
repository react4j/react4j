package react4j.css.integration;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
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
        assertTrue(optimized.contains("ButtonStyles--success"), optimized);
        assertFalse(optimized.contains("ButtonStyles--cancel"), optimized);
        assertFalse(optimized.contains("MapStyles--dead"), optimized);
        assertTrue(development.contains("ButtonStyles--cancel"), development);
        assertTrue(development.contains("MapStyles--dead"), development);
        assertFalse(input("javascript").contains("$react4j$css$"));
    }

    @Test
    public void prunedNestedListMapsSurvivingSelectorAndDeclarationToExactJavaPositions() throws Exception {
        String css = input("optimized");
        String source = input("source");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(input("map"));
        for (var pair : List.of(
                new String[] {"--active", "&.active {"}, new String[] {"color:rgb(1,2,3)", "color: rgb(1, 2, 3)"})) {
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
