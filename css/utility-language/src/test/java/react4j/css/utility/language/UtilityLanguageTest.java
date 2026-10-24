package react4j.css.utility.language;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;

public final class UtilityLanguageTest {
    private static final String RULES = """
        /* rules for Rose */
        @utility-rules;
        @scale spacing .25rem;
        @tokens space <length-percentage> { panel: 1.25rem; }
        @tokens color <color> { link: #3972aa; tint: rgba(var(--cs5), .05); }
        @utility flex { display: flex; }
        @utility "p-{size}" (size: scale(spacing) | tokens(space) | arbitrary(<length-percentage>)) {
          padding: param(size);
        }
        @utility "bg-{tone}" (tone: tokens(color) | arbitrary(<color>)) { background-color: param(tone); }
        @utility "-ml-{size}" (size: scale(spacing)) { margin-left: calc(-1 * param(size)); }
        @utility border-l { border-left-width: 1px; border-left-style: solid; }
        @variant hover { @media (hover: hover) { &:hover { @slot; } } }
        @variant th { & th { @slot; } }
        @variant "nth-{position}" (position: arbitrary(<an+b>)) { &:nth-child(param(position)) { @slot; } }
        """;

    private static UtilityLanguage.Checked checked(String text) {
        return UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("rose.utilities.css", text)),
                "demo.Rose",
                new UtilityLanguage.Bindings(Set.of("iconStack")));
    }

    @Test
    public void markerRoutingDoesNotParseFiniteSourcesOrDiscardNativeErrors() {
        assertTrue(UtilityLanguage.nativeSource(" /* intro */ \n@utility-rules; @utility flex {display:flex;}"));
        assertTrue(UtilityLanguage.nativeSource("@utility-rules /**/ ; broken"));
        assertFalse(UtilityLanguage.nativeSource(".finite { color:red; }"));
        assertFalse(UtilityLanguage.nativeSource("/* incomplete"));
        expectThrows(UtilityLanguage.Failure.class, () -> checked("@utility-rules; broken"));
    }

    @Test
    public void expandsRoseValuesAndWrittenVariantOrderWithSourceMaps() {
        var library = checked(RULES);
        var batch = UtilityLanguage.expand(
                library,
                List.of(
                        "p-3",
                        "p-1.5",
                        "p-panel",
                        "-ml-2",
                        "th:hover:bg-link",
                        "hover:th:bg-link",
                        "[&_th]:nth-[n+3]:border-l",
                        "[&_td_div:not(.iconStack)]:p-[10%]"));
        assertEquals(batch.diagnostics(), List.of());
        assertEquals(batch.expansions().size(), 8);
        assertTrue(css(batch, "p-3").contains("padding: 0.75rem;"));
        assertTrue(css(batch, "p-1.5").contains("padding: 0.375rem;"));
        assertTrue(css(batch, "p-panel").contains("padding: 1.25rem;"));
        assertTrue(css(batch, "-ml-2").contains("calc(-1 * 0.5rem)"));
        assertTrue(css(batch, "th:hover:bg-link").contains(" th:hover {"));
        assertTrue(css(batch, "hover:th:bg-link").contains(":hover th {"));
        assertTrue(css(batch, "[&_th]:nth-[n+3]:border-l").contains(" th:nth-child(n+3) {"));
        assertTrue(batch.expansions().get(0).sourceMap().contains("rose.utilities.css"));
        assertEquals(batch.expansions().get(0).trace().get(0).definition().source(), "rose.utilities.css");
        assertEquals(
                library.prepared().utilities().get(1).sources(),
                List.of("scale(spacing)", "tokens(space)", "arbitrary(<length-percentage>)"));
    }

    @Test
    public void descriptorsAreDeterministicReusableAndVersioned() {
        var library = checked(RULES);
        var reread = UtilityLanguage.readChecked(library.json());
        assertEquals(reread.fingerprint(), library.fingerprint());
        assertEquals(
                UtilityLanguage.readPrepared(library.prepared().json()).json(),
                library.prepared().json());
        var first = UtilityLanguage.expand(library, List.of("p-4", "p-2"));
        assertEquals(first, UtilityLanguage.expand(library, List.of("p-2", "p-4")));
        assertEquals(
                UtilityLanguage.expand(library, List.of("flex")).expansions().size(), 1);
        expectThrows(
                IllegalArgumentException.class,
                () -> UtilityLanguage.readPrepared(
                        library.prepared().json().replace("\"version\":1", "\"version\":0")));
    }

    @Test
    public void batchErrorsRetainPeersAndStructuredFacts() {
        var batch = UtilityLanguage.expand(
                checked(RULES),
                List.of("missing", "bg-[12px]", "bg-missing", "[th]:p-3", "bad:flex", "p-3!", "p-[1rem", "p-2", "p-2"));
        assertEquals(batch.expansions().size(), 1);
        assertEquals(
                batch.diagnostics().stream()
                        .map(UtilityLanguage.Diagnostic::code)
                        .toList(),
                List.of(
                        "U_UNKNOWN_UTILITY",
                        "U_VALUE_TYPE",
                        "U_ARGUMENT",
                        "U_SELECTOR_GUARD",
                        "U_UNKNOWN_VARIANT",
                        "U_IMPORTANT",
                        "U_CANDIDATE_SYNTAX",
                        "U_CANDIDATE_SYNTAX"));
        assertEquals(batch.diagnostics().get(1).facts().get("candidate"), "bg-[12px]");
        assertTrue(batch.diagnostics().get(1).render().contains("U_VALUE_TYPE"));
    }

    @Test
    public void unusedDefinitionsAreCheckedAndSourcesCannotOverlap() {
        for (var entry : List.of(
                List.of("@variant bad { &:hover { color: red; @slot; } }", "U_SLOT"),
                List.of("@variant bad { &:hover { @slot; @slot; } }", "U_SLOT"),
                List.of("@variant bad { :not(&) { @slot; } }", "U_SELECTOR_GUARD"),
                List.of("@utility bad { padding: param(unknown); }", "U_PARAMETER"),
                List.of("@utility bad { padding: 1rem !important; }", "U_IMPORTANT"),
                List.of(
                        "@utility \"x-{v}\" (v: scale(spacing) | arbitrary(<color>)) { x: param(v); }",
                        "U_SOURCE_TYPE"),
                List.of(
                        "@tokens numeric <length> { 4: 1rem; } @utility \"x-{v}\" (v: tokens(numeric) |"
                                + " scale(spacing)) { x:param(v); }",
                        "U_AMBIGUOUS_RULE"),
                List.of("@utility p-3 { x:0; }", "U_AMBIGUOUS_RULE"))) {
            var error = expectThrows(UtilityLanguage.Failure.class, () -> checked(RULES + entry.get(0)));
            assertEquals(error.diagnostic().code(), entry.get(1));
        }
    }

    @Test
    public void valuesCannotInjectRulesAndLiteralHooksRequireOwnership() {
        var batch = UtilityLanguage.expand(
                checked(RULES), List.of("p-[1px;color:red]", "[&_.foreign]:p-2", "[&+&]:p-2", "[&:is(&)]:p-2", "p-[]"));
        assertEquals(batch.expansions().size(), 0);
        assertEquals(
                batch.diagnostics().stream()
                        .map(UtilityLanguage.Diagnostic::code)
                        .toList(),
                List.of("U_VALUE_TYPE", "U_EXTERNAL_CLASS", "U_SELECTOR_GUARD", "U_SELECTOR_GUARD", "U_VALUE_TYPE"));
    }

    @Test
    public void supportingDefinitionsAndDeclarationRunsPreserveOrder() {
        var library = checked(RULES + """
            @property --phase { syntax: "<number>"; inherits: false; initial-value: 0; }
            @keyframes pulse { to { opacity: 1; } }
            :root { --tone: red; }
            @utility nested { color:red; & strong { color:blue; } color:green; }
            """);
        assertTrue(UtilityLanguage.supportCss(library.prepared()).contains("@keyframes pulse"));
        var css = css(UtilityLanguage.expand(library, List.of("nested")), "nested");
        assertTrue(css.indexOf("color:red") < css.indexOf(" strong"));
        assertTrue(css.indexOf(" strong") < css.indexOf("color:green"));
    }

    @Test
    public void bracketDecodingHasNoStringOrIdentifierHeuristics() {
        var library = checked(RULES + "@utility \"content-{v}\" (v: arbitrary(<string>)) { content:param(v); }");
        assertTrue(css(UtilityLanguage.expand(library, List.of("content-['Save_changes']")), "content-['Save_changes']")
                .contains("'Save changes'"));
        assertTrue(css(
                        UtilityLanguage.expand(library, List.of("content-['Save\\_changes']")),
                        "content-['Save\\_changes']")
                .contains("'Save_changes'"));
        var punctuation = UtilityLanguage.expand(library, List.of("content-['Hi!{}']"));
        assertEquals(punctuation.diagnostics(), List.of());
        assertTrue(punctuation.expansions().get(0).css().contains("'Hi!{}'"));
        assertEquals(UtilityLanguage.candidates(" p-2\tflex\np-3\f"), List.of("p-2", "flex", "p-3"));
    }

    private static String css(UtilityLanguage.Batch batch, String candidate) {
        assertEquals(batch.diagnostics(), List.of());
        return batch.expansions().stream()
                .filter(value -> candidate.equals(value.candidate()))
                .findFirst()
                .orElseThrow()
                .css();
    }
}
