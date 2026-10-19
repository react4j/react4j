package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                Map.of(),
                symbols);
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
                "1".repeat(64));
    }

    private static SymbolId id(String name) {
        return new SymbolId("Button", SymbolKind.CLASS, name);
    }

    @Test
    public void allocationIsDeterministicReservesAuthoredNamesAndComposesTokensIndependently() {
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
        assertEquals(table.value(binding), "c readable-success");
        assertEquals(table.entries().size(), 3);
        assertEquals(table.entries().get(1).compiled(), "d");
        assertEquals(table.entries().get(1).getters(), List.of());
        assertEquals(table.entries().get(0).getters(), List.of("Button#success"));
        assertEquals(CssNames.read(table.policy(), table.json()), table);
        var prefixed = CssNames.create(catalogue, new CssNames.Policy(true, "app-"));
        assertEquals(prefixed.value(binding), "app-a readable-success");
        var readable = CssNames.create(catalogue, new CssNames.Policy(false, "ignored-"));
        assertEquals(readable.value(binding), binding.value());
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
                IllegalArgumentException.class, () -> new CssLink("//:app", "1".repeat(64), false, List.of(), table));
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
