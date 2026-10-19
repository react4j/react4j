package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public final class CssResourceTest {
    private static CssResource metadata(
            String owner, CssResource.Kind kind, List<String> dependencies, List<String> includes, boolean bindings) {
        return new CssResource(
                owner,
                kind,
                dependencies,
                includes,
                bindings ? "demo.Styled_Button" : "",
                bindings
                        ? List.of(new CssResource.Binding(
                                "style",
                                CssResource.SymbolKind.CLASS,
                                "root",
                                List.of(),
                                List.of(new CssResource.SymbolId(
                                        "demo.Button", CssResource.SymbolKind.CLASS, "token"))))
                        : List.of(),
                List.of(),
                List.of());
    }

    @Test
    public void bindingProviderChainsAreExplicitImmutableDependencies() {
        var providers = new ArrayList<>(List.of("Alias", "Owner"));
        var binding = new CssResource.Binding(
                "id",
                CssResource.SymbolKind.ID,
                "shared",
                providers,
                List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.ID, "token")));
        providers.clear();
        assertEquals(binding.requires(), List.of("Alias", "Owner"));
        assertEquals(CssResource.Binding.read(binding.json()), binding);
        expectThrows(
                UnsupportedOperationException.class, () -> binding.requires().clear());
        var resource = new CssResource(
                "Consumer",
                CssResource.Kind.COMPONENT,
                binding.requires(),
                List.of(),
                "Styled_Consumer",
                List.of(binding),
                List.of(),
                List.of());
        assertEquals(CssResource.read(resource.json(), "Consumer"), resource);
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Consumer",
                        CssResource.Kind.COMPONENT,
                        List.of("Owner"),
                        List.of(),
                        "Styled_Consumer",
                        List.of(binding),
                        List.of(),
                        List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding(
                        "id",
                        CssResource.SymbolKind.ID,
                        "shared",
                        List.of("Owner", "Owner"),
                        List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.ID, "token"))));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding(
                        "id",
                        CssResource.SymbolKind.ID,
                        "shared",
                        List.of("../Owner"),
                        List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.ID, "token"))));
        for (var invalid : List.of("null", "{}", "[1]", "[true]", "[null]", "\"Owner\"")) {
            var json = binding.json();
            json.add("requires", JsonParser.parseString(invalid));
            expectThrows(RuntimeException.class, () -> CssResource.Binding.read(json));
        }
        var json = binding.json();
        json.remove("requires");
        expectThrows(IllegalArgumentException.class, () -> CssResource.Binding.read(json));
    }

    @Test
    public void ownedSymbolsPreservePoliciesAndRejectMalformedInventory() {
        var symbols = new ArrayList<>(List.of(
                new CssResource.Symbol(CssResource.SymbolKind.CLASS, "", "root", false),
                new CssResource.Symbol(CssResource.SymbolKind.CLASS, "success", "root--success", true),
                new CssResource.Symbol(CssResource.SymbolKind.ID, "control", "control", true),
                new CssResource.Symbol(CssResource.SymbolKind.PROPERTY, "accent", "--accent", true),
                new CssResource.Symbol(CssResource.SymbolKind.ANIMATION, "", "pulse", false)));
        var resource = new CssResource(
                "Button", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), symbols, List.of());
        symbols.clear();
        assertEquals(resource.symbols().size(), 5);
        assertEquals(CssResource.read(resource.json(), "Button"), resource);
        expectThrows(
                UnsupportedOperationException.class, () -> resource.symbols().clear());
        for (var kind : CssResource.SymbolKind.values()) {
            for (var pair :
                    List.of(List.of("has space", "valid"), List.of("valid", ""), List.of("valid", "has space"))) {
                expectThrows(
                        IllegalArgumentException.class,
                        () -> new CssResource.Symbol(kind, pair.get(0), pair.get(1), false));
            }
            if (CssResource.SymbolKind.ID == kind || CssResource.SymbolKind.PROPERTY == kind) {
                expectThrows(IllegalArgumentException.class, () -> new CssResource.Symbol(kind, "", "value", false));
            }
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "",
                        List.of(),
                        List.of(resource.symbols().get(0), resource.symbols().get(0)),
                        List.of()));
        for (var replacement : List.of("null", "7", "{}", "\"true\"")) {
            var json = JsonParser.parseString(resource.json()).getAsJsonObject();
            json.getAsJsonArray("symbols").get(0).getAsJsonObject().add("stable", JsonParser.parseString(replacement));
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "Button"));
        }
        for (var field : List.of("stable", "kind", "name", "value")) {
            var json = JsonParser.parseString(resource.json()).getAsJsonObject();
            json.getAsJsonArray("symbols").get(0).getAsJsonObject().remove(field);
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "Button"));
        }
    }

    @Test
    public void metadataRoundTripsAndCopiesDependencies() {
        for (var kind : CssResource.Kind.values()) {
            var expected = List.of("demo.Pulse", "demo.Outer$Defaults");
            var dependencies = new ArrayList<>(expected);
            var resource = metadata("demo.Button", kind, dependencies, List.of("demo.Pulse"), true);
            dependencies.clear();
            assertEquals(resource.dependencies(), expected);
            assertEquals(CssResource.read(resource.json(), "demo.Button"), resource);
            expectThrows(
                    UnsupportedOperationException.class,
                    () -> resource.dependencies().add("Other"));
        }
        assertEquals(CssResource.path("demo.Outer$Inner"), "demo/Outer$Inner.css");
        assertEquals(CssResource.path("Button"), "Button.css");
        assertEquals(CssResource.path("démø.按钮"), "démø/按钮.css");
    }

    @Test
    public void bindingsAndCanonicalCompositionsAreValidatedAndImmutable() {
        var bindings = new ArrayList<>(List.of(new CssResource.Binding(
                "style",
                CssResource.SymbolKind.CLASS,
                "root",
                List.of(),
                List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.CLASS, "token")))));
        var owners = new ArrayList<>(List.of("Defaults", "Theme"));
        var resource = new CssResource(
                "Button",
                CssResource.Kind.COMPONENT,
                owners,
                List.of(),
                "Styled_Button",
                bindings,
                List.of(),
                List.of());
        owners.clear();
        bindings.clear();
        assertEquals(resource.bindings().size(), 1);
        assertEquals(resource.dependencies(), List.of("Defaults", "Theme"));
        assertEquals(CssResource.read(resource.json(), "Button"), resource);
        for (var method : List.of("bad/name", "a.b", "")) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource.Binding(
                            method,
                            CssResource.SymbolKind.ID,
                            "node",
                            List.of(),
                            List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.ID, "token"))));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding(
                        "id",
                        CssResource.SymbolKind.ID,
                        " ",
                        List.of(),
                        List.of(new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.ID, "token"))));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "",
                        resource.bindings(),
                        List.of(),
                        List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "Styled_Button",
                        List.of(),
                        List.of(),
                        List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "Styled_Button",
                        List.of(resource.bindings().get(0), resource.bindings().get(0)),
                        List.of(),
                        List.of()));
        for (var key : List.of("implementation", "bindings", "symbols", "occurrences")) {
            var json = JsonParser.parseString(resource.json()).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "Button"));
        }
    }

    @Test
    public void canonicalOccurrencesAreImmutableValidatedAndProjectedWithPruning() {
        var root = new CssResource.SymbolId("Consumer", CssResource.SymbolKind.CLASS, "");
        var property = new CssResource.SymbolId("Provider", CssResource.SymbolKind.PROPERTY, "accent");
        var uses = new ArrayList<>(List.of(
                new CssResource.Occurrence(root, 1, 4, List.of()),
                new CssResource.Occurrence(property, 8, 12, List.of("Provider"))));
        var resource = new CssResource(
                "Consumer", CssResource.Kind.COMPONENT, List.of("Provider"), List.of(), "", List.of(), List.of(), uses);
        uses.clear();
        assertEquals(resource.occurrences().size(), 2);
        assertEquals(CssResource.read(resource.json(), "Consumer"), resource);
        expectThrows(
                UnsupportedOperationException.class,
                () -> resource.occurrences().clear());
        var projected = resource.project(List.of(new SourceMap.Span(0, 5), new SourceMap.Span(8, 13)));
        assertEquals(
                projected.occurrences(),
                List.of(
                        new CssResource.Occurrence(root, 1, 4, List.of()),
                        new CssResource.Occurrence(property, 5, 9, List.of("Provider"))));
        assertTrue(resource.project(List.of(new SourceMap.Span(12, 14)))
                .occurrences()
                .isEmpty());
        expectThrows(IllegalArgumentException.class, () -> resource.project(List.of(new SourceMap.Span(2, 4))));
        for (var span : List.of(new int[] {-1, 4}, new int[] {1, 1}, new int[] {4, 1})) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource.Occurrence(root, span[0], span[1], List.of()));
        }
        expectThrows(IllegalArgumentException.class, () -> new CssResource.Occurrence(root, 1, 4, List.of("Provider")));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Occurrence(property, 1, 4, List.of("Provider", "Provider")));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Occurrence(property, 1, 4, List.of("../Provider")));
        expectThrows(
                IllegalArgumentException.class,
                () -> resource.withOccurrences(List.of(
                        resource.occurrences().get(1), resource.occurrences().get(0))));
        expectThrows(
                IllegalArgumentException.class,
                () -> resource.withOccurrences(
                        List.of(new CssResource.Occurrence(property, 1, 4, List.of("Missing")))));
        for (var field : List.of("start", "end")) {
            for (var value : List.of("null", "-1", "1.5", "\"1\"", "2147483648", "{}")) {
                var json = JsonParser.parseString(resource.json()).getAsJsonObject();
                json.getAsJsonArray("occurrences").get(0).getAsJsonObject().add(field, JsonParser.parseString(value));
                expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "Consumer"));
            }
        }
    }

    @Test
    public void malformedMetadataAndUnsafeIdentitiesAreRejected() {
        String valid = metadata("demo.Button", CssResource.Kind.COMPONENT, List.of(), List.of(), false)
                .json();
        for (var input : List.of(
                "{",
                "[]",
                "null",
                "1",
                "{}",
                valid.replace("\"version\":7", "\"version\":7.1"),
                valid.replace("\"version\":7", "\"version\":\"1\""),
                valid.replace("COMPONENT", "UNKNOWN"),
                valid.replace("[]", "[1]"),
                valid.replace("[]", "[null]"),
                valid.replace("[]", "{}"),
                valid.replace("[]", "null"),
                valid.replace("[]", "[\"A\",\"A\"]"),
                valid.replace("demo.Button", "../Button"),
                valid.replace("demo.Button", "other.Button"))) {
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(input, "demo.Button"));
        }
        for (var key : List.of("version", "kind", "owner", "dependencies", "includes")) {
            var json = JsonParser.parseString(valid).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
            for (var replacement : List.of("null", "{}", "true", "999")) {
                json.add(key, JsonParser.parseString(replacement));
                expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
            }
        }
        for (var replacement : List.of("null", "{}", "7", "\"true\"")) {
            var json = JsonParser.parseString(valid).getAsJsonObject();
            json.add("bindings", JsonParser.parseString(replacement));
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
        }
        var json = JsonParser.parseString(valid).getAsJsonObject();
        json.remove("bindings");
        expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
        expectThrows(
                IllegalArgumentException.class,
                () -> metadata("A", CssResource.Kind.GLOBAL, List.of("B"), List.of("B", "B"), false));
        for (var owner : List.of("", ".A", "A.", "a..B", "a/b", "a\\b", "1A", "a-B", "a B")) {
            expectThrows(IllegalArgumentException.class, () -> CssResource.path(owner));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> metadata("A", CssResource.Kind.COMPONENT, List.of(owner), List.of(), false));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> metadata("A", CssResource.Kind.COMPONENT, List.of("B", "B"), List.of(), false));
        expectThrows(
                IllegalArgumentException.class,
                () -> metadata("A", CssResource.Kind.KEYFRAMES, List.of("B"), List.of("Missing"), false));
    }
}
