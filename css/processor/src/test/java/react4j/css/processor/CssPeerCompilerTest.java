package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.writer.CSSWriterSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssPeerCompilerTest {
    private static final Map<String, CssCompiler.Import> IMPORTS = Map.of(
            "label", new CssCompiler.Import("demo.Label", Kind.COMPONENT, false),
            "other", new CssCompiler.Import("demo.Outer$Other", Kind.COMPONENT, false),
            "beat", new CssCompiler.Import("demo.Beat", Kind.KEYFRAMES, false));

    private static CssCompiler.Result component(String css) {
        return CssCompiler.component(
                css,
                "jcss-demo-Button",
                new CssCompiler.Exports(List.of("style"), Set.of(), Set.of(), Set.of(), Set.of()),
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                IMPORTS,
                Map.of());
    }

    private static CssCompiler.Result global(String css) {
        return CssCompiler.global(
                css,
                "jcss-demo-Global",
                new CssCompiler.Exports(List.of(), Set.of(), Set.of(), Set.of(), Set.of()),
                offset -> new SourceMap.Location("Global.java", css, 0, offset),
                IMPORTS,
                Map.of());
    }

    @Test
    public void everyCombinatorResolvesQualifiedPeerRoots() {
        for (var combinator : List.of(" ", " > ", " + ", " ~ ")) {
            String css = "&:hover" + combinator + ":CSS(label)[data-name=':css(other)']:not(:disabled):where(:focus)"
                    + " > :css(other)::before { content: ':css(beat)'; }";
            var result = component(css);
            assertEquals(result.imports(), Set.of("label", "other"));
            var sheet = Objects.requireNonNull(
                    CSSReader.readFromStringReader(result.output().css(), new CSSReaderSettings()));
            var rule = sheet.getAllStyleRules().get(0);
            assertEquals(
                    rule.getSelectorsAsCSSString(new CSSWriterSettings(true), 0),
                    ".jcss-demo-Button:hover" + combinator.trim() + (combinator.isBlank() ? " " : "")
                            + ".jcss-demo-Label[data-name=':css(other)']:not(:disabled):where(:focus)>.jcss-demo-Outer_0024_Other::before");
            var value = rule.getAllDeclarations()
                    .get(0)
                    .getExpression()
                    .getAllMembers()
                    .get(0);
            assertEquals(((CSSExpressionMemberTermSimple) value).getValue(), "':css(beat)'");
        }
    }

    @Test
    public void nestedPeersAndConditionsPreserveParentSpecificityAndOwnership() {
        String css = "@media screen { &:hover, &:focus { @supports (display: grid) { & + :css(label) {"
                + " &:is(:hover,:focus) { color: red; } & > :css(other) { color: blue; } } } } }";
        var result = component(css);
        assertEquals(result.imports(), Set.of("label", "other"));
        assertTrue(
                result.output()
                        .css()
                        .contains(
                                ":is(.jcss-demo-Button:hover,.jcss-demo-Button:focus)+.jcss-demo-Label:is(:hover,:focus){color:red;}"),
                result.output().css());
        assertTrue(
                result.output().css().contains("+.jcss-demo-Label>.jcss-demo-Outer_0024_Other{color:blue;}"),
                result.output().css());
        assertEquals(component("& + :css(label) {}").imports(), Set.of("label"));
    }

    @DataProvider
    public Object[][] invalidSelectors() {
        return List.of(
                        ":css(label)",
                        ".raw :css(label)",
                        "& :hover",
                        "& span",
                        "& .raw",
                        "& #id",
                        "& *",
                        "& > &",
                        "& + [data-x]",
                        "& ~ :is(:css(label))",
                        "& > :not(:css(label))",
                        "& + :css(label).local",
                        "& + :css(label):is(.local)",
                        "&:css(label)",
                        "&:is(:css(label))",
                        "&:not(:css(label))",
                        "&:where(:css(label))",
                        "&:not(:is(:css(label)))",
                        "& + :css(label):is(:css(other))",
                        "& + :css(missing)",
                        "& + :css(Label)",
                        "& + :css(beat)",
                        "& + :css()",
                        "& + :css('label')",
                        "& + :css(\"label\")",
                        "& + :css(label, other)",
                        "& + :css(label other)",
                        "& + :css(label.local)",
                        "& + :css(label:hover)",
                        "& + :css(label > other)",
                        "& + :css(var(--peer))",
                        "& + :css(1)",
                        "& + :css(lab\\65l)",
                        "& + :css(label):css(other)",
                        "& + :css(label)&",
                        "& + :css(label) .raw",
                        "& + :css(label) + .raw",
                        "&:css",
                        "& + :css",
                        "&:CSS()",
                        "& > :c\\73s(label)")
                .stream()
                .map(selector -> new Object[] {selector + " { color: red; }"})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidSelectors")
    public void malformedOrUnownedPeerSelectorsFail(String css) {
        expectThrows(CssSyntax.InvalidCss.class, () -> component(css));
    }

    @Test
    public void peerNestingCannotHideOwnerModifiersEvenInMixedSelectorLists() {
        for (var parent : List.of("& + :css(label)", "&, & + :css(label)")) {
            for (var child : List.of("&.active", "&:is(.active)", "&:not(.active)", "&:where(.active)")) {
                String css = parent + " { @media screen { " + child + " { color: red; } } }";
                var error = expectThrows(
                        CssSyntax.InvalidCss.class,
                        () -> CssCompiler.component(
                                css,
                                "jcss-demo-Button",
                                new CssCompiler.Exports(
                                        List.of("style", "active"), Set.of(), Set.of(), Set.of(), Set.of()),
                                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                                IMPORTS,
                                Map.of()));
                assertEquals(error.getMessage(), "Local modifiers cannot qualify imported peers");
            }
        }
    }

    @Test
    public void globalsResolvePeersInsideSupportedSelectorFunctions() {
        for (var selector : List.of(
                "body :css(label)",
                ":is(:css(label), .raw)",
                ":not(:css(label))",
                ":where(:css(label))",
                "body:has(> :css(label))",
                ":host(:css(label))",
                ":host-context(:css(label))",
                "::slotted(:css(label))")) {
            var result = global(selector + " { color: red; }");
            assertEquals(result.imports(), Set.of("label"));
            assertTrue(
                    result.output().css().contains(".jcss-demo-Label"),
                    result.output().css());
            assertFalse(result.output().css().contains(":css("));
        }
        var result = global("body { & > :css(label) { animation-name: beat; } }");
        assertEquals(result.imports(), Set.of("label", "beat"));
        assertTrue(result.output().css().contains("&>.jcss-demo-Label{animation-name:jcss-demo-Beat--keyframes;}"));
        var unused = global("[data-text=':css(label)'] { content: ':css(label)'; --peer: ':css(label)'; }");
        assertTrue(unused.imports().isEmpty());
    }

    @Test
    public void globalsCheckReferenceArgumentsAndKinds() {
        for (var selector : List.of(
                ":css",
                ":css()",
                ":css(missing)",
                ":css(beat)",
                ":css('label')",
                ":css(label,other)",
                ":css(label other)",
                ":is(:css(missing))",
                ":has(:css(beat))",
                ":c\\73s(label)",
                ".lab\\65l",
                ":nth-child(2 of :css(label))",
                ":custom(:css(label))")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> global(selector + " { color: red; }"));
        }
    }

    @Test
    public void selectorsMapToTheAuthoredRuleAndImportsUseStableEncodedOwners() throws Exception {
        String css = "color: blue; & + :css(other) { color: red; }";
        var result = component(css);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(result.output().map("Button.css"));
        int generated = result.output().css().indexOf(".jcss-demo-Button+");
        var origin = Objects.requireNonNull(consumer.getMappingForLine(1, generated + 1));
        assertEquals(origin.getOriginalFile(), "Button.java");
        assertEquals(origin.getColumnPosition(), css.indexOf('&') + 1);
        assertEquals(origin.getLineNumber(), 1);
        for (var alias : List.of("--peer", "éclair", "none", "INITIAL")) {
            String source = "& > :css(" + alias + ") { color: red; }";
            var imported = CssCompiler.component(
                    source,
                    "root",
                    new CssCompiler.Exports(List.of("style"), Set.of(), Set.of(), Set.of(), Set.of()),
                    offset -> new SourceMap.Location("Button.java", source, 0, offset),
                    Map.of(alias, new CssCompiler.Import("demo.Label", Kind.COMPONENT, false)),
                    Map.of());
            assertEquals(imported.imports(), Set.of(alias));
        }
    }
}
