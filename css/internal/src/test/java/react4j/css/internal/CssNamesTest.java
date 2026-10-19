package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.Binding;
import react4j.css.internal.CssResource.Symbol;
import react4j.css.internal.CssResource.SymbolId;
import react4j.css.internal.CssResource.SymbolKind;

public final class CssNamesTest {
    private static CssCatalogue catalogue(List<Symbol> symbols, List<Binding> bindings, String css) {
        var resource = new CssResource(
                "Button",
                CssResource.Kind.COMPONENT,
                List.of(),
                List.of(),
                bindings.isEmpty() ? "" : "Styled_Button",
                bindings,
                symbols,
                List.of());
        return new CssCatalogue(
                Map.of(
                        "Button",
                        new CssCatalogue.Resource(
                                resource,
                                css,
                                JsonParser.parseString(new SourceMap.Writer()
                                                .append(css)
                                                .map("Button.css"))
                                        .getAsJsonObject())),
                "1".repeat(64),
                Map.of());
    }

    private static SymbolId id(String name) {
        return new SymbolId("Button", SymbolKind.CLASS, name);
    }

    @Test
    public void allocationIsDeterministicReservesAuthoredNamesAndKeepsStableTokens() {
        var root = new Symbol(SymbolKind.CLASS, "", "readable-root", false);
        var success = new Symbol(SymbolKind.CLASS, "success", "readable-success", true);
        var dead = new Symbol(SymbolKind.CLASS, "dead", "readable-dead", false);
        var binding = new Binding(
                "success",
                SymbolKind.CLASS,
                "readable-root readable-success",
                List.of(),
                List.of(id(""), id("success")));
        var catalogue = catalogue(List.of(success, dead, root), List.of(binding), ".a,.b{content:'readable-root';}");
        var table = CssNames.create(catalogue, new CssNames.Policy(true, ""));
        assertEquals(
                table,
                CssNames.create(
                        catalogue(List.of(root, dead, success), List.of(binding), ".a,.b{content:'readable-root';}"),
                        table.policy()));
        assertEquals(
                table.entries().stream().map(CssNames.Entry::compiled).toList(), List.of("c", "d", "readable-success"));
        assertEquals(table.entries().size(), 3);
        assertEquals(table.entries().get(1).compiled(), "d");
        assertEquals(table.entries().get(1).getters(), List.of());
        assertEquals(table.entries().get(0).getters(), List.of("Button#success"));
        assertEquals(CssNames.read(table.policy(), table.json()), table);
        var prefixed = CssNames.create(catalogue, new CssNames.Policy(true, "app-"));
        assertEquals(
                prefixed.entries().stream().map(CssNames.Entry::compiled).toList(),
                List.of("app-a", "app-b", "readable-success"));
        var readable = CssNames.create(catalogue, new CssNames.Policy(false, "ignored-"));
        assertTrue(
                readable.entries().stream().allMatch(entry -> entry.readable().equals(entry.compiled())));
    }

    @Test
    public void allocationCrossesTheAlphabetBoundaryWithoutCollisions() {
        var symbols = new ArrayList<Symbol>();
        for (int i = 0; i < 200; i++) {
            symbols.add(new Symbol(SymbolKind.CLASS, "m" + i, "readable-" + i, false));
        }
        var names = CssNames.create(catalogue(symbols, List.of(), ""), new CssNames.Policy(true, ""));
        assertEquals(
                names.entries().stream()
                        .map(CssNames.Entry::compiled)
                        .distinct()
                        .count(),
                200L);
        assertEquals(names.entries().get(52).compiled(), "aa");
        assertTrue(names.entries().stream().allMatch(entry -> entry.compiled().matches("[A-Za-z]+")));
    }

    @Test
    public void animationCandidatesCannotBecomeCaseInsensitiveGrammarKeywords() {
        var symbols = new ArrayList<Symbol>();
        for (int i = 0; i < 5; i++) {
            symbols.add(new Symbol(SymbolKind.ANIMATION, "animation" + i, "readable-" + i, false));
        }
        var table = CssNames.create(catalogue(symbols, List.of(), ""), new CssNames.Policy(true, "NON"));
        assertEquals(
                table.entries().stream().map(CssNames.Entry::compiled).toList(),
                List.of("NONa", "NONb", "NONc", "NONd", "NONf"));
    }

