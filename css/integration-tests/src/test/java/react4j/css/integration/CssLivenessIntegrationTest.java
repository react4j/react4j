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
    public void utilityListsActivateTheProviderAndCompressEveryTokenInDeclaredOrder() throws Exception {
        String css = input("optimized");
        String js = input("javascript");
        var values = List.of(
                compiled("UtilityLibrary", "CLASS", "gap-2"),
                compiled("UtilityLibrary", "CLASS", "flex"),
                compiled("UtilityLibrary", "CLASS", "text-red"),
                compiled("LivenessEntry$UtilityBindings", "CLASS", ""),
                compiled("LivenessEntry$UtilityBindings", "CLASS", "active"));
        assertTrue(js.contains("\"" + String.join(" ", values) + "\""), js);
        for (var value : values.subList(0, 3)) {
            assertTrue(css.matches("(?s).*\\." + value + "\\s*\\{.*"), css);
        }
        assertFalse(css.contains("outline-width:37px"), css);
        assertTrue(input("development").contains("outline-width:37px"));
        assertFalse(js.contains("$react4j$css$"), js);
    }

    @Test
    public void independentNativeContributionsDeduplicateOrderAndPruneWithSurvivingGetters() throws Exception {
        String optimized = input("optimized");
        String development = input("development");
        assertEquals(optimized.split("gap: 34.25rem", -1).length - 1, 1, optimized);
        assertEquals(development.split("gap: 34.25rem", -1).length - 1, 1, development);
        assertTrue(development.contains("gap: 249.75rem"), development);
        assertFalse(optimized.contains("gap: 249.75rem"), optimized);
        assertFalse(development.contains("gap: 250rem"), development);
        assertTrue(optimized.indexOf("gap: 34.25rem") < optimized.indexOf("gap: 0.5rem"), optimized);
        String shared = compiled("NativeUtilityLibrary", "CLASS", "gap-137");
        assertTrue(optimized.contains("." + shared + " {"), optimized);
        assertTrue(input("javascript").contains(shared), input("javascript"));
        String importedAnimation = compiled("SharedPulse", "ANIMATION", "");
        String ownedAnimation = compiled("NativeUtilityLibrary", "ANIMATION", "native-pulse");
        assertEquals(optimized.split("@keyframes " + importedAnimation + "\\{", -1).length - 1, 1, optimized);
        assertEquals(optimized.split("@keyframes " + ownedAnimation + " ", -1).length - 1, 1, optimized);
        assertTrue(optimized.contains("animation: " + importedAnimation + " 1s, " + ownedAnimation + " 1s"), optimized);
        assertTrue(optimized.contains("var(" + compiled("SharedDefaults", "PROPERTY", "accent") + ")"), optimized);
        var map = JsonParser.parseString(input("map")).getAsJsonObject();
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map.toString());
        int at = optimized.indexOf("gap: 34.25rem");
        int line =
                (int) optimized.substring(0, at).chars().filter(c -> '\n' == c).count() + 1;
        int column = at - optimized.lastIndexOf('\n', at - 1);
        var origin = Objects.requireNonNull(consumer.getMappingForLine(line, column));
        assertTrue(origin.getOriginalFile().endsWith("native.utilities.css"), origin.toString());
    }

    @Test
    public void nativeConditionsCompressOwnedNamesWithoutRetainingSetup() throws Exception {
        String optimized = input("optimized");
        String container = compiled("NativeUtilityLibrary", "CONTAINER", "card");
        String property = compiled("NativeUtilityLibrary", "PROPERTY", "density");
        assertTrue(optimized.contains("@container " + container + " (0px <= inline-size < 30rem)"), optimized);
        assertTrue(optimized.contains("@container " + container + " style(" + property + ": compact)"), optimized);
        assertTrue(optimized.contains("@starting-style"), optimized);
        assertTrue(optimized.contains("font-weight: 650"), optimized);
        assertFalse(optimized.contains("987px"), optimized);
        assertFalse(input("development").contains("987px"));
    }

    @Test
    public void anchorReferencesCompressWithoutRetainingTheirSetup() throws Exception {
        String css = input("optimized");
        String anchor = compiled("NativeUtilityLibrary", "ANCHOR", "--trigger");
        assertTrue(anchor.startsWith("--"), anchor);
        for (String expected : List.of(
                "position-anchor: " + anchor,
                "anchor-scope: " + anchor,
                "anchor(" + anchor + " left",
                "anchor-size(" + anchor + " width")) {
            assertTrue(css.contains(expected), css);
        }
        assertFalse(css.contains("997px"), css);
        assertFalse(input("development").contains("997px"));
        var consumer = new SourceMapConsumerV3();
        consumer.parse(input("map"));
        int at = css.indexOf("anchor(" + anchor);
        int line = (int) css.substring(0, at).chars().filter(c -> '\n' == c).count() + 1;
        int column = at - css.lastIndexOf('\n', at - 1);
        assertTrue(Objects.requireNonNull(consumer.getMappingForLine(line, column))
                .getOriginalFile()
                .endsWith("native.utilities.css"));
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
    public void optimizedJavascriptMapsInlinedStyleValuesToCompleteGeneratedBindingJava() throws Exception {
        var document = JsonParser.parseString(input("javascript_map")).getAsJsonObject();
        var consumer = new SourceMapConsumerV3();
        consumer.parse(document.toString());
        String js = input("javascript");
        for (var owner : List.of("ButtonStyles", "LivenessEntry$MapStyles")) {
            String value = compiled(owner, "CLASS", "ButtonStyles".equals(owner) ? "success" : "active");
            String literal = "\"" + compiled(owner, "CLASS", "") + " " + value + "\"";
            int at = js.indexOf(literal);
            assertTrue(at >= 0, literal + " in " + js);
            at++;
            int line = (int) js.substring(0, at).chars().filter(c -> '\n' == c).count() + 1;
            int column = at - js.lastIndexOf('\n', at);
            var origin = Objects.requireNonNull(consumer.getMappingForLine(line, column));
            var sources = document.getAsJsonArray("sources");
            int found = -1;
            for (int i = 0; i < sources.size(); i++) {
                if (sources.get(i).getAsString().equals(origin.getOriginalFile())) {
                    found = i;
                }
            }
            assertTrue(found >= 0, origin.toString());
            String java = document.getAsJsonArray("sourcesContent").get(found).getAsString();
            assertTrue(origin.getOriginalFile().endsWith(".java"), origin.toString());
            assertTrue(java.contains("@Generated(\"react4j.css.processor.CssProcessor\")"), java);
            assertTrue(java.contains("return \"jcss-react4j-css-integration-"), java);
            String treeProperty = "ButtonStyles".equals(owner) ? "css.tree" : "css.liveness.binding_tree";
            var tree = Path.of(Objects.requireNonNull(System.getProperty(treeProperty)));
            String name =
                    origin.getOriginalFile().substring(origin.getOriginalFile().lastIndexOf('/') + 1);
            assertEquals(
                    java,
                    Files.readString(tree.resolve("react4j/css/integration").resolve(name)));
            String originalLine = java.lines().toList().get(origin.getLineNumber() - 1);
            assertTrue(originalLine.contains("return"), originalLine);
        }
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
