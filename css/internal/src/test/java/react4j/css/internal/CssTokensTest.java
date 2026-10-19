package react4j.css.internal;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.SymbolKind;

public final class CssTokensTest {
    @Test
    public void tokensDistinguishSelectorsPropertiesAnimationsAndSameDocumentFragments() {
        String css = "/* 😀 */\r\n@supports (--a:var(--b,calc(var(--c)*1))) {\n"
                + ".root:is(.m,:not(#id),:where(.w)):has(.h){--d:var(--e);filter:url(\"#mask\");"
                + "content:'#ignored';background:url(icon.svg#external);animation:pulse 1s ease,other 2s steps(2);"
                + "animation-name:pulse;&.nested{color:red;}}}\n@keyframes pulse{to{filter:url(#mask);}}";
        var tokens = CssTokens.read(css);
        assertEquals(values(tokens, SymbolKind.CLASS), Set.of("root", "m", "w", "h", "nested"));
        assertEquals(values(tokens, SymbolKind.ID), Set.of("id", "mask"));
        assertEquals(values(tokens, SymbolKind.PROPERTY), Set.of("--a", "--b", "--c", "--d", "--e"));
        assertEquals(values(tokens, SymbolKind.ANIMATION), Set.of("pulse", "1s", "ease", "other", "2s"));
        for (var token : tokens) {
            assertEquals(css.substring(token.start(), token.end()), token.value());
        }
        assertEquals(
                tokens.stream().filter(token -> "mask".equals(token.value())).count(), 2L);
    }

    @Test
    public void reservationsDecodeEscapesAndQuotedAnimationNamesWithoutRewritingStrings() {
        String css = ".\\61 ,#\\62 {--\\63 :red;animation-name:'d';content:'e';filter:url('#\\66 ');}";
        var names = CssTokens.read(css).stream().map(CssTokens.Token::name).collect(Collectors.toSet());
        assertTrue(names.containsAll(Set.of("a", "b", "--c", "d", "f")), names.toString());
        assertFalse(names.contains("e"));
    }

    @Test
    public void selectorFunctionsAndNestedSelectorsExposeTheirIdentifierTokens() {
        String css = ":host(.a):host-context(.b)::slotted(.c){color:red;} :custom(#d){color:red;}";
        var tokens = CssTokens.read(css);
        assertEquals(values(tokens, SymbolKind.CLASS), Set.of("a", "b", "c"));
        assertEquals(values(tokens, SymbolKind.ID), Set.of("d"));
        assertTrue(CssTokens.read("").isEmpty());
    }

    @Test
    public void fragmentReservationsUseCssEscapesAndUrlDecodingWithoutDecodingPlusAsSpace() {
        assertEquals(CssTokens.fragmentName("#a+b"), "a+b");
        assertEquals(CssTokens.fragmentName("#a%2Bb"), "a+b");
        assertEquals(CssTokens.fragmentName("#%F0%9F%98%80"), "😀");
        expectThrows(IllegalArgumentException.class, () -> CssTokens.fragmentName("#bad%"));
        for (var value : List.of("url('\\23 f')", "url('#%66')", "url( #f )")) {
            String css = "body{filter:" + value + ";}";
            var ids = CssTokens.read(css).stream()
                    .filter(token -> SymbolKind.ID == token.kind())
                    .toList();
            assertEquals(ids.size(), 1);
            assertEquals(ids.get(0).name(), "f");
            assertEquals(
                    css.substring(ids.get(0).start(), ids.get(0).end()),
                    ids.get(0).value());
        }
    }

    private static Set<String> values(List<CssTokens.Token> tokens, SymbolKind kind) {
        return tokens.stream()
                .filter(token -> kind == token.kind())
                .map(CssTokens.Token::value)
                .collect(Collectors.toSet());
    }
}