    @Test
    public void canonicalCompositionsMustExistAndMatchTheJvmValue() {
        var root = new Symbol(SymbolKind.CLASS, "", "root", false);
        var binding = new Binding("style", SymbolKind.CLASS, "changed", List.of(), List.of(id("")));
        expectThrows(
                IllegalArgumentException.class,
                () -> CssNames.create(catalogue(List.of(root), List.of(binding), ""), new CssNames.Policy(true, "")));
        expectThrows(
                IllegalArgumentException.class,
                () -> CssNames.create(catalogue(List.of(), List.of(binding), ""), new CssNames.Policy(true, "")));
        expectThrows(
                IllegalArgumentException.class,
                () -> new Binding("style", SymbolKind.CLASS, "root", List.of(), List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new Binding("style", SymbolKind.CLASS, "root", List.of(), List.of(id(""), id(""))));
        expectThrows(
                IllegalArgumentException.class,
                () -> new Binding("id", SymbolKind.ID, "node", List.of(), List.of(id(""))));
        expectThrows(IllegalArgumentException.class, () -> new SymbolId("../Owner", SymbolKind.ID, "node"));
        expectThrows(IllegalArgumentException.class, () -> new SymbolId("Owner", SymbolKind.PROPERTY, ""));
        expectThrows(IllegalArgumentException.class, () -> new SymbolId("Owner", SymbolKind.CLASS, "bad name"));
    }

    @Test
    public void everyKindIncludesCssOnlyNamesAndMultipleStableOrigins() {
        var symbols = List.of(
                new Symbol(SymbolKind.CLASS, "", "jcss-Local", false),
                new Symbol(SymbolKind.ID, "control", "shared", false),
                new Symbol(SymbolKind.PROPERTY, "accent", "--shared", false),
                new Symbol(SymbolKind.ANIMATION, "pulse", "pulse", false),
                new Symbol(SymbolKind.PROPERTY, "runtime", "--runtime", false));
        var local = new CssResource(
                "Local", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), symbols, List.of());
        var entries = new TreeMap<String, CssCatalogue.Resource>();
        entries.put(
                "Local",
                new CssCatalogue.Resource(
                        local, "", JsonParser.parseString("{}").getAsJsonObject()));
        for (var owner : List.of("First", "Second")) {
            var id = new SymbolId(owner, SymbolKind.ID, "shared");
            var binding = new Binding("id", SymbolKind.ID, "shared", List.of(), List.of(id));
            var metadata = new CssResource(
                    owner,
                    CssResource.Kind.GLOBAL,
                    List.of(),
                    List.of(),
                    "Styled_" + owner,
                    List.of(binding),
                    List.of(new Symbol(SymbolKind.ID, "shared", "shared", true)),
                    List.of());
            entries.put(
                    owner,
                    new CssCatalogue.Resource(
                            metadata,
                            ".app-a{color:red;}",
                            JsonParser.parseString("{}").getAsJsonObject()));
        }
        var table =
                CssNames.create(new CssCatalogue(entries, "1".repeat(64), Map.of()), new CssNames.Policy(true, "app-"));
        assertEquals(
                table.entries().stream().map(entry -> entry.symbol().kind()).collect(Collectors.toSet()),
                Set.of(SymbolKind.values()));
        var shared = table.entries().stream()
                .filter(entry -> "shared".equals(entry.readable()))
                .toList();
        assertEquals(shared.size(), 3);
        assertEquals(shared.stream().filter(CssNames.Entry::stable).count(), 2L);
        for (var entry : table.entries()) {
            if (entry.stable()) {
                assertEquals(entry.compiled(), "shared");
                assertEquals(entry.getters(), List.of(entry.symbol().owner() + "#id"));
            } else {
                assertTrue(entry.compiled()
                        .startsWith(SymbolKind.PROPERTY == entry.symbol().kind() ? "--app-" : "app-"));
                assertNotEquals(entry.compiled(), "app-a");
                assertTrue(entry.getters().isEmpty());
            }
        }
        var bad = local.withOccurrences(
                List.of(new CssResource.Occurrence(new SymbolId("Local", SymbolKind.ID, "missing"), 0, 1, List.of())));
        expectThrows(
                IllegalArgumentException.class,
                () -> CssNames.create(
                        new CssCatalogue(
                                Map.of(
                                        "Local",
                                        new CssCatalogue.Resource(
                                                bad,
                                                "x",
                                                JsonParser.parseString("{}").getAsJsonObject())),
                                "1".repeat(64),
                                Map.of()),
                        table.policy()));
    }

