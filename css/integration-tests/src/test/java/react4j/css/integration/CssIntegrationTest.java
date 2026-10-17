package react4j.css.integration;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testng.annotations.Test;

public final class CssIntegrationTest {
    @Test
    public void j2clCompilationAndDependencyGraphAssemblyUseDeclaredInputs() throws Exception {
        var styles = ButtonStyles.of();
        assertSame(styles, ButtonStyles.of());
        String root = "jcss-react4j-css-integration-ButtonStyles";
        assertEquals(styles.style(), root);
        assertEquals(styles.forState(StyleState.SUCCESS), root + " " + root + "--success");
        assertEquals(styles.forState(StyleState.CANCEL), root + " " + root + "--cancel");
        String css = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.fixture"))));
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                css,
                new CSSReaderSettings()
                        .setCustomErrorHandler(new ThrowingCSSParseErrorHandler())
                        .setCustomExceptionHandler(error -> {
                            throw new AssertionError(error);
                        })));
        assertEquals(sheet.getRuleCount(), 2);
        var layers = sheet.getAllLayerRules();
        assertEquals(layers.size(), 2);
        assertEquals(layers.get(0).getAllSelectors(), List.of("shared", "components"));
        assertEquals(layers.get(0).getRuleCount(), 0);
        var components = layers.get(1);
        assertEquals(components.getAllSelectors(), List.of("components"));
        assertEquals(components.getRuleCount(), 7);
        var rules = components.getAllStyleRules();
        assertEquals(rules.size(), 5);
        assertStyle(rules.get(0), List.of("." + root), "color", "white");
        assertStyle(rules.get(1), List.of("." + root, "." + root + "--success"), "background-color", "green");
        assertStyle(rules.get(2), List.of("." + root, "." + root + "--cancel"), "background-color", "red");
        assertStyle(rules.get(3), List.of("." + root, ":hover"), "animation-name", root + "--keyframes-pulse");
        String peer = "jcss-react4j-css-integration-SharedPulse--keyframes";
        assertEquals(
                components.getAllRules().stream()
                        .filter(CSSKeyframesRule.class::isInstance)
                        .map(rule -> ((CSSKeyframesRule) rule).getAnimationName())
                        .toList(),
                List.of(root + "--keyframes-pulse", peer));
        assertEquals(
                ((CSSExpressionMemberTermSimple)
                                Objects.requireNonNull(rules.get(4).getDeclarationOfPropertyName("animation-name"))
                                        .getExpression()
                                        .getAllMembers()
                                        .get(0))
                        .getValue(),
                peer);
    }

    @Test
    public void resourcesSurviveJ2clAndBundledMapsPointToTheOriginalJava() throws Exception {
        var tree = Path.of(Objects.requireNonNull(System.getProperty("css.tree")));
        Path fragment = tree.resolve("react4j/css/integration/ButtonStyles.css");
        assertTrue(Files.isRegularFile(fragment));
        assertTrue(Files.isRegularFile(Path.of(fragment + ".map")));
        assertTrue(Files.isRegularFile(Path.of(fragment + ".json")));
        assertTrue(Files.isRegularFile(tree.resolve("react4j/css/integration/ButtonStyles.impl.java.js")));
        String css = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.fixture"))));
        String map = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.map"))));
        var indexed = JsonParser.parseString(map).getAsJsonObject();
        assertEquals(indexed.getAsJsonArray("sections").size(), 3);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        String source = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.source"))));
        for (var pair : Map.of(
                        "background-color:green", "background-color: green",
                        "@keyframes jcss-react4j-css-integration-ButtonStyles--keyframes-pulse", "@keyframes pulse",
                        "opacity:0.5", "opacity: 0.5",
                        "animation-name:jcss-react4j-css-integration-ButtonStyles--keyframes-pulse",
                                "animation-name: pulse")
                .entrySet()) {
            int index = css.indexOf(pair.getKey());
            assertTrue(index >= 0, css);
            int line =
                    (int) css.substring(0, index).chars().filter(c -> '\n' == c).count() + 1;
            int column = index - css.lastIndexOf('\n', index - 1);
            var location = Objects.requireNonNull(consumer.getMappingForLine(line, column));
            assertEquals(location.getOriginalFile(), "react4j/css/integration/ButtonStyles.java");
            int original = source.indexOf(pair.getValue());
            assertEquals(
                    location.getLineNumber(),
                    (int) source.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - source.lastIndexOf('\n', original - 1));
        }
        var componentMap =
                indexed.getAsJsonArray("sections").get(0).getAsJsonObject().getAsJsonObject("map");
        assertEquals(componentMap.getAsJsonArray("sourcesContent").get(0).getAsString(), source);
        assertTrue(css.endsWith("/*# sourceMappingURL=fixture.css.map */\n"));
    }

    private static void assertStyle(CSSStyleRule rule, List<String> selectors, String property, String value) {
        assertEquals(rule.getSelectorCount(), 1);
        var members = rule.getAllSelectors().get(0).getAllMembers();
        assertTrue(members.stream().allMatch(CSSSelectorSimpleMember.class::isInstance));
        assertEquals(
                members.stream()
                        .map(member -> ((CSSSelectorSimpleMember) member).getValue())
                        .toList(),
                selectors);
        var declarations = rule.getAllDeclarations();
        assertEquals(declarations.size(), 1);
        var declaration = declarations.get(0);
        assertEquals(declaration.getProperty(), property);
        assertFalse(declaration.isImportant());
        var values = declaration.getExpression().getAllMembers();
        assertEquals(values.size(), 1);
        assertTrue(values.get(0) instanceof CSSExpressionMemberTermSimple);
        assertEquals(((CSSExpressionMemberTermSimple) values.get(0)).getValue(), value);
    }
}
