package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                        ? List.of(new CssResource.Binding("style", CssResource.SymbolKind.CLASS, "root", List.of()))
                        : List.of(),
                Map.of(),
                List.of());
    }

    @Test
    public void bindingProviderChainsAreExplicitImmutableDependencies() {
        var providers = new ArrayList<>(List.of("Alias", "Owner"));
        var binding = new CssResource.Binding("id", CssResource.SymbolKind.ID, "shared", providers);
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
                Map.of(),
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
                        Map.of(),
                        List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding("id", CssResource.SymbolKind.ID, "shared", List.of("Owner", "Owner")));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding("id", CssResource.SymbolKind.ID, "shared", List.of("../Owner")));
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
                "Button", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), Map.of(), symbols);
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
                        Map.of(),
                        List.of(resource.symbols().get(0), resource.symbols().get(0))));
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
    public void bindingsAndMultipleProvidersAreValidatedAndImmutable() {
        var bindings = new ArrayList<>(
                List.of(new CssResource.Binding("style", CssResource.SymbolKind.CLASS, "root", List.of())));
        var owners = new ArrayList<>(List.of("Defaults", "Theme"));
        var resource = new CssResource(
                "Button",
                CssResource.Kind.COMPONENT,
                owners,
                List.of(),
                "Styled_Button",
                bindings,
                Map.of("--accent", owners),
                List.of());
        owners.clear();
        bindings.clear();
        assertEquals(resource.bindings().size(), 1);
        assertEquals(resource.providers().get("--accent"), List.of("Defaults", "Theme"));
        assertEquals(CssResource.read(resource.json(), "Button"), resource);
        for (var method : List.of("bad/name", "a.b", "")) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource.Binding(method, CssResource.SymbolKind.ID, "node", List.of()));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.Binding("id", CssResource.SymbolKind.ID, " ", List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "Button",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "",
                        resource.bindings(),
                        Map.of(),
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
                        Map.of(),
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
                        Map.of(),
                        List.of()));
        for (var providers : List.of(
                Map.of("", List.of("Defaults")),
                Map.of("--accent", List.<String>of()),
                Map.of("--accent", List.of("Missing")),
                Map.of("--accent", List.of("Defaults", "Defaults")))) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource(
                            "Button",
                            CssResource.Kind.GLOBAL,
                            List.of("Defaults"),
                            List.of(),
                            "",
                            List.of(),
                            providers,
                            List.of()));
        }
        for (var key : List.of("implementation", "bindings", "providers", "symbols")) {
            var json = JsonParser.parseString(resource.json()).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "Button"));
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
                valid.replace("\"version\":5", "\"version\":5.1"),
                valid.replace("\"version\":5", "\"version\":\"1\""),
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
            for (var replacement : List.of("null", "{}", "true", "7")) {
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
