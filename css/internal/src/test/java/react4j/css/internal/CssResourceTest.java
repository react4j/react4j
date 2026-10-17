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
            var expected = CssResource.Kind.KEYFRAMES == kind
                    ? List.<String>of()
                    : List.of("demo.Pulse", "demo.Outer$Defaults");
            var dependencies = new ArrayList<>(expected);
            var resource = new CssResource("demo.Button", kind, dependencies);
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
        String valid = new CssResource("demo.Button", CssResource.Kind.COMPONENT, List.of()).json();
        for (var input : List.of(
                "{",
                "[]",
                "null",
                "1",
                "{}",
                valid.replace("\"version\":1", "\"version\":1.1"),
                valid.replace("\"version\":1", "\"version\":\"1\""),
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
        for (var key : List.of("version", "kind", "owner", "dependencies")) {
            var json = JsonParser.parseString(valid).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
            for (var replacement : List.of("null", "{}", "true", "7")) {
                json.add(key, JsonParser.parseString(replacement));
                expectThrows(IllegalArgumentException.class, () -> CssResource.read(json.toString(), "demo.Button"));
            }
        }
        for (var owner : List.of("", ".A", "A.", "a..B", "a/b", "a\\b", "1A", "a-B", "a B")) {
            expectThrows(IllegalArgumentException.class, () -> CssResource.path(owner));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource("A", CssResource.Kind.COMPONENT, List.of(owner)));
        }
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource("A", CssResource.Kind.COMPONENT, List.of("B", "B")));
        expectThrows(
                IllegalArgumentException.class, () -> new CssResource("A", CssResource.Kind.KEYFRAMES, List.of("B")));
    }
}
