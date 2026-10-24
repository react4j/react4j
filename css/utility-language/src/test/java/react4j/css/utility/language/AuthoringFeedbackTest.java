package react4j.css.utility.language;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.SourceMap;

public final class AuthoringFeedbackTest {
    private static final String RULES = """
        @utility-rules;
        @scale spacing .25rem;
        @tokens space <length> { compact:1rem; }
        @utility flex {display:flex;}
        @utility "pad-{v}" (v:tokens(space)|scale(spacing)|arbitrary(<length>) within [0,*]) {padding:param(v);}
        @variant hover {&:hover {@slot;}}
        """;

    private static UtilityLanguage.Checked checked() {
        return UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("rules.css", RULES)),
                "demo.Rules",
                new UtilityLanguage.Bindings(Set.of()));
    }

    @Test
    public void diagnosesPayloadSpansAndSuggestsRulesWithoutChangingResults() {
        var library = checked();
        String candidate = "hover:pad-[1s]";
        var batch = UtilityLanguage.expand(
                library, List.of(candidate, "flxe", "hovr:flex", "pad-missing", "pad-[-1px]", "flex"));
        assertEquals(batch.expansions().size(), 1);
        assertEquals(batch.diagnostics().size(), 5);
        var type = batch.diagnostics().get(0);
        assertEquals(type.code(), "U_VALUE_TYPE");
        assertEquals(type.origin().start(), candidate.indexOf('[') + 1);
        assertEquals(type.origin().end(), candidate.lastIndexOf(']'));
        assertEquals(type.facts().get("payload"), "1s");
        assertEquals(type.facts().get("expectedType"), "<length>");
        assertEquals(type.facts().get("requestOffset"), "6");
        assertEquals(type.related().get(0).start(), RULES.indexOf("@utility \"pad"));
        assertTrue(type.render().contains("Defined at rules.css:"));
        assertEquals(batch.diagnostics().get(1).facts().get("suggestions"), "flex");
        assertEquals(batch.diagnostics().get(2).facts().get("suggestions"), "hover");
        assertTrue(Objects.requireNonNull(batch.diagnostics().get(2).facts().get("suggestedTemplate"))
                .contains("&:hover"));
        assertEquals(batch.diagnostics().get(3).facts().get("allowedTokens"), "compact");
        assertEquals(batch.diagnostics().get(4).code(), "U_VALUE_RANGE");
        assertEquals(batch.diagnostics().get(4).related().get(0).start(), RULES.indexOf("@utility \"pad"));
    }

    @Test
    public void composedConditionsAndSelectorHooksKeepTheirOwnPayloadOffsets() {
        var library = UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source(
                        "rules.css",
                        RULES + "@variant \"cq-{v}\" (v:arbitrary(<length>)) {@container (width>param(v)) {@slot;}}")),
                "demo.Rules",
                new UtilityLanguage.Bindings(Set.of()));
        for (String candidate : List.of("hover:[&_.unknown]:flex", "cq-[anchor-size(width)]:flex")) {
            var error = UtilityLanguage.expand(library, List.of(candidate))
                    .diagnostics()
                    .get(0);
            assertEquals(error.origin().start(), candidate.indexOf('[') + 1);
            assertEquals(error.origin().end(), candidate.indexOf(']'));
        }
    }

    @Test
    public void descriptionsAndTracesExposeSourcesTokensConditionsAndDeferredChecks() {
        var library = checked();
        assertEquals(library.prepared().scales().get("spacing"), ".25rem");
        assertEquals(library.prepared().tokens().get(0).values().get("compact"), "1rem");
        assertEquals(
                Objects.requireNonNull(
                                library.prepared().tokens().get(0).origins().get("compact"))
                        .start(),
                RULES.indexOf("compact:"));
        assertTrue(library.prepared().variants().get(0).template().contains("@slot"));
        var expansion = UtilityLanguage.expand(library, List.of("hover:pad-compact"))
                .expansions()
                .get(0);
        assertEquals(expansion.trace().stream().map(UtilityLanguage.Trace::rule).toList(), List.of("hover", "pad-{v}"));
        assertEquals(expansion.trace().get(1).related().get(0).start(), RULES.indexOf("compact:"));
        var deferred = UtilityLanguage.expand(library, List.of("pad-[var(--absent)]"));
        assertEquals(deferred.diagnostics().get(0).code(), "U_EXTERNAL_PROPERTY");
        expectThrows(
                UnsupportedOperationException.class,
                () -> library.prepared().scales().clear());
    }

    @Test
    public void mappedDefinitionsAndLiteralContextErrorsUseTheirOriginalJavaFile() {
        String text = RULES + "@utility bad {padding:anchor(left);}";
        String original = "class Rules {\n" + text + "\n}";
        var writer = new SourceMap.Writer();
        int base = original.indexOf(text);
        for (int i = 0; i < text.length(); i++) {
            int at = base + i;
            var line = (int)
                    original.substring(0, at).chars().filter(c -> '\n' == c).count();
            int column = at - original.lastIndexOf('\n', at - 1) - 1;
            writer.append(text.substring(i, i + 1), new SourceMap.Location("Rules.java", original, line, column));
        }
        var failure = expectThrows(
                UtilityLanguage.Failure.class,
                () -> UtilityLanguage.prepare(new UtilityLanguage.Source("rules.css", text, writer.map("rules.css"))));
        assertEquals(failure.diagnostic().code(), "U_VALUE_CONTEXT");
        assertEquals(failure.diagnostic().origin().source(), "Rules.java");
        assertEquals(failure.diagnostic().origin().start(), original.indexOf("padding:anchor"));
    }

    @Test
    public void unusedTokenContextsAndDuplicateDefinitionsPointToTokenEntries() {
        String context = "@utility-rules; @tokens sizes <length> { bad:anchor-size(width); } @utility \"p-{v}\""
                + " (v:tokens(sizes)) {padding:param(v);}";
        var bad = expectThrows(
                UtilityLanguage.Failure.class,
                () -> UtilityLanguage.prepare(new UtilityLanguage.Source("tokens.css", context)));
        assertEquals(bad.diagnostic().code(), "U_VALUE_CONTEXT");
        assertEquals(bad.diagnostic().origin().start(), context.indexOf("bad:"));
        String duplicate = "@utility-rules; @tokens sizes <length> { a:1px; a:2px; }";
        var overlap = expectThrows(
                UtilityLanguage.Failure.class,
                () -> UtilityLanguage.prepare(new UtilityLanguage.Source("tokens.css", duplicate)));
        assertEquals(overlap.diagnostic().origin().start(), duplicate.indexOf("a:2px"));
        assertEquals(overlap.diagnostic().related().get(0).start(), duplicate.indexOf("a:1px"));
    }

    @Test
    public void malformedDescriptorsFailWithAnExplicitDescriptorError() {
        var checked = checked();
        for (String invalid : List.of("null", "[]", "{}", "{", "{\"version\":0}")) {
            var failure = expectThrows(IllegalArgumentException.class, () -> UtilityLanguage.readChecked(invalid));
            assertTrue(String.valueOf(failure.getMessage()).contains("descriptor"), failure.getMessage());
        }
        var value = JsonParser.parseString(checked.json()).getAsJsonObject();
        value.remove("sourceMap");
        assertTrue(String.valueOf(expectThrows(
                                IllegalArgumentException.class, () -> UtilityLanguage.readChecked(value.toString()))
                        .getMessage())
                .contains("sourceMap"));
        var bindings = JsonParser.parseString(checked.json()).getAsJsonObject();
        bindings.getAsJsonObject("bindings").remove("externalAnchors");
        assertTrue(String.valueOf(expectThrows(
                                IllegalArgumentException.class, () -> UtilityLanguage.readChecked(bindings.toString()))
                        .getMessage())
                .contains("externalAnchors"));
    }
}
