package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public final class CssResourceTest {
    @Test
    public void metadataRoundTripsAndCopiesDependencies() {
        for (var kind : CssResource.Kind.values()) {
            var expected = List.of("demo.Pulse", "demo.Outer$Defaults");
            var dependencies = new ArrayList<>(expected);
            var resource = new CssResource("demo.Button", kind, dependencies, List.of("demo.Pulse"), true);
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
    public void malformedMetadataAndUnsafeIdentitiesAreRejected() {
        String valid = new CssResource("demo.Button", CssResource.Kind.COMPONENT, List.of(), List.of(), false).json();
        for (var input : List.of(
                "{",
                "[]",
                "null",
                "1",
                "{}",
                valid.replace("\"version\":2", "\"version\":2.1"),
                valid.replace("\"version\":2", "\"version\":\"1\""),
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
                () -> new CssResource("A", CssResource.Kind.GLOBAL, List.of("B"), List.of("B", "B"), false));
        for (var owner : List.of("", ".A", "A.", "a..B", "a/b", "a\\b", "1A", "a-B", "a B")) {
            expectThrows(IllegalArgumentException.class, () -> CssResource.path(owner));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource("A", CssResource.Kind.COMPONENT, List.of(owner), List.of(), false));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource("A", CssResource.Kind.COMPONENT, List.of("B", "B"), List.of(), false));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource("A", CssResource.Kind.KEYFRAMES, List.of("B"), List.of("Missing"), false));
    }
}
