package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testng.annotations.Test;

public final class CssAssetValuesTest {
    @Test
    public void urlEditsFollowParsedNodesThroughLayersConditionsAndImageSets() {
        for (String expression : List.of(
                "url(asset:demo/icon.svg?x=1#mask)",
                "u\\72 l('asset:demo/icon.svg?x=1#mask')",
                "image-set('asset:demo/icon.svg?x=1#mask' 1x,url(asset:demo/icon.svg?x=1#mask) 2x)",
                "-webkit-image-set(\"asset:demo/icon.svg?x=1#mask\" 1x)",
                "var(--image,image-set('asset:demo/icon.svg?x=1#mask' 1x type('image/svg+xml')))")) {
            String css = "@layer components{@media screen{.root{background:" + expression
                    + ";content:'asset:demo/icon.svg';}}}";
            var writer = new SourceMap.Writer().append(css, new SourceMap.Location("View.java", "complete view", 9, 2));
            var edits = CssUrls.edits(
                    css,
                    value -> CssUrls.checked(value, (path, suffix) -> {
                        assertEquals(path, "demo/icon.svg");
                        assertEquals(suffix, "?x=1#mask");
                        return "../assets/hash.svg" + suffix;
                    }));
            assertFalse(edits.isEmpty(), expression);
            var result = new MappedText(
                            css, JsonParser.parseString(writer.map("app.css")).getAsJsonObject())
                    .replace(edits);
            assertTrue(result.text().contains("../assets/hash.svg?x=1#mask"));
            assertTrue(result.text().contains("content:'asset:demo/icon.svg'"));
            assertEquals(result.map().getAsJsonArray("sourcesContent").get(0).getAsString(), "complete view");
        }
        for (var css : List.of(".a{background:url(var(--image));}", ".a{background:u\\72 l();}")) {
            expectThrows(IllegalArgumentException.class, () -> CssUrls.edits(css, value -> value));
        }
        for (var value : List.of("../icon.svg", "", "asset:\\escape.svg", "a\nb", "javascript:alert(1)")) {
            expectThrows(IllegalArgumentException.class, () -> CssUrls.checked(value, (path, suffix) -> path));
        }
        for (var value : List.of(
                "#mask",
                "https://example.com/a.png",
                "HTTP://example.com/a.png",
                "//example.com/a.png",
                "data:image/png;base64,AAAA",
                "blob:https://example.com/id")) {
            assertEquals(
                    CssUrls.checked(value, (path, suffix) -> {
                        fail("External URL should not resolve");
                        return path;
                    }),
                    value);
        }
    }

