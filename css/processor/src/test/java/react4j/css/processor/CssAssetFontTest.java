package react4j.css.processor;

import static org.testng.Assert.*;

import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.writer.CSSWriterSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssAssetFontTest {
    private static CssCompiler.Result compile(String css, Map<String, CssFonts.Import> fonts) {
        return CssCompiler.component(
                css,
                "jcss-demo-Button",
                new CssCompiler.Exports(List.of("style", "unused"), Set.of(), Set.of()),
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                Map.of(),
                Map.of(),
                Map.of(),
                new CssCompiler.Resources(
                        Map.of("media.icon", "demo/icon.svg", "media.font", "demo/font.woff2"), fonts));
    }

    private static CssCompiler.Result component(String css) {
        return compile(css + " &.unused {color:red;}", Map.of());
    }

    @Test
    public void urlsResolveOnlyParsedImageSourcesAndPreserveSuffixes() {
        var result = component("background:image-set('asset:media.icon?size=2#shape' 1x,url(asset:media.icon) 2x);"
                + "content:'asset:media.icon';filter:url(#mask);cursor:url(https://example.com/icon.svg),auto;");
        String css = result.output().css();
        assertTrue(css.contains("asset:demo/icon.svg?size=2#shape"), css);
        assertTrue(css.contains("asset:demo/icon.svg"), css);
        assertTrue(css.contains("content:'asset:media.icon'"), css);
        assertTrue(css.contains("url(#mask)"), css);
        assertTrue(css.contains("https://example.com/icon.svg"), css);
        assertTrue(result.output().map("Button.css").contains("Button.java"));
        for (var url : List.of(
                "icons/a.svg",
                "../a.svg",
                "/a.svg",
                "asset:missing.icon",
                "asset:media.missing",
                "javascript:alert(1)",
                "file:///etc/passwd")) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> component("background:url('" + url + "');"));
            assertTrue(error.offset() >= 0);
        }
        for (var url :
                List.of("data:image/svg+xml;base64,AAAA", "//example.com/a.svg", "blob:https://example.com/id")) {
            assertTrue(
                    component("background:url('" + url + "');").output().css().contains(url));
        }
    }

    @Test
    public void fontFacesAndEveryStaticFamilySpellingShareTheOwnedIdentity() {
        String faces = "@font-face {font-family:'My Heading';src:url(asset:media.font) format('woff2');font-weight:400;"
                + "font-display:swap;} @font-face {font-family:My Heading;src:url(asset:media.font);font-weight:700;}";
        for (var declaration : List.of(
                "font-family:'my heading',sans-serif",
                "font-family:MY HEADING, serif",
                "font:italic small-caps 700 16px/1.2 'My Heading',sans-serif",
                "font:oblique 12deg 1.25rem My Heading",
                "font:calc(1rem + 2px) My Heading")) {
            var result = component(faces + declaration + ";");
            assertEquals(result.fonts(), Map.of("my heading", "jcss-demo-Button--font-my_0020_heading"));
            var sheet = Objects.requireNonNull(
                    CSSReader.readFromStringReader(result.output().css(), new CSSReaderSettings()));
            assertEquals(sheet.getAllFontFaceRules().size(), 2);
            for (var face : sheet.getAllFontFaceRules()) {
                assertTrue(face.getAsCSSString(new CSSWriterSettings(true), 0)
                        .contains("jcss-demo-Button--font-my_0020_heading"));
            }
            assertTrue(result.output().css().contains("asset:demo/font.woff2"));
        }
    }

    @Test
    public void importedFamiliesUseSeparateAliasesAndDynamicFallbacksRemainChecked() {
        var imported = Map.of("heading", new CssFonts.Import("shared.Fonts", "righteous", true));
        for (var declaration : List.of(
                "font-family:heading,serif;",
                "font:700 2rem heading;",
                "font-family:var(--choice,heading);",
                "font:var(--choice,italic 700 16px heading);",
                "font:var(--runtime);")) {
            var result = compile(declaration + "&.unused{color:red;}", imported);
            assertEquals(result.fonts(), Map.of("heading", "jcss-demo-Button--font-heading"));
            if (!declaration.equals("font:var(--runtime);")) {
                assertTrue(
                        result.output().css().contains("jcss-demo-Button--font-heading"),
                        result.output().css());
            }
        }
        expectThrows(
                CssSyntax.InvalidCss.class,
                () -> compile(
                        "@font-face {font-family:heading;src:url(asset:media.font);} &.unused{color:red;}", imported));
        expectThrows(
                CssSyntax.InvalidCss.class, () -> compile("font:var(--size) heading;&.unused{color:red;}", imported));
        for (var css : List.of(
                "--choice:heading;font-family:var(--choice);",
                "--choice:16px heading;font:var(--choice);",
                "--choice:heading;--other:var(--choice);font-family:var(--other);")) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> compile(css + "&.unused{color:red;}", imported));
            assertTrue(String.valueOf(error.getMessage()).contains("font-family fallback"));
        }
    }

    @Test
    public void malformedAndMisplacedFontDefinitionsFailAtTheirSource() {
        for (var css : List.of(
                "@font-face{src:url(asset:media.font);}",
                "@font-face{font-family:heading;}",
                "@font-face{font-family:heading,other;src:url(asset:media.font);}",
                "@font-face{font-family:var(--family);src:url(asset:media.font);}",
                "@font-face{font-family:serif;src:url(asset:media.font);}",
                "@font-face{font-family:heading;font-family:other;src:url(asset:media.font);}",
                "@font-face{font-family:heading;src:url(asset:media.font);color:red;}",
                "@font-face{font-family:heading;src:missing;}",
                "@font-face{font-family:heading;src:local();}",
                "@font-face{font-family:heading;src:url(asset:media.font),;}",
                "@font-face{font-family:heading;src:url(asset:media.font) unknown();}",
                "@media screen{@font-face{font-family:heading;src:url(asset:media.font);}}",
                "&:hover{@font-face{font-family:heading;src:url(asset:media.font);}}",
                "font-family:heading,,serif;",
                "font-family:heading,;",
                "font:italic heading;",
                "font:16px/1.2;",
                "font:unknown 16px heading;",
                "font-family:'Heading' other;")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> component(css));
        }
    }
}