    @Test
    public void malformedNameTablesFailRatherThanSilentlyChangingAssignments() {
        var table = CssNames.create(
                catalogue(List.of(new Symbol(SymbolKind.CLASS, "", "root", false)), List.of(), ""),
                new CssNames.Policy(true, ""));
        for (var field : List.of("readable", "compiled", "owner", "kind", "name", "stable", "getters")) {
            var json = table.json();
            json.get(0).getAsJsonObject().remove(field);
            expectThrows(RuntimeException.class, () -> CssNames.read(table.policy(), json));
        }
        for (var invalid : List.of("null", "1", "\"false\"")) {
            var json = table.json();
            json.get(0).getAsJsonObject().add("stable", JsonParser.parseString(invalid));
            expectThrows(IllegalArgumentException.class, () -> CssNames.read(table.policy(), json));
        }
        var json = table.json();
        json.get(0).getAsJsonObject().add("getters", JsonParser.parseString("[1]"));
        expectThrows(IllegalArgumentException.class, () -> CssNames.read(table.policy(), json));
        var duplicate = table.json();
        duplicate.add(duplicate.get(0).deepCopy());
        expectThrows(IllegalArgumentException.class, () -> CssNames.read(table.policy(), duplicate));
        expectThrows(IllegalArgumentException.class, () -> new CssNames.Entry(id(""), "", "a", false, List.of()));
        expectThrows(IllegalArgumentException.class, () -> new CssNames.Entry(id(""), "root", "", false, List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssLink("//:app", "1".repeat(64), false, List.of(), table, List.of()));
    }

    @Test
    public void attributeNamesAreReservedIncludingListsEscapesAndAsciiCaseFlags() {
        for (String selector : List.of(
                "[class~=a]",
                "[class='other a']",
                "[CLASS=A i]",
                "[\\63 lass='\\61 ']",
                ":is([class=a])",
                ":not([class=a])",
                ":where([class=a])")) {
            var table = CssNames.create(
                    catalogue(
                            List.of(new Symbol(SymbolKind.CLASS, "", "root", false)),
                            List.of(),
                            selector + "{color:red;}"),
                    new CssNames.Policy(true, ""));
            assertNotEquals(table.entries().get(0).compiled(), "a", selector);
        }
        for (String selector : List.of(
                "[id=a]",
                "[ID=A i]",
                "[href='#a']",
                "[src='#a']",
                "[for=a]",
                "[form=a]",
                "[list=a]",
                "[headers='other a']",
                "[aria-labelledby='a other']",
                "[aria-describedby=a]",
                "[aria-controls=a]",
                "[aria-owns=a]",
                "[aria-activedescendant=a]",
                "[aria-details=a]",
                "[aria-errormessage=a]")) {
            var table = CssNames.create(
                    catalogue(
                            List.of(new Symbol(SymbolKind.ID, "control", "scoped-control", false)),
                            List.of(),
                            selector + "{color:red;}"),
                    new CssNames.Policy(true, ""));
            assertNotEquals(table.entries().get(0).compiled(), "a", selector);
        }
        var broad = catalogue(
                List.of(new Symbol(SymbolKind.CLASS, "", "root", false)), List.of(), "[class^=app]{color:red;}");
        expectThrows(IllegalArgumentException.class, () -> CssNames.create(broad, new CssNames.Policy(true, "app-")));
        assertEquals(
                CssNames.create(broad, new CssNames.Policy(false, "app-"))
                        .entries()
                        .get(0)
                        .compiled(),
                "root");
    }

    @Test
    public void knownBroadAttributesCannotAcquireClassListMatches() {
        var symbols = List.of(
                new Symbol(SymbolKind.CLASS, "", "source-root", false),
                new Symbol(SymbolKind.CLASS, "success", "source-modifier", false));
        var binding = new Binding(
                "success", SymbolKind.CLASS, "source-root source-modifier", List.of(), List.of(id(""), id("success")));
        String css = "[class*='a app']{color:red;}";
        var catalogue = catalogue(symbols, List.of(binding), css);
        var error = expectThrows(
                IllegalArgumentException.class, () -> CssNames.create(catalogue, new CssNames.Policy(true, "app-")));
        assertTrue(String.valueOf(error.getMessage()).contains("Button#success"));
        assertEquals(
                CssNames.create(catalogue, new CssNames.Policy(false, "app-"))
                        .entries()
                        .size(),
                2);
        assertEquals(
                CssNames.create(catalogue(symbols, List.of(), css), new CssNames.Policy(true, "app-"))
                        .entries()
                        .stream()
                        .map(CssNames.Entry::compiled)
                        .toList(),
                List.of("app-a", "app-b"));
    }

    @Test
    public void escapedRawAnimationAndVariableContextsReserveTheirActualNames() {
        for (var kind : List.of(SymbolKind.ANIMATION, SymbolKind.PROPERTY, SymbolKind.ID)) {
            String css =
                    switch (kind) {
                        case ANIMATION -> "body{anima\\74 ion-name:a;}";
                        case PROPERTY -> "body{color:v\\61 r(--a);}";
                        default -> "body{filter:u\\72 l('#a');}";
                    };
            String readable = SymbolKind.PROPERTY == kind ? "--scoped" : "scoped";
            var table = CssNames.create(
                    catalogue(List.of(new Symbol(kind, "name", readable, false)), List.of(), css),
                    new CssNames.Policy(true, ""));
            assertNotEquals(table.entries().get(0).compiled(), SymbolKind.PROPERTY == kind ? "--a" : "a");
        }
    }

    @Test
    public void controlsUseNormalDefinesWithStrictTypesAndModeDefaults() {
        assertEquals(CssNames.Policy.defines(List.of(), true), new CssNames.Policy(true, ""));
        assertEquals(CssNames.Policy.defines(List.of(), false), new CssNames.Policy(false, ""));
        assertEquals(
                CssNames.Policy.defines(
                        List.of("--define=react4j.css.compress_names=false", "--define=react4j.css.name_prefix='app-'"),
                        true),
                new CssNames.Policy(false, "app-"));
        assertEquals(
                CssNames.Policy.defines(
                        List.of(
                                "--define=react4j.css.compress_names=true",
                                "--define=react4j.css.name_prefix=\"app_\"",
                                "--define=unrelated=5",
                                "--other"),
                        false),
                new CssNames.Policy(false, "app_"));
        for (var define : List.of(
                "react4j.css.compress_names",
                "react4j.css.compress_names='false'",
                "react4j.css.compress_names=1",
                "react4j.css.name_prefix=app",
                "react4j.css.name_prefix='1bad'",
                "react4j.css.name_prefix='bad alias'",
                "react4j.css.name_prefix='--bad'",
                "react4j.css.name_prefix='unterminated")) {
            expectThrows(
                    IllegalArgumentException.class, () -> CssNames.Policy.defines(List.of("--define=" + define), true));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> CssNames.Policy.defines(
                        List.of(
                                "--define=react4j.css.compress_names=true",
                                "--define=react4j.css.compress_names=false"),
                        true));
        var policy = new CssNames.Policy(true, "app-");
        assertEquals(CssNames.Policy.read(policy.json()), policy);
        var invalid = policy.json();
        invalid.addProperty("compressed", "true");
        expectThrows(IllegalArgumentException.class, () -> CssNames.Policy.read(invalid));
        invalid.addProperty("compressed", true);
        invalid.addProperty("prefix", 4);
        expectThrows(IllegalArgumentException.class, () -> CssNames.Policy.read(invalid));
    }
}
