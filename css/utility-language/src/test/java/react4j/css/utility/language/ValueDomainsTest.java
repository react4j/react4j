package react4j.css.utility.language;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssUtilityBinding;

public final class ValueDomainsTest {
    private static UtilityLanguage.Checked checked(String source) {
        var context = new CssUtilityBinding.Context(
                Set.of("x", "cs1", "cs5", "motion", "shadow", "tracks"),
                Set.of(),
                Set.of(),
                Set.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of("bg", "demo/background.png"),
                Map.of(),
                List.of());
        return UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("values.css", "@utility-rules;\n" + source)),
                "demo.Values",
                new UtilityLanguage.Bindings(Set.of(), context));
    }

    private static UtilityLanguage.Checked typed(String type, String property) {
        return checked(
                "an+b".equals(type)
                        ? "@utility u { color:red; } @variant \"v-{value}\" (value: arbitrary(<an+b>)) {"
                                + " &:nth-child(param(value)) { @slot; } }"
                        : "@utility \"v-{value}\" (value: arbitrary(<" + type + ">)) { " + property
                                + ":param(value); }");
    }

    private static String candidate(String type, String value) {
        return "v-[" + value.replace("_", "\\_").replace(" ", "_") + "]" + ("an+b".equals(type) ? ":u" : "");
    }

    private static void add(List<Object[]> cases, String type, String property, String... values) {
        for (var value : values) {
            cases.add(new Object[] {type, property, value});
        }
    }

    @DataProvider
    public Object[][] validValues() {
        var cases = new ArrayList<Object[]>();
        add(cases, "integer", "z-index", "-2", "+03", "calc(2.5)", "calc(10px / 2px)");
        add(
                cases,
                "number",
                "opacity",
                "-.5",
                "1e2",
                "calc(2 * 3)",
                "min(1,max(0,.5))",
                "clamp(none,.5,none)",
                "calc(pi)",
                "calc(nan)");
        add(cases, "percentage", "width", "25%", "calc(10% + 2%)", "min(10%,20%)");
        add(
                cases,
                "length",
                "left",
                "0",
                "0e2",
                "-0.5em",
                "1cqi",
                "1\\70x",
                "calc(1px * 2)",
                "calc(1px * 1em / 1cm)",
                "min(10px,2rem)",
                "max(0px,var(--x))",
                "clamp(0px,1rem,4rem)",
                "anchor(left)",
                "anchor-size(width)");
        add(cases, "length-percentage", "width", "10%", "calc(100% - 2rem)", "min(10%,20px)", "var(--x)");
        add(cases, "angle", "rotate", ".25turn", "calc(1turn - 10deg)", "max(1deg,1rad)");
        add(cases, "time", "transition-delay", "-.1s", "1ms", "calc(1s / 2)", "var(--motion)");
        add(cases, "ratio", "aspect-ratio", "16/9", "0", "0/0", "var(--x)/2", "1.5 / 2e1", "var(--x)");
        add(cases, "string", "content", "'Save changes'", "\"\\41 \"", "'Hi!{}'", "'param(unused)'");
        add(
                cases,
                "color",
                "color",
                "aliceblue",
                "#1234",
                "currentColor",
                "rgb(var(--cs1))",
                "rgba(var(--cs5), .05)",
                "rgb(1 20% 3 / 50%)",
                "hsl(1turn 20 40%)",
                "hsl(120,30%,20%)",
                "hwb(30deg 20% 10%)",
                "lab(50% 1 -2)",
                "lch(50% 20 30deg)",
                "oklab(.4 .1 -.1)",
                "oklch(40% .15 .5turn)",
                "color(display-p3 1 .5 .2 / .7)",
                "color(xyz-d50 none .1 .2)",
                "color(var(--x) 1 0 0)",
                "rgba(1,2,3)",
                "\\72 gb(1 2 3)",
                "rgb(calc(1 + 2) 0 0)");
        add(
                cases,
                "box-shadow",
                "box-shadow",
                "0 -1px 5px 1px rgba(var(--cs1), .2)",
                "inset 1px 2px red",
                "red 1px 2px",
                "0 0 0 red, 1px 2px 4px blue",
                "var(--shadow)",
                "none");
        add(
                cases,
                "image",
                "background-image",
                "url('asset:bg')",
                "url('https://example.com/a.png')",
                "linear-gradient(90deg,#fff,#000)",
                "linear-gradient(in oklab to right,red,blue)",
                "conic-gradient(in hsl longer hue from 45deg,red,blue)",
                "linear-gradient(to bottom right in oklab,red 0% 10%,blue 100%)",
                "radial-gradient(circle closest-side at right 1rem bottom 2rem,red,blue)",
                "radial-gradient(10px at center,red,blue)",
                "radial-gradient(10% 20%,red,blue)",
                "conic-gradient(from 45deg at center,red 0deg,blue 1turn)",
                "conic-gradient(red 0%,50%,blue 100%)",
                "image-set('asset:bg' 1x type('image/png'),url('https://example.com/a.png') 2x)",
                "image-set(linear-gradient(red,blue) 1x)");
        add(
                cases,
                "position",
                "background-position",
                "center",
                "top",
                "10%",
                "left top",
                "top left",
                "10px 20%",
                "right 1rem bottom 2rem",
                "bottom 2px center",
                "center bottom 2px",
                "left var(--x)");
        add(
                cases,
                "grid-tracks",
                "grid-template-columns",
                "none",
                "200px minmax(0,1fr)",
                "[a] 1fr [b]",
                "[a]1FR[b]",
                "repeat(2,1fr 2fr)",
                "repeat(var(--x),1fr)",
                "repeat(calc(1 + 1),1fr)",
                "repeat(auto-fit,minmax(10rem,1fr))",
                "20px repeat(auto-fill,10px) 20px",
                "subgrid [a] repeat(3,[b]) [c]",
                "subgrid repeat(auto-fill,[a]) []",
                "subgrid",
                "auto min-content fit-content(50%)",
                "var(--tracks)");
        add(cases, "an+b", "", "n+3", "2n-1", "-n+3", "even", "1", "N + 3");
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "validValues")
    public void acceptedTypesExpandThroughTheCheckedFacade(String type, String property, String value) {
        var batch = UtilityLanguage.expand(typed(type, property), List.of(candidate(type, value)));
        assertEquals(batch.diagnostics(), List.of(), type + ": " + value + " " + batch.diagnostics());
        assertEquals(batch.expansions().size(), 1);
        assertTrue(
                batch.expansions().get(0).css().contains("url('asset:")
                        || batch.expansions()
                                .get(0)
                                .css()
                                .contains(
                                        "an+b".equals(type)
                                                ? value.toLowerCase(Locale.ROOT).replace(" ", "")
                                                : value)
                        || value.contains("asset:bg"),
                batch.expansions().get(0).css());
    }

    @DataProvider
    public Object[][] invalidValues() {
        var cases = new ArrayList<Object[]>();
        add(cases, "integer", "z-index", "2.5", "1e2", "1px", "calc(2px)");
        add(
                cases,
                "number",
                "opacity",
                "1px",
                "calc(1px + 1s)",
                "abs(1)",
                "min()",
                "clamp(0,1)",
                "calc(1 2)",
                "calc(var(--x) + 1px + 1s)");
        add(cases, "percentage", "width", "0", "2", "calc(1% + 2px)");
        add(
                cases,
                "length",
                "left",
                "2",
                "1furlong",
                "calc(0 + 1px)",
                "calc(1px * 1s)",
                "min(1px,1s)",
                "calc(1rem + 10%)");
        add(cases, "length-percentage", "width", "1s", "calc(1px + 1deg)", "round(1px)");
        add(cases, "angle", "rotate", "1", "1px", "calc(1deg + 1s)");
        add(cases, "time", "transition-delay", "0", "20", "1deg");
        add(cases, "ratio", "aspect-ratio", "-1/2", "1/-2", "1/2/3", "1px/2px", "calc(2)");
        add(cases, "string", "content", "hello", "'a' 'b'", "1px");
        add(
                cases,
                "color",
                "color",
                "12px",
                "#fffff",
                "unknown",
                "rgb(1,20%,3)",
                "rgb(1 2)",
                "rgb(1 2 3 4)",
                "rgb(1px 2 3)",
                "var(\"x\")",
                "var(--x --y)",
                "var(--)",
                "rgb(1 2 3 / 1s)",
                "hsl(20,10,20)",
                "hwb(1,2%,3%)",
                "color(unknown 0 0 0)",
                "rgb(calc(.5 + 10%) 0 0)",
                "rgb(foo(1) 0 0)");
        add(
                cases,
                "box-shadow",
                "box-shadow",
                "1px",
                "0 0 -1px red",
                "0 0 1px 2px 3px red",
                "0 0 red blue",
                "inset inset 0 0",
                "1px red 2px",
                "0 0 1deg red",
                "red,blue");
        add(
                cases,
                "image",
                "background-image",
                "none",
                "red",
                "linear-gradient(red)",
                "linear-gradient(1px,red,blue)",
                "linear-gradient(to top bottom,red,blue)",
                "linear-gradient(red 1s,blue)",
                "linear-gradient(red,10%,20%,blue)",
                "conic-gradient(red 1px,blue)",
                "radial-gradient(circle 10%,red,blue)",
                "image-set(image-set('asset:bg') 1x)",
                "image-set('asset:bg' -1x)",
                "image-set('asset:bg' 1x 2x)",
                "image-set('asset:bg' 1fr)");
        add(
                cases,
                "position",
                "background-position",
                "left right",
                "top bottom",
                "top 1px",
                "center 1px bottom",
                "left 1px right 1px",
                "left top bottom",
                "1px 2px 3px",
                "1deg",
                "left top right bottom center");
        add(
                cases,
                "grid-tracks",
                "grid-template-columns",
                "-1px",
                "-1fr",
                "minmax(1fr,2fr)",
                "fit-content(-1px)",
                "minmax(1px)",
                "repeat(0,1fr)",
                "repeat(2,repeat(2,1fr))",
                "repeat(auto-fit,1fr)",
                "repeat(auto-fit,10px) 1fr",
                "[auto] 1fr",
                "[span] 1fr",
                "subgrid 1fr",
                "subgrid repeat(auto-fit,[a])",
                "repeat(2,none)");
        add(cases, "an+b", "", "var(--x)", "2n of .a", "n+-1", "1.5", "n+3);color:red");
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidValues")
    public void knownWrongStaticValuesDoNotPassAsBrowserUncertainty(String type, String property, String value) {
        var batch = UtilityLanguage.expand(typed(type, property), List.of(candidate(type, value)));
        assertEquals(batch.expansions().size(), 0, type + ": " + value);
        assertEquals(batch.diagnostics().size(), 1);
        assertTrue(
                Set.of("U_VALUE_TYPE", "U_CANDIDATE_SYNTAX")
                        .contains(batch.diagnostics().get(0).code()),
                batch.diagnostics().toString());
    }

    @Test
    public void scalarSourcesBoundsAndDeferredChecksRemainExplicit() {
        var library = checked("""
            @scale duration 1ms;
            @scale rotation 1deg;
            @utility "duration-{v}" (v: scale(duration) | arbitrary(<time>) within [0ms, *]) { transition-duration:param(v); }
            @utility "opacity-{v}" (v: bare(<number>) | arbitrary(<number>) within [0, 1]) { opacity:param(v); }
            @utility "rotate-{v}" (v: scale(rotation) | arbitrary(<angle>) within [0deg, 1turn]) { rotate:param(v); }
            @utility "width-{v}" (v: arbitrary(<length>) within [1in, 2in]) { width:param(v); }
            @utility "ratio-{v}" (v: bare(<ratio>)) { aspect-ratio:param(v); }
            @utility "z-{v}" (v: bare(<integer>)) { z-index:param(v); }
            """);
        var valid = UtilityLanguage.expand(
                library,
                List.of(
                        "duration-150",
                        "duration-0.5",
                        "opacity-0.7",
                        "rotate-45",
                        "width-[96px]",
                        "width-[50.8mm]",
                        "ratio-16/9",
                        "z-+2",
                        "duration-[var(--motion)]",
                        "width-[2rem]",
                        "rotate-[1rad]"));
        assertEquals(valid.diagnostics(), List.of());
        assertTrue(valid.expansions().stream().anyMatch(value -> value.css().contains("0.5ms")));
        var dynamic = valid.expansions().stream()
                .filter(value -> value.candidate().contains("var("))
                .findFirst()
                .orElseThrow();
        assertEquals(dynamic.trace().get(0).checks().get(0).status(), "browser-dependent");
        assertEquals(dynamic.trace().get(0).checks().get(0).interval(), "[0ms, *]");
        var bounded = valid.expansions().stream()
                .filter(value -> "width-[96px]".equals(value.candidate()))
                .findFirst()
                .orElseThrow();
        assertEquals(bounded.trace().get(0).checks().get(0).status(), "checked");
        var invalid = UtilityLanguage.expand(
                library,
                List.of(
                        "opacity-1.5",
                        "duration-[-1ms]",
                        "width-[95px]",
                        "rotate-[8rad]",
                        "duration-.5",
                        "duration-1e2"));
        assertEquals(invalid.expansions().size(), 0);
        assertEquals(
                invalid.diagnostics().stream()
                        .map(UtilityLanguage.Diagnostic::code)
                        .toList(),
                List.of(
                        "U_VALUE_RANGE",
                        "U_VALUE_RANGE",
                        "U_VALUE_RANGE",
                        "U_VALUE_RANGE",
                        "U_ARGUMENT",
                        "U_ARGUMENT"));
    }

    @Test
    public void invalidIntervalsAndSourceAmbiguityFailBeforeAnyCandidateIsRequested() {
        for (var interval : List.of("[*, *]", "[1, 0]", "[0px, 1]", "[var(--x), 1]", "[0, 1, 2]", "[0, 1ms]")) {
            var error = expectThrows(
                    UtilityLanguage.Failure.class,
                    () -> checked(
                            "@utility \"x-{v}\" (v: bare(<number>) within " + interval + ") {opacity:param(v);}"));
            assertEquals(error.diagnostic().code(), "U_RANGE_BOUND");
        }
        var token = expectThrows(
                UtilityLanguage.Failure.class,
                () -> checked("@tokens values <number> { bad:2; } @utility \"x-{v}\" (v: tokens(values) within [0,1])"
                        + " {opacity:param(v);}"));
        assertEquals(token.diagnostic().code(), "U_VALUE_RANGE");
        var overlap = expectThrows(
                UtilityLanguage.Failure.class,
                () -> checked("@scale n 1; @utility \"x-{v}\" (v: scale(n) | bare(<number>) within [0,0])"
                        + " {opacity:param(v);}"));
        assertEquals(overlap.diagnostic().code(), "U_AMBIGUOUS_RULE");
    }

    @Test
    public void candidateDependentUrlAndNameChecksDoNotInventTemplateReferences() {
        var library = checked("@keyframes pulse {to{opacity:1;}} @utility \"url-{v}\" (v: arbitrary(<string>))"
                + " {background-image:url(param(v));} @utility \"animation-{v}\" (v: arbitrary(<string>))"
                + " {animation-name:param(v);}");
        var valid = UtilityLanguage.expand(library, List.of("url-['asset:bg']", "animation-['pulse']"));
        assertEquals(valid.diagnostics(), List.of());
        assertFalse(library.json().contains("react4j-utility-"));
        for (var value : valid.expansions()) {
            assertFalse(value.css().contains("react4j-utility-"));
            assertFalse(value.metadata().json().contains("react4j-utility-"));
        }
        assertTrue(valid.expansions().stream().anyMatch(value -> value.css().contains("asset:demo/background.png")));
        assertTrue(valid.expansions().stream().anyMatch(value -> value.css().contains("--keyframes-pulse")));
        var invalid = UtilityLanguage.expand(library, List.of("url-['local.png']", "animation-['missing']"));
        assertEquals(invalid.expansions().size(), 0);
        assertEquals(invalid.diagnostics().size(), 2);
        var unused = expectThrows(
                UtilityLanguage.Failure.class,
                () -> checked("@tokens paths <string> {bad:'local.png';} @utility \"url-{v}\" (v: tokens(paths))"
                        + " {background-image:url(param(v));}"));
        assertEquals(unused.diagnostic().code(), "U_ASSET_REFERENCE");
    }

    @Test
    public void typedHolesFollowCssEscapesAndCommentsWithoutBecomingStringInterpolation() {
        var library = checked(
                "@utility \"x-{v}\" (v: arbitrary(<length>)) { width:p\\61ram(/* value */v); content:'param(v)'; }"
                        + " @utility u {color:red;} @variant \"nth-{v}\" (v: arbitrary(<an+b>)) { &:nth-child( param(/*"
                        + " value */v) ) {@slot;} }");
        var values = UtilityLanguage.expand(library, List.of("x-[1rem]", "nth-[n+3]:u"));
        assertEquals(values.diagnostics(), List.of());
        assertTrue(values.expansions().get(0).css().contains("width:1rem"));
        assertTrue(values.expansions().get(0).css().contains("'param(v)'"));
        var bad = UtilityLanguage.expand(typed("image", "background-image"), List.of("v-[url('local.png')]"));
        assertEquals(bad.diagnostics().get(0).code(), "U_ASSET_REFERENCE");
    }
}
