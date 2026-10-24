package react4j.css.utility.language;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssUtilityBinding;

public final class ConditionalVariantsTest {
    private static final String RULES = """
        @utility-rules;
        @tokens thresholds <length> { small:24rem; }
        @utility setup { container: card / inline-size; --density:compact; }
        @utility red { color:red; }
        @variant "wide-{v}" (v: tokens(thresholds) | arbitrary(<length>)) {
          @container card (inline-size >= param(v)) { @slot; }
        }
        @variant "aspect-{v}" (v: bare(<ratio>)) { @container (aspect-ratio > param(v)) { @slot; } }
        @variant compact { @container card style(--density: compact) { @slot; } }
        @variant starting { @starting-style { @slot; } }
        @variant hover { &:hover { @slot; } }
        """;

    private static UtilityLanguage.Checked checked(String rules) {
        return UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("conditions.utilities.css", rules)),
                "demo.Queries",
                new UtilityLanguage.Bindings(
                        Set.of(),
                        new CssUtilityBinding.Context(
                                Set.of("host-density"),
                                Set.of(),
                                Set.of(),
                                Set.of("host"),
                                Set.of(),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                List.of())));
    }

    @Test
    public void preservesWrapperOrderAndNamesWithoutInstantiatingSetup() {
        var library = checked(RULES + """
            @variant host { @container host style(--host-density: compact) { @slot; } }
            """);
        var batch = UtilityLanguage.expand(
                library, List.of("wide-small:compact:starting:hover:red", "aspect-16/9:red", "host:red"));
        assertEquals(batch.diagnostics(), List.of());
        var css =
                batch.expansions().stream().map(UtilityLanguage.Expansion::css).reduce("", String::concat);
        assertTrue(css.contains("@container jcss-demo-Queries--container-card (inline-size >= 24rem)"), css);
        assertTrue(css.contains("style(--jcss-demo-Queries--density: compact)"), css);
        assertTrue(css.indexOf("(inline-size >= 24rem)") < css.indexOf("style(--jcss-demo-Queries--density"), css);
        assertTrue(css.indexOf("style(--jcss-demo-Queries--density") < css.indexOf("@starting-style"), css);
        assertTrue(css.contains(":hover"), css);
        assertTrue(css.contains("aspect-ratio > 16/9"), css);
        assertTrue(css.contains("@container host style(--host-density: compact)"), css);
        assertFalse(css.contains("container:"), css);
        assertFalse(css.contains("--density:compact;"), css);
        assertTrue(batch.expansions().get(0).sourceMap().contains("conditions.utilities.css"));
        assertEquals(
                UtilityLanguage.expand(library, List.of("wide-[30%]:red"))
                        .diagnostics()
                        .get(0)
                        .code(),
                "U_VALUE_TYPE");
    }

    @DataProvider
    public Object[][] conditions() {
        return new Object[][] {
            {"(width)", true},
            {"(min-width:24rem)", true},
            {"(orientation:portrait)", true},
            {"(40rem > inline-size >= 24rem)", true},
            {"(24rem <= width < 40rem)", true},
            {"not (width < 20px)", true},
            {"(width > 20px) and (height >= 10px)", true},
            {"((width > 20px) or (height >= 10px)) and style(--density)", true},
            {"style((--density: compact) or (--density: comfortable))", true},
            {"style(not (--density: compact))", true},
            {"scroll-state(stuck: top)", true},
            {"scroll-state(snapped: both)", true},
            {"scroll-state(scrollable: inline)", true},
            {"scroll-state(scrolled: bottom)", true},
            {"scroll-state((stuck: top) and (stuck: left))", true},
            {"scroll-state(not (snapped))", true},
            {"(width > 30%)", false},
            {"(width > 30)", false},
            {"(aspect-ratio:3px)", false},
            {"(orientation > portrait)", false},
            {"(min-orientation:portrait)", false},
            {"(width >> 3px)", false},
            {"(width => 3px)", false},
            {"(3px < width > 4px)", false},
            {"(3px < width < 4px < 5px)", false},
            {"(inline-size 3px)", false},
            {"(unknown:3px)", false},
            {"(min-width)", false},
            {"style(color:red)", false},
            {"style(--density:)", false},
            {"style(--density:compact; color:red)", false},
            {"scroll-state(snapped: top)", false},
            {"scroll-state(stuck: both)", false},
            {"scroll-state(scrolled: both)", false},
            {"scroll-state(unknown: x)", false},
            {"unknown(foo)", false},
            {"(width) and (height) or (width)", false},
            {"not (width) and (height)", false},
            {"(width) and", false},
            {"card", false},
            {"card (width), host (width)", false},
            {"(width) garbage (height)", false},
        };
    }

    @Test(dataProvider = "conditions")
    public void checksLiteralConditions(String condition, boolean valid) {
        String rules = RULES + "@variant test { @container " + condition + " { @slot; } }";
        if (valid) {
            var batch = UtilityLanguage.expand(checked(rules), List.of("test:red"));
            assertEquals(batch.diagnostics(), List.of(), condition);
        } else {
            var failure = expectThrows(UtilityLanguage.Failure.class, () -> checked(rules));
            assertEquals(failure.diagnostic().code(), "U_CONTAINER_CONDITION", condition);
        }
    }

    @Test
    public void allScrollStateValuesAndMixedWrappersRemainLiteral() {
        for (String feature : List.of("stuck", "snapped", "scrollable", "scrolled")) {
            var values = "snapped".equals(feature)
                    ? List.of("none", "x", "y", "block", "inline", "both")
                    : "stuck".equals(feature)
                            ? List.of(
                                    "none",
                                    "top",
                                    "right",
                                    "bottom",
                                    "left",
                                    "block-start",
                                    "block-end",
                                    "inline-start",
                                    "inline-end")
                            : List.of(
                                    "none",
                                    "top",
                                    "right",
                                    "bottom",
                                    "left",
                                    "block-start",
                                    "block-end",
                                    "inline-start",
                                    "inline-end",
                                    "x",
                                    "y",
                                    "block",
                                    "inline");
            for (String value : values) {
                var batch = UtilityLanguage.expand(
                        checked(RULES + "@variant test {@supports (display:grid) {@container scroll-state(" + feature
                                + ":" + value + ") {@media screen {&:focus {@slot;}}}}}"),
                        List.of("test:red"));
                assertEquals(batch.diagnostics(), List.of());
                String css = batch.expansions().get(0).css();
                assertTrue(css.indexOf("@supports") < css.indexOf("@container"), css);
                assertTrue(css.indexOf("@container") < css.indexOf("@media"), css);
                assertTrue(css.indexOf("@media") < css.indexOf(":focus"), css);
            }
        }
    }

    @Test
    public void holesOnlyFillCompleteMatchingNumericThresholds() {
        for (String head : List.of(
                "param(v) (width > 2px)",
                "(param(v) > 2px)",
                "(width param(v) 2px)",
                "param(v)",
                "style(--density:param(v))",
                "scroll-state(stuck:param(v))",
                "(width > calc(param(v) + 1px))",
                "(aspect-ratio > param(v))")) {
            var failure = expectThrows(
                    UtilityLanguage.Failure.class,
                    () -> checked(RULES + "@variant \"bad-{v}\" (v:arbitrary(<length>)) { @container " + head
                            + " { @slot; } }"));
            assertEquals(failure.diagnostic().code(), "U_CONTAINER_CONDITION", head);
        }
        var failure = expectThrows(
                UtilityLanguage.Failure.class,
                () -> checked(RULES + "@variant bad { @starting-style (x:0) {@slot;} }"));
        assertEquals(failure.diagnostic().code(), "U_STARTING_STYLE");
    }
}
