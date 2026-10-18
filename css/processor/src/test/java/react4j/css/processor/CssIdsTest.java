package react4j.css.processor;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.writer.CSSWriterSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssIdsTest {
    private static final String ROOT = "jcss-demo-Button";
    private static final CssCompiler.Exports EXPORTS =
            new CssCompiler.Exports(List.of("style"), Set.of("mask", "control"), Set.of());

    private static CssCompiler.Result component(String css) {
        return CssCompiler.component(
                css,
                ROOT,
                EXPORTS,
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                Map.of(),
                Map.of());
    }

    private static CssCompiler.Result global(String css) {
        return CssCompiler.global(
                css,
                ROOT,
                EXPORTS,
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                Map.of(),
                Map.of());
    }

    @Test
    public void boundHashesAndPureFragmentsAgreeAndOtherValuesStayLiteral() {
        var result =
                component("&#mask, &:is(#control, :not(#mask)) { filter: var(--filter,url('#mask')); content:'#mask';"
                        + " background: url(icons.svg#mask); color: #fff; }");
        assertEquals(result.ids(), Map.of("mask", ROOT + "--id-mask", "control", ROOT + "--id-control"));
        var style = Objects.requireNonNull(
                        CSSReader.readFromStringReader(result.output().css(), new CSSReaderSettings()))
                .getAllStyleRules()
                .get(0);
        assertEquals(
                ((CSSSelectorSimpleMember)
                                style.getAllSelectors().get(0).getAllMembers().get(1))
                        .getValue(),
                "#" + ROOT + "--id-mask");
        var is = (CSSSelectorMemberPseudoIs)
                style.getAllSelectors().get(1).getAllMembers().get(1);
        assertEquals(
                ((CSSSelectorSimpleMember)
                                is.getAllSelectors().get(0).getAllMembers().get(0))
                        .getValue(),
                "#" + ROOT + "--id-control");
        for (var entry : Map.of(
                        "filter",
                        "var(--" + ROOT + "--filter,url(#" + ROOT + "--id-mask))",
                        "content",
                        "'#mask'",
                        "background",
                        "url(icons.svg#mask)",
                        "color",
                        "#fff")
                .entrySet()) {
            assertEquals(
                    Objects.requireNonNull(style.getDeclarationOfPropertyName(entry.getKey()))
                            .getExpression()
                            .getAsCSSString(new CSSWriterSettings(true), 0),
                    entry.getValue());
        }
    }

    @Test
    public void fragmentsWorkInEveryDeclarationContextAndNormalizeQuotes() {
        for (var uri : List.of(
                "url(#mask)",
                "url('#mask')",
                "url(\"#mask\")",
                "URL( #mask )",
                "var(--filter,url(#mask))",
                "image-set(url(#mask) 1x)")) {
            assertEquals(component("filter:" + uri + ";").ids(), Map.of("mask", ROOT + "--id-mask"));
        }
        assertEquals(
                component("@keyframes pulse {to {filter:url(#mask);}} @supports (filter:url(#control)) { &:hover"
                                + " { color: red; } }")
                        .ids(),
                Map.of("mask", ROOT + "--id-mask", "control", ROOT + "--id-control"));
        assertEquals(
                CssCompiler.keyframes(
                                "to {filter:url(#mask);}",
                                ROOT,
                                EXPORTS,
                                offset -> new SourceMap.Location("Frames.java", "", 0, offset),
                                Map.of())
                        .ids(),
                Map.of("mask", ROOT + "--id-mask"));
        assertTrue(global("#unbound { content:'#mask'; background:url(icons.svg#mask); }")
                .ids()
                .isEmpty());
    }

    @Test
    public void globalSelectorFunctionsAndNestingFindIdsWithoutChangingUnboundNames() {
        for (var function : List.of(
                ":not(#mask)",
                ":is(#mask)",
                ":where(#mask)",
                ":has(> #mask)",
                ":host(#mask)",
                ":host-context(#mask)",
                "::slotted(#mask)")) {
            var result = global("body " + function + " { color:red; }");
            assertEquals(result.ids(), Map.of("mask", ROOT + "--id-mask"), function);
            assertTrue(
                    result.output().css().contains("#" + ROOT + "--id-mask"),
                    result.output().css());
        }
        assertEquals(
                global("body { & #mask { color:red; } } #base { color:blue; }").ids(),
                Map.of("mask", ROOT + "--id-mask"));
        assertTrue(global("[data-label='mask'][href='icons.svg#mask'][id=other][id] {content:'#mask';} :lang(en)"
                        + " {color:red;}")
                .ids()
                .isEmpty());
    }

    @Test
    public void knownAttributeReferencesFailInsteadOfSilentlyMissingTheirTarget() {
        for (var attribute : List.of(
                "[id=mask]",
                "[ID='mask']",
                "[href='#mask']",
                "[href=\"#mask\" i]",
                "[src='#mask']",
                "[for=mask]",
                "[aria-labelledby='other mask']",
                "[aria-controls~=mask]",
                "[id^=mask]",
                "[id$=mask]",
                "[id*=mask]",
                "[id|=mask]",
                "[id=MASK i]",
                "[href=\"#MASK\" i]",
                "[id^=ma]",
                "[id$=ask]",
                "[href*=mask]")) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> global(attribute + " {color:red;}"));
            assertTrue(String.valueOf(error.getMessage()).contains("Attribute selectors"), error.getMessage());
        }
        expectThrows(CssSyntax.InvalidCss.class, () -> global("[id='ma\\73k'] {color:red;}"));
        for (var attribute : List.of(
                "[id^=other]",
                "[id$=other]",
                "[id*=other]",
                "[id~='other']",
                "[id|=other]",
                "[id=MASK s]",
                "[id^='']",
                "[id$='']",
                "[id*='']")) {
            assertTrue(global(attribute + " {color:red;}").ids().isEmpty(), attribute);
        }
        for (var attribute : List.of("[href~='#mask']", "[href|='#mask']", "[href^='#m']", "[href$=ask]")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> global(attribute + " {color:red;}"));
        }
        var unknownFunction = expectThrows(CssSyntax.InvalidCss.class, () -> global(":custom(#mask) {color:red;}"));
        assertTrue(String.valueOf(unknownFunction.getMessage()).contains("selector function"));
        expectThrows(CssSyntax.InvalidCss.class, () -> component("& #mask {color:red;}"));
        expectThrows(CssSyntax.InvalidCss.class, () -> component("&#unbound {color:red;}"));
    }
}
