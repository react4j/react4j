package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssResource;
import react4j.css.internal.SourceMap;

public final class CssPrunerTest {
    private static Resource resource(SourceMap.Writer writer) {
        return new Resource(
                new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "",
                        List.of(),
                        List.of(),
                        List.of()),
                writer.css(),
                JsonParser.parseString(writer.map("Button.css")).getAsJsonObject());
    }

    private static Resource prune(Resource resource, Set<String> known, Set<String> present) {
        return new CssPruner(resource, known, present).prune(Set.of());
    }

    @Test
    public void removesImpossibleSelectorsAndPreservesUncertainFunctionsAndSpecificity() {
        var known = Set.of(".root", ".dead", ".peer", "#absent");
        for (var selector : List.of(
                ".root.dead", ".peer>.root", ".root#absent", ":is(.dead,.peer)", ".root:where(.dead,#absent)")) {
            var pruned =
                    prune(resource(new SourceMap.Writer().append(selector + "{color:red;}")), known, Set.of(".root"));
            assertEquals(pruned.css(), "", selector);
        }
        for (var selector : List.of(
                ".root",
                ".root:not(.dead)",
                ".root:is(.dead,.root)",
                ".root:where(.dead,:not(.peer))",
                ".root[data-name='.dead']",
                ".unknown",
                ".root:nth-child(2n+1)",
                ".root::before")) {
            String css = selector + "{content:'.dead';}";
            assertEquals(
                    prune(resource(new SourceMap.Writer().append(css)), known, Set.of(".root"))
                            .css(),
                    css,
                    selector);
        }
    }

    @Test
    public void deletesOnlyImpossibleTopLevelBranchesAndEmptyGroups() {
        for (var selectors : List.of(
                ".dead,.root",
                ".root,.dead",
                ".dead,.peer,.root",
                ".root,.dead,.peer",
                ".dead,.root,.peer",
                ".root,.dead,.root")) {
            String expected = ".root,.dead,.root".equals(selectors) ? ".root,.root" : ".root";
            assertEquals(
                    prune(
                                    resource(new SourceMap.Writer().append(selectors + "{color:blue;}")),
                                    Set.of(".root", ".dead", ".peer"),
                                    Set.of(".root"))
                            .css(),
                    expected + "{color:blue;}");
        }
        for (var css : List.of(
                "@media screen{.dead{color:red;}}", "@supports (display:grid){@media screen{.dead{color:red;}}}")) {
            assertEquals(
                    prune(resource(new SourceMap.Writer().append(css)), Set.of(".dead"), Set.of())
                            .css(),
                    "");
        }
    }

    @Test
    public void nativeNestingPreservesParentMatchingContextsAndMaximumSpecificity() {
        for (var css : List.of(
                ".ordinary,#absent{& .child{color:red;}}.ordinary .child{color:blue;}",
                ".ordinary,#absent{@media screen{& .child{color:red;}}}",
                ".ordinary,#absent{@supports (display:grid){& .child{color:red;}}}",
                "#absent{:not(&){outline-width:11px;}}",
                "#absent{@media screen{:not(&){outline-width:11px;}}}",
                "#absent{@supports (display:grid){:not(&){outline-width:11px;}}}",
                ".ordinary{#absent{:not(&){outline-width:11px;}}}")) {
            assertEquals(
                    prune(resource(new SourceMap.Writer().append(css)), Set.of("#absent"), Set.of())
                            .css(),
                    css);
        }
        assertEquals(
                prune(
                                resource(new SourceMap.Writer()
                                        .append(".ordinary,#absent{&.dead{color:red;}& .child{color:blue;}}")),
                                Set.of("#absent", ".dead"),
                                Set.of())
                        .css(),
                ".ordinary,#absent{& .child{color:blue;}}");
    }

    @Test
    public void literalTabsInStringsAndAttributesUseCharacterOffsets() {
        for (var retained : List.of(".root{content:'\t';}", ".root[data-label='\t']{color:red;}")) {
            String css = retained + ".dead{color:blue;}.root{opacity:0.5;}";
            assertEquals(
                    prune(resource(new SourceMap.Writer().append(css)), Set.of(".root", ".dead"), Set.of(".root"))
                            .css(),
                    retained + ".root{opacity:0.5;}");
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
        var pruned = prune(resource(writer), Set.of(".dead", ".root"), Set.of(".root"));
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