    @Test
    public void dynamicFontDependenciesSurviveOnlyTheirAuthoredSpans() {
        var id = new CssResource.SymbolId("Fonts", CssResource.SymbolKind.FONT, "my heading");
        String css = ".root{font-family:var(--family,heading);}.dead{font:var(--shorthand);}";
        var uses = CssFontValues.uses(css, List.of(id));
        assertEquals(uses.size(), 2);
        var resource = new CssResource(
                "View",
                CssResource.Kind.COMPONENT,
                List.of("Fonts"),
                List.of(),
                "",
                List.of(),
                List.of(),
                List.of(),
                uses);
        assertEquals(CssResource.read(resource.json(), "View"), resource);
        assertEquals(
                resource.project(List.of(new SourceMap.Span(0, css.indexOf(".dead"))))
                        .fontUses(),
                List.of(uses.get(0)));
        int second = css.indexOf(".dead");
        var projected = resource.project(List.of(new SourceMap.Span(second, css.length())))
                .fontUses();
        assertEquals(projected.get(0).start(), uses.get(1).start() - second);
        assertEquals(projected.get(0).end(), uses.get(1).end() - second);
        expectThrows(
                UnsupportedOperationException.class, () -> resource.fontUses().clear());
        expectThrows(IllegalArgumentException.class, () -> resource.project(List.of(new SourceMap.Span(0, 20))));
        var invalidUse = new CssResource.FontUse(0, 1, List.of(id));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource(
                        "View",
                        CssResource.Kind.COMPONENT,
                        List.of(),
                        List.of(),
                        "",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(invalidUse)));
        for (var span : List.of(List.of(-1, 2), List.of(2, 2), List.of(2, 1))) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssResource.FontUse(span.get(0), span.get(1), List.of(id)));
        }
        expectThrows(IllegalArgumentException.class, () -> new CssResource.FontUse(0, 1, List.of()));
        expectThrows(IllegalArgumentException.class, () -> new CssResource.FontUse(0, 1, List.of(id, id)));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssResource.FontUse(
                        0, 1, List.of(new CssResource.SymbolId("Fonts", CssResource.SymbolKind.ANIMATION, "pulse"))));
        assertTrue(CssFontValues.uses(css, List.of()).isEmpty());
        var invalid = JsonParser.parseString(resource.json()).getAsJsonObject();
        invalid.getAsJsonArray("fontUses").get(0).getAsJsonObject().remove("families");
        expectThrows(IllegalArgumentException.class, () -> CssResource.read(invalid.toString(), "View"));
    }

    @Test
    public void malformedFontFamiliesAndShorthandTokensAreRejected() {
        for (var declaration : List.of(
                "font-family:;",
                "font-family:1bad;",
                "font-family:heading,,serif;",
                "font-family:heading,;",
                "font-family:format('x');",
                "font-family:'name' other;",
                "font:italic heading;",
                "font:bold;",
                "font:12px/1.2;",
                "font:format('x') heading;")) {
            expectThrows(RuntimeException.class, () -> {
                var sheet = Objects.requireNonNull(
                        CSSReader.readFromStringReader(".a{" + declaration + "}", new CSSReaderSettings()));
                CssFontValues.read(
                        Objects.requireNonNull(sheet.getAllStyleRules().get(0).getDeclarationAtIndex(0)));
            });
        }
        for (var declaration : List.of(
                "font:menu",
                "font:inherit",
                "font:normal normal bold 16px/normal Arial,serif",
                "font:clamp(1rem,2vw,2rem) 'Heading'",
                "font-family:env(family,heading)",
                "font-family:var(--family)")) {
            var sheet = Objects.requireNonNull(
                    CSSReader.readFromStringReader(".a{" + declaration + "}", new CSSReaderSettings()));
            assertNotNull(CssFontValues.read(
                    Objects.requireNonNull(sheet.getAllStyleRules().get(0).getDeclarationAtIndex(0))));
        }
        expectThrows(IllegalArgumentException.class, () -> CssFontValues.canonical("\n"));
        expectThrows(IllegalArgumentException.class, () -> CssFontValues.canonical("a\u0000b"));
        var catalogue = new CssCatalogue(Map.of(), "1".repeat(64), Map.of());
        CssLink.validateCssAssets(catalogue, Map.of());
    }

    @Test
    public void everyCssReferenceIsValidatedBeforeAnyLivenessDecision() {
        var metadata = new CssResource(
                "View",
                CssResource.Kind.COMPONENT,
                List.of(),
                List.of(),
                "",
                List.of(),
                List.of(),
                List.of(),
                List.of());
        for (var url : List.of("asset:demo/absent.svg", "asset:../escape.svg", "../unmanaged.svg")) {
            var resource =
                    new CssCatalogue.Resource(metadata, ".dead{background:url('" + url + "');}", new JsonObject());
            var catalogue = new CssCatalogue(Map.of("View", resource), "1".repeat(64), Map.of());
            expectThrows(IllegalArgumentException.class, () -> CssLink.validateCssAssets(catalogue, Map.of()));
        }
        var resource = new CssCatalogue.Resource(
                metadata, ".dead{background:url(asset:demo/icon.svg#shape);}", new JsonObject());
        CssLink.validateCssAssets(
                new CssCatalogue(Map.of("View", resource), "1".repeat(64), Map.of()),
                Map.of("demo/icon.svg", "declared-but-not-read.svg"));
    }
}
