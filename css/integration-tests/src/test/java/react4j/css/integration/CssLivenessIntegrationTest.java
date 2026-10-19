package react4j.css.integration;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import com.helger.css.writer.CSSWriterSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;

public final class CssLivenessIntegrationTest {
    private static String input(String property) throws Exception {
        return Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.liveness." + property))));
    }

    private static String compiled(String owner, String kind, String name) throws Exception {
        for (var item : JsonParser.parseString(input("names")).getAsJsonObject().getAsJsonArray("names")) {
            var symbol = item.getAsJsonObject();
            if (("react4j.css.integration." + owner).equals(symbol.get("owner").getAsString())
                    && kind.equals(symbol.get("kind").getAsString())
                    && name.equals(symbol.get("name").getAsString())) {
                return symbol.get("compiled").getAsString();
            }
        }
        throw new AssertionError("Missing symbol in reverse-name artifact: " + owner + "#" + name);
    }

    private static Set<String> classes(CascadingStyleSheet sheet) {
        var classes = new HashSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onStyleRuleSelector(CSSSelector selector) {
                for (var member : selector.getAllMembers()) {
                    if (member instanceof CSSSelectorSimpleMember simple && simple.isClass()) {
                        classes.add(simple.getValue().substring(1));
                    }
                }
            }
        });
        return classes;
    }

    @Test
    public void realLinksHandleSeparatelyCompiledInterfaceAndFacadeBindings() throws Exception {
        String optimized = input("optimized");
        String development = input("development");
        assertEquals(input("alias").replace("alias.css.map", "optimized.css.map"), optimized);
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                optimized, new CSSReaderSettings().setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        var classes = classes(sheet);
        assertTrue(classes.contains(compiled("ButtonStyles", "CLASS", "success")), optimized);
        assertFalse(classes.contains(compiled("ButtonStyles", "CLASS", "cancel")), optimized);
        assertFalse(classes.contains(compiled("LivenessEntry$MapStyles", "CLASS", "dead")), optimized);
        assertTrue(development.contains("ButtonStyles--cancel"), development);
        assertTrue(development.contains("MapStyles--dead"), development);
        assertFalse(input("javascript").contains("$react4j$css$"));

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
        String root = "jcss-react4j-css-integration-LivenessEntry_0024_";
        var ids = Map.of(
                "#" + root + "NestingDefaults--id-bound",
                "#" + compiled("LivenessEntry$NestingDefaults", "ID", "bound"),
                "#" + root + "NestingDefaults--id-negative",
                "#" + compiled("LivenessEntry$NestingDefaults", "ID", "negative"));
        for (var rule : developmentRules) {
            for (var selector : rule.getAllSelectors()) {
                for (int index = 0; index < selector.getMemberCount(); index++) {
                    var member = selector.getMemberAtIndex(index);
                    if (member instanceof CSSSelectorSimpleMember simple && ids.containsKey(simple.getValue())) {
                        selector.removeMember(index);
                        selector.addMember(
                                index, new CSSSelectorSimpleMember(Objects.requireNonNull(ids.get(simple.getValue()))));
                    }
                }
            }
        }
        for (int i = 0; i < 5; i++) {
            assertEquals(
                    optimizedRules.get(i).getAsCSSString(settings, 0),
                    developmentRules.get(i).getAsCSSString(settings, 0));
        }
        assertEquals(
                optimizedRules.get(0).getSelectorsAsCSSString(settings, 0),
                ".ordinary,#" + compiled("LivenessEntry$NestingDefaults", "ID", "bound"));
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
                new String[] {"." + compiled("LivenessEntry$MapStyles", "CLASS", "active"), "&.active {"},
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
