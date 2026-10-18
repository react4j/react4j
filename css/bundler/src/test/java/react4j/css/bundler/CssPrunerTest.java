package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssResource;
import react4j.css.internal.SourceMap;

public final class CssPrunerTest {
    private static Resource resource(SourceMap.Writer writer) {
        return new Resource(
                new CssResource("Button", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), Map.of()),
                writer.css(),
                JsonParser.parseString(writer.map("Button.css")).getAsJsonObject());
    }

    @Test
    public void removesImpossibleSelectorsAndPreservesUncertainFunctionsAndSpecificity() {
        var known = Set.of(".root", ".dead", ".peer", "#absent");
        for (var selector : List.of(
                ".root.dead", ".peer>.root", ".root#absent", ":is(.dead,.peer)", ".root:where(.dead,#absent)")) {
            var pruned = CssPruner.prune(
                    resource(new SourceMap.Writer().append(selector + "{color:red;}")), known, Set.of(".root"));
            assertEquals(pruned.css(), "", selector);
        }
        for (var selector : List.of(
                ".root",
                ".root:not(.dead)",
                ".root:is(.dead,.root)",
                ".root:where(.dead,:not(.peer))",
                ".root[data-name='.dead']",
                ".unknown",
                ".dead,.root",
                ".root:nth-child(2n+1)",
                ".root::before")) {
            String css = selector + "{content:'.dead';}";
            assertEquals(
                    CssPruner.prune(resource(new SourceMap.Writer().append(css)), known, Set.of(".root"))
                            .css(),
                    css,
                    selector);
        }
    }

    @Test
    public void nestedDeletionProjectsExactSelectorAndDeclarationOrigins() throws Exception {
        var writer = new SourceMap.Writer()
                .append(".dead{content:'😀';}\n", new SourceMap.Location("View.java", "authored", 3, 12))
                .append("@media screen{\n")
                .append(".root{", new SourceMap.Location("View.java", "authored", 9, 8))
                .append("color:blue;", new SourceMap.Location("View.java", "authored", 10, 10))
                .append("}\n.dead{color:red;}\n}\n");
        var pruned = CssPruner.prune(resource(writer), Set.of(".dead", ".root"), Set.of(".root"));
        assertEquals(pruned.css(), "\n@media screen{\n.root{color:blue;}\n\n}\n");
        var consumer = new SourceMapConsumerV3();
        consumer.parse(pruned.map().toString());
        var selector = Objects.requireNonNull(consumer.getMappingForLine(3, 1));
        assertEquals(selector.getOriginalFile(), "View.java");
        assertEquals(selector.getLineNumber(), 10);
        assertEquals(selector.getColumnPosition(), 9);
        var declaration = Objects.requireNonNull(consumer.getMappingForLine(3, 7));
        assertEquals(declaration.getLineNumber(), 11);
        assertEquals(declaration.getColumnPosition(), 11);
        assertNull(consumer.getMappingForLine(4, 1));
    }
}
