package react4j.css.utility.language;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssUtilityBinding;

public final class AnchorValuesTest {
    private static final String RULES = """
        @utility-rules;
        @utility trigger { anchor-name:--trigger, --other; --trigger:3px; }
        @utility scope { anchor-scope:--trigger, --other; }
        @utility panel { position:fixed; position-anchor:--trigger; top:anchor(--trigger bottom, anchor(--other top, 0px)); width:anchor-size(--trigger width); }
        @utility "left-{v}" (v:arbitrary(<length-percentage>)) { left:param(v); }
        @utility "width-{v}" (v:arbitrary(<length>)) { width:param(v); }
        @utility "margin-{v}" (v:arbitrary(<length>)) { margin-inline:param(v); }
        @utility "padding-{v}" (v:arbitrary(<length>)) { padding:param(v); }
        @utility "variable-{v}" (v:arbitrary(<length>)) { --x:param(v); }
        @variant "cq-{v}" (v:arbitrary(<length>)) { @container (width > param(v)) {@slot;} }
        """;

    private static UtilityLanguage.Checked checked(String source) {
        return UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("anchors.css", source)),
                "demo.Anchors",
                new UtilityLanguage.Bindings(
                        Set.of(),
                        new CssUtilityBinding.Context(
                                Set.of("x"),
                                Set.of(),
                                Set.of(),
                                Set.of(),
                                Set.of("--host"),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                Map.of(),
                                List.of())));
    }

    @Test
    public void bindsEveryOwnedOccurrenceAndKeepsDefaultAndExternalNames() {
        var library = checked(RULES);
        var batch = UtilityLanguage.expand(
                library,
                List.of(
                        "scope",
                        "panel",
                        "left-[calc(anchor(--trigger_left)_+_2px)]",
                        "width-[anchor-size(--host_height,_10px)]",
                        "left-[anchor(left)]",
                        "width-[anchor-size()]",
                        "margin-[anchor-size(inline)]"));
        assertEquals(batch.diagnostics(), List.of());
        String css =
                batch.expansions().stream().map(UtilityLanguage.Expansion::css).reduce("", String::concat);
        assertTrue(
                css.contains("anchor-scope:--jcss-demo-Anchors--anchor-trigger, --jcss-demo-Anchors--anchor-other"),
                css);
        assertTrue(css.contains("position-anchor:--jcss-demo-Anchors--anchor-trigger"), css);
        assertTrue(css.contains("anchor(--jcss-demo-Anchors--anchor-other top, 0px)"), css);
        assertTrue(css.contains("anchor-size(--host height, 10px)"), css);
        assertTrue(css.contains("anchor(left)"), css);
        assertFalse(css.contains("anchor-name:"), css);
        var symbols = library.support().metadata().symbols();
        assertTrue(symbols.stream()
                .anyMatch(s -> CssResource.SymbolKind.ANCHOR == s.kind() && "--trigger".equals(s.name())));
        assertTrue(symbols.stream()
                .anyMatch(s -> CssResource.SymbolKind.PROPERTY == s.kind() && "trigger".equals(s.name())));
        assertEquals(UtilityLanguage.readChecked(library.json()).fingerprint(), library.fingerprint());
        assertTrue(batch.expansions().get(0).sourceMap().contains("anchors.css"));
    }

    @DataProvider
    public Object[][] candidates() {
        return new Object[][] {
            {"left-[anchor(50%)]", true},
            {"width-[anchor-size(self-inline)]", true},
            {"left-[anchor(left_--trigger,_var(--x))]", true},
            {"width-[anchor-size(--other)]", true},
            {"left-[anchor(--host_left,_anchor(--other_right,_0px))]", true},
            {"left-[anchor()]", false},
            {"left-[anchor(--trigger)]", false},
            {"left-[anchor(var(--x)_left)]", false},
            {"left-[anchor(--trigger_var(--x))]", false},
            {"width-[anchor-size(var(--x))]", false},
            {"width-[anchor-size(30%)]", true},
            {"width-[anchor-size(10px)]", true},
            {"width-[anchor-size(calc(2px_+_3px))]", true},
            {"width-[anchor-size(,10px)]", false},
            {"width-[anchor-size(width,)]", false},
            {"left-[anchor(left_right)]", false},
            {"left-[anchor(--trigger_--other_left)]", false},
            {"left-[anchor(left,_red)]", false},
            {"left-[anchor(left,_0px,_1px)]", false},
            {"left-[anchor(--missing_left)]", false},
            {"padding-[anchor(left)]", false},
            {"padding-[anchor-size(width)]", false},
            {"variable-[anchor-size(width)]", false},
            {"width-[calc(anchor(left)_+_1px)]", false},
            {"cq-[anchor-size(width)]:panel", false},
        };
    }

    @Test
    public void checksUnusedTokenAnchorNamesAgainstTheProviderAtTheirEntry() {
        for (String value : List.of(
                "anchor-size(--missing width)",
                "calc(anchor-size(--trigger width,anchor-size(--missing height)) + 1px)",
                "anchor(--missing left)")) {
            String source = RULES + "@tokens unused <length> { missing:" + value + "; }";
            var failure = expectThrows(UtilityLanguage.Failure.class, () -> checked(source));
            assertEquals(failure.diagnostic().code(), "U_ANCHOR_NAME");
            assertEquals(failure.diagnostic().origin().start(), source.indexOf("missing:"));
        }
        var library = checked(RULES + """
            @tokens unused <length> {
              owned:anchor-size(--trigger width);
              external:anchor(--host left);
              default:anchor-size(width);
            }
            """);
        assertEquals(library.prepared().tokens().get(0).values().size(), 3);
        assertEquals(UtilityLanguage.expand(library, List.of("panel")).diagnostics(), List.of());
    }

    @Test(dataProvider = "candidates")
    public void checksSelectorsFallbacksAndDeclarationContexts(String candidate, boolean valid) {
        var batch = UtilityLanguage.expand(checked(RULES), List.of(candidate));
        assertEquals(batch.diagnostics().isEmpty(), valid, batch.diagnostics().toString());
    }

    @Test
    public void rejectsUnusedInvalidNamesAndConcealedCarriers() {
        for (String source : List.of(
                "@utility bad {anchor-name:var(--x);}",
                "@utility bad {position-anchor:var(--x);}",
                "@utility bad {anchor-scope:var(--x);}",
                "@utility bad {anchor-name:--trigger --other;}",
                "@utility bad {position-anchor:--trigger,--other;}",
                "@utility bad {--alias:--trigger;}",
                "@utility bad {--alias:var(--x,--trigger);}",
                "@utility bad {--alias:anchor-size(width);}",
                "@utility bad {padding:anchor(left);}",
                "@utility \"bad-{v}\" (v:arbitrary(<string>)) {anchor-name:param(v);}",
                "@utility \"bad-{v}\" (v:arbitrary(<length>)) {top:anchor(--trigger param(v));}")) {
            expectThrows(UtilityLanguage.Failure.class, () -> checked(RULES + source));
        }
        var permitted = checked(RULES
                + "@utility inherited {--copy:var(--trigger);anchor-name:none;position-anchor:auto;anchor-scope:all;}");
        assertEquals(UtilityLanguage.expand(permitted, List.of("inherited")).diagnostics(), List.of());
        expectThrows(UtilityLanguage.Failure.class, () -> checked(RULES + "@utility bad {position-anchor:--unknown;}"));
    }
}
