package react4j.css.utility.language;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class DefinitionValidationTest {
    @DataProvider
    public Object[][] invalidDefinitions() {
        return new Object[][] {
            {"", "U_SYNTAX"},
            {"@utility flex {display:flex;}", "U_SYNTAX"},
            {"@scale spacing;", "U_SOURCE_TYPE"},
            {"@scale 2bad 1px;", "U_SOURCE_TYPE"},
            {"@scale spacing red;", "U_SOURCE_TYPE"},
            {"@scale spacing 0px;", "U_AMBIGUOUS_RULE"},
            {"@scale spacing -1px;", "U_AMBIGUOUS_RULE"},
            {"@scale s 1px; @scale s 2px;", "U_AMBIGUOUS_RULE"},
            {"@tokens x <length>;", "U_SOURCE_TYPE"},
            {"@tokens x <unknown> {a:1px;}", "U_SOURCE_TYPE"},
            {"@tokens x <length> {_:1px;}", "U_VALUE_TYPE"},
            {"@tokens x <length> {a {x:1px;}}", "U_VALUE_TYPE"},
            {"@tokens x <length> {a:param(v);}", "U_PARAMETER"},
            {"@tokens x <length> {a:1px;} @tokens x <length> {b:2px;}", "U_AMBIGUOUS_RULE"},
            {"@keyframes pulse;", "U_SYNTAX"},
            {":root {color:red;}", "U_SYNTAX"},
            {"@utility x;", "U_SYNTAX"},
            {"@utility 2bad {x:0;}", "U_SYNTAX"},
            {"@utility \"x:y\" {x:0;}", "U_SYNTAX"},
            {"@utility x (v:bare(<number>)) {x:0;}", "U_PARAMETER"},
            {"@utility \"x{v}\" (v:bare(<number>)) {x:param(v);}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (wrong:bare(<number>)) {x:param(v);}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (v:unknown(<number>)) {x:param(v);}", "U_SOURCE_TYPE"},
            {"@utility \"x-{v}\" (v:tokens(missing)) {x:param(v);}", "U_SOURCE_TYPE"},
            {"@utility \"x-{v}\" (v:scale(missing)) {x:param(v);}", "U_SOURCE_TYPE"},
            {"@utility \"x-{v}\" (v:bare(<color>)) {color:param(v);}", "U_SOURCE_TYPE"},
            {"@utility \"x-{v}\" (v:arbitrary(<length>)) {@media (width>param(v)) {color:red;}}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (v:arbitrary(<length>)) {&:nth-child(param(v)) {color:red;}}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (v:arbitrary(<length>)) {param(v):0;}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (v:arbitrary(<an+b>)) {width:param(v);}", "U_PARAMETER"},
            {"@utility \"x-{v}\" (v:arbitrary(<number>)|arbitrary(<number>)) {x:param(v);}", "U_AMBIGUOUS_RULE"},
            {
                "@tokens a <length> {same:1px;} @tokens b <length> {same:2px;} @utility \"x-{v}\""
                        + " (v:tokens(a)|tokens(b)) {x:param(v);}",
                "U_AMBIGUOUS_RULE"
            },
            {
                "@tokens a <length> {0:1px;} @utility \"x-{v}\" (v:tokens(a)|bare(<length>)) {width:param(v);}",
                "U_AMBIGUOUS_RULE"
            },
            {"@utility x {x:0;} @utility x {x:1;}", "U_AMBIGUOUS_RULE"},
            {
                "@tokens a <length> {wide-2:1px;} @utility \"x-{v}\" (v:tokens(a)) {x:param(v);} @utility"
                        + " \"x-wide-{v}\" (v:bare(<length>)) {x:param(v);}",
                "U_AMBIGUOUS_RULE"
            },
            {"@utility \"x-{v}\" (v:arbitrary(<color>) within [0,1]) {color:param(v);}", "U_RANGE_BOUND"},
            {"@utility \"x-{v}\" (v:arbitrary(<number>) within 0,1) {opacity:param(v);}", "U_RANGE_BOUND"},
            {"@utility x { &: {color:red;} }", "U_SELECTOR_GUARD"},
            {"@utility x {color:red;", "U_SYNTAX"},
            {"}", "U_SYNTAX"},
            {"@utility x {;}", "U_SYNTAX"},
            {"@utility x {color:red}", "U_SYNTAX"},
            {"/* missing", "U_SYNTAX"},
            {"@utility x {content:'line\nbreak';}", "U_SYNTAX"},
            {"@utility x {content:'missing;}", "U_SYNTAX"},
            {"\\", "U_SYNTAX"}
        };
    }

    @Test(dataProvider = "invalidDefinitions")
    public void rejectsMalformedUnusedProviderDefinitions(String source, String code) {
        String text = source.isEmpty() || source.startsWith("@utility flex") ? source : "@utility-rules; " + source;
        var failure = expectThrows(
                UtilityLanguage.Failure.class,
                () -> UtilityLanguage.prepare(new UtilityLanguage.Source("invalid.css", text)));
        assertEquals(failure.diagnostic().code(), code, source);
    }

    @Test
    public void longestPrefixMixedNumbersAndNamedStructuralTokensHaveOneMeaning() {
        String source = """
            @utility-rules;
            @tokens positions <an+b> { first:ODD; }
            @utility flex {display:flex;}
            @utility "x-{v}" (v:bare(<length>)) {width:param(v);}
            @utility "x-wide-{v}" (v:bare(<number>)) {z-index:param(v);}
            @utility "z-{v}" (v:bare(<integer>)|arbitrary(<number>)) {z-index:param(v);}
            @variant "nth-{v}" (v:tokens(positions)) {&:nth-child(param(v)) {@slot;}}
            """;
        var library = UtilityLanguage.bind(
                UtilityLanguage.prepare(new UtilityLanguage.Source("rules.css", source)),
                "demo.Rules",
                new UtilityLanguage.Bindings(Set.of()));
        var result = UtilityLanguage.expand(library, List.of("x-2px", "x-wide-2", "z-3", "z-[3.5]", "nth-first:flex"));
        assertEquals(result.diagnostics(), List.of());
        assertTrue(result.expansions().stream().anyMatch(value -> value.css().contains(":nth-child(odd)")));
        var errors = UtilityLanguage.expand(
                library, List.of("x-wide-2px", "x-calc(1px)", ":flex", "hover:", "hover::flex", "flex\nflex\nflex"));
        assertEquals(errors.diagnostics().size(), 6);
        assertEquals(errors.diagnostics().get(0).code(), "U_ARGUMENT");
        assertEquals(errors.diagnostics().get(1).code(), "U_ARGUMENT");
        assertEquals(errors.diagnostics().get(5).code(), "U_CANDIDATE_SYNTAX");
    }
}
