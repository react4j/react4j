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
    public void scopedAndConditionalVariablesCannotHideManagedFamilies() {
        var imported = Map.of("heading", new CssFonts.Import("shared.Fonts", "righteous", true));
        for (var rules : List.of(
                "&:hover{--family:heading;font-family:var(--family)}&:focus{--family:serif}",
                "&:focus{--family:serif}&:hover{--family:heading;font-family:var(--family)}",
                "&:hover{--family:heading;font-family:var(--family);@media screen{&{--family:serif}}}",
                "&:hover{--family:heading;--alias:var(--family);font-family:var(--alias)}&:focus{--family:serif}",
                "&:hover{--family:heading;--size:16px;font:var(--size) var(--family)}&:focus{--family:serif}",
                "--family:heading;font:var(--external-size) var(--family);",
                "--family:var(--missing,heading);font-family:var(--family);",
                "--family:heading;font-family:env(missing,var(--family));",
                "&:hover{--family:heading;--alias:var(--family);font-family:var(--alias)}"
                        + "&:focus{--family:var(--alias)}")) {
            var error =
                    expectThrows(CssSyntax.InvalidCss.class, () -> compile(rules + "&.unused{color:red;}", imported));
            assertTrue(String.valueOf(error.getMessage()).contains("font-family fallback"), error.getMessage());
        }
    }

    @Test
    public void fontVariableFragmentsAreCheckedInTheirFullDeclaration() {
        for (var fonts : List.of(
                Map.<String, CssFonts.Import>of(),
                Map.of("heading", new CssFonts.Import("shared.Fonts", "righteous", true)))) {
            for (var css : List.of(
                    "--weight:bold;font:var(--weight) 16px Arial;",
                    "--size:16px;font:var(--size) Arial;",
                    "--height:1.2;font:16px/var(--height) Arial;",
                    "--weight:bold;--size:16px;--height:1.2;font:var(--weight) var(--size)/var(--height) Arial;",
                    "--family:Arial;--font:16px var(--family);font:var(--font);",
                    "--first:var(--second);--second:var(--first);font:var(--first,16px Arial);",
                    "--family:var(--missing,Arial);font-family:var(--family);",
                    "--weight:bold;font:var(--weight) var(--external-size) Arial;",
                    "--family:Arial;--family:16px;font-family:var(--family);",
                    "--weight:bold;--weight:bold;font:var(--weight) 16px Arial;",
                    "--family:Arial;font-family:env(missing,var(--family));")) {
                var result = compile(css + "&.unused{color:red;}", fonts);
                assertTrue(
                        result.output().css().contains("Arial"), result.output().css());
            }
        }
    }

    @Test
    public void fontKeywordsAndMultiwordNamesAreNotMistakenForManagedFamilies() {
        var fonts = Map.of(
                "bold",
                new CssFonts.Import("shared.Fonts", "bold", true),
                "heading",
                new CssFonts.Import("shared.Fonts", "heading", true));
        for (var css : List.of(
                "--weight:bold;font:var(--weight) 16px Arial;",
                "font:bold var(--size) Arial;",
                "font:var(--prefix) 16px My Heading;",
                "--family:My Heading;font:var(--external-size) var(--family);")) {
            compile(css + "&.unused{color:red;}", fonts);
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

    @Test
    public void unicodeFontMatchingPreservesQuotedWhitespaceAndNormalization() {
        var result = component("@font-face{font-family:'Straße';src:url(asset:media.font);}font-family:'STRASSE';");
        assertEquals(result.fonts(), Map.of("strasse", "jcss-demo-Button--font-strasse"));
        assertTrue(result.output().css().contains("font-family:jcss-demo-Button--font-strasse"));
        var spaces = component("@font-face{font-family:'A  B';src:url(asset:media.font);}font-family:'a b';");
        assertEquals(spaces.fonts().keySet(), Set.of("a  b"));
        assertTrue(
                spaces.output().css().contains("font-family:'a b'"),
                spaces.output().css());
        var normalization = component("@font-face{font-family:'Å';src:url(asset:media.font);}font-family:'Å';");
        assertTrue(normalization.output().css().contains("font-family:'Å'"));
    }
}
