package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.SymbolId;
import react4j.css.internal.CssResource.SymbolKind;

public final class CssRenamerTest {
    @Test
    public void attributeComparisonsCheckActualGetterCompositionsIncludingStableTokens() {
        for (int stable = 0; stable < 4; stable++) {
            boolean rootStable = 0 != (stable & 1);
            boolean modifierStable = 0 != (stable & 2);
            String root = rootStable ? "a" : "source-root";
            String modifier = modifierStable ? "b" : "source-modifier";
            var renamer = new CssRenamer(new CssNames(
                    new CssNames.Policy(true, ""),
                    List.of(
                            new CssNames.Entry(
                                    new SymbolId("Button", SymbolKind.CLASS, "success"),
                                    modifier,
                                    "b",
                                    modifierStable,
                                    List.of("Button#success")),
                            new CssNames.Entry(
                                    new SymbolId("Button", SymbolKind.CLASS, ""),
                                    root,
                                    "a",
                                    rootStable,
                                    List.of("Button#style", "Button#success")))));
            for (String selector : List.of(
                    "[class='a b']", "[class|='a b']", "[class*='a b']", "[CLASS*='A B' i]", "[class*='\\61  b']")) {
                if (rootStable && modifierStable) {
                    renamer.checkShared(selector + "{}", "variant.css");
                } else {
                    var error = expectThrows(
                            IllegalArgumentException.class, () -> renamer.checkShared(selector + "{}", "variant.css"));
                    assertTrue(String.valueOf(error.getMessage()).contains("variant.css: raw CSS attribute selector"));
                }
            }
            for (var comparison : Map.of("[class^='a ']", rootStable, "[class$=' b']", modifierStable)
                    .entrySet()) {
                if (comparison.getValue()) {
                    renamer.checkShared(comparison.getKey() + "{}", "variant.css");
                } else {
                    expectThrows(
                            IllegalArgumentException.class,
                            () -> renamer.checkShared(comparison.getKey() + "{}", "variant.css"));
                }
            }
            for (String selector : List.of("[class*=' ']", "[class^='b ']", "[id*='a b']")) {
                renamer.checkShared(selector + "{}", "variant.css");
            }
        }
        var unrelated = new CssRenamer(new CssNames(
                new CssNames.Policy(true, ""),
                List.of(
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, ""),
                                "source-root",
                                "a",
                                false,
                                List.of("Button#style")),
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, "success"),
                                "source-modifier",
                                "b",
                                false,
                                List.of("Button#success")))));
        unrelated.checkShared("[class*='a b']{}", "variant.css");
    }

    @Test
    public void escapedRawGrammarCannotHideLateAnimationPropertyOrFragmentCollisions() {
        for (var kind : List.of(SymbolKind.ANIMATION, SymbolKind.PROPERTY, SymbolKind.ID)) {
            String compiled = SymbolKind.PROPERTY == kind ? "--a" : "a";
            var table = new CssNames(
                    new CssNames.Policy(true, ""),
                    List.of(new CssNames.Entry(
                            new SymbolId("Owner", kind, "name"),
                            SymbolKind.PROPERTY == kind ? "--scoped" : "scoped",
                            compiled,
                            false,
                            List.of())));
            String css =
                    switch (kind) {
                        case ANIMATION -> "body{anima\\74 ion-name:a;}";
                        case PROPERTY -> "@supports (color:v\\61 r(--a)){body{color:red;}}";
                        default -> "body{filter:u\\72 l('#a');}";
                    };
            expectThrows(IllegalArgumentException.class, () -> new CssRenamer(table).checkShared(css, "variant.css"));
        }
    }

    @Test
    public void rawAttributeComparisonsCannotAcquireCompressedNames() {
        for (var kind : List.of(SymbolKind.CLASS, SymbolKind.ID)) {
            var renamer = new CssRenamer(new CssNames(
                    new CssNames.Policy(true, ""),
                    List.of(new CssNames.Entry(new SymbolId("Owner", kind, "name"), "source", "a", false, List.of()))));
            String attribute = SymbolKind.CLASS == kind ? "class" : "id";
            for (String operator : List.of("=", "~=", "|=", "^=", "$=", "*=")) {
                for (String value : List.of("'a'", "'A' i", "'\\61 '")) {
                    String css = "[" + attribute + operator + value + "]{color:red;}";
                    var error =
                            expectThrows(IllegalArgumentException.class, () -> renamer.checkShared(css, "variant.css"));
                    assertTrue(String.valueOf(error.getMessage())
                            .contains("variant.css: raw CSS attribute selector conflicts"));
                }
            }
            renamer.checkShared("[" + attribute + "='A']{color:red;}", "variant.css");
            renamer.checkShared("[" + attribute + "]{color:red;}", "variant.css");
        }
        var id = new CssRenamer(new CssNames(
                new CssNames.Policy(true, ""),
                List.of(new CssNames.Entry(
                        new SymbolId("Owner", SymbolKind.ID, "control"), "source", "a", false, List.of()))));
        for (String selector :
                List.of("[href='#a']", "[src='#a']", "[for=a]", "[headers='other a']", "[aria-controls='other a']")) {
            expectThrows(
                    IllegalArgumentException.class, () -> id.checkShared(selector + "{color:red;}", "variant.css"));
        }
        id.checkShared("[href='icon.svg#a']{color:red;}", "variant.css");
        var prefixed = new CssRenamer(new CssNames(
                new CssNames.Policy(true, "app-"),
                List.of(new CssNames.Entry(
                        new SymbolId("Owner", SymbolKind.CLASS, ""), "original", "app-a", false, List.of()))));
        for (String selector : List.of("[class^=app]", "[class$=a]", "[class*=pp]", "[class|=app]", "[class^=APP i]")) {
            expectThrows(
                    IllegalArgumentException.class,
                    () -> prefixed.checkShared(selector + "{color:red;}", "variant.css"));
        }
        prefixed.checkShared("[class*=a]{color:red;}", "variant.css");
        prefixed.checkShared("[data-name=app-a]{color:red;}", "variant.css");
        new CssRenamer(new CssNames(
                        new CssNames.Policy(true, ""),
                        List.of(new CssNames.Entry(
                                new SymbolId("Owner", SymbolKind.CLASS, ""), "original", "a", false, List.of()))))
                .checkShared("[class*=a]{color:red;}", "variant.css");
    }

    @Test
    public void parsedClassEditsPreserveStringsAttributesNativeNestingAndAuthoredAnchors() throws Exception {
        String selector = ".readable:is(.modifier,:not(.readable))";
        var writer = new SourceMap.Writer()
                .append("/* 😀 */\n")
                .append(selector, new SourceMap.Location("Button.java", "authored selector and declaration", 10, 3))
                .append("[data-text='readable']{")
                .append(
                        "content:'readable modifier';",
                        new SourceMap.Location("Button.java", "authored selector and declaration", 11, 6))
                .append("&.modifier{color:red;}}\n");
        var metadata = new CssResource(
                "Button", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), List.of(), List.of());
        metadata = metadata.withOccurrences(CssTokens.read(writer.css()).stream()
                .filter(token -> SymbolKind.CLASS == token.kind())
                .map(token -> new CssResource.Occurrence(
                        new SymbolId("Button", SymbolKind.CLASS, "readable".equals(token.value()) ? "" : "modifier"),
                        token.start(),
                        token.end(),
                        List.of()))
                .toList());
        var resource = new CssCatalogue.Resource(
                metadata,
                writer.css(),
                JsonParser.parseString(writer.map("Button.css")).getAsJsonObject());
        var table = new CssNames(
                new CssNames.Policy(true, ""),
                List.of(
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, ""), "readable", "a", false, List.of()),
                        new CssNames.Entry(
                                new SymbolId("Button", SymbolKind.CLASS, "modifier"),
                                "modifier",
                                "bc",
                                false,
                                List.of())));
        var result = new CssRenamer(table).rename(resource);
        assertEquals(
                result.css(),
                "/* 😀 */\n.a:is(.bc,:not(.a))[data-text='readable']{content:'readable modifier';&.bc{color:red;}}\n");
        assertEquals(
                CssTokens.read(writer.css()).stream()
                        .filter(token -> SymbolKind.CLASS == token.kind())
                        .map(CssTokens.Token::value)
                        .collect(Collectors.toSet()),
                Set.of("readable", "modifier"));
        var map = new SourceMapConsumerV3();
        map.parse(result.map().toString());
        for (int column : List.of(1, 2, 8, 16)) {
            var location = map.getMappingForLine(2, column);
            assertNotNull(location);
            assert null != location;
            assertEquals(location.getOriginalFile(), "Button.java");
            assertEquals(location.getLineNumber(), 11);
            assertEquals(location.getColumnPosition(), 4);
        }
        int declaration = result.css().substring(result.css().indexOf('\n') + 1).indexOf("content");
        var location = map.getMappingForLine(2, declaration + 1);
        assertNotNull(location);
        assert null != location;
        assertEquals(location.getLineNumber(), 12);
        assertEquals(location.getColumnPosition(), 7);
        assertNull(map.getMappingForLine(1, 2));
        assertEquals(
                result.map().getAsJsonArray("sourcesContent").get(0).getAsString(),
                "authored selector and declaration");
        assertSame(
                new CssRenamer(new CssNames(
                                new CssNames.Policy(false, ""),
                                table.entries().stream()
                                        .map(entry -> new CssNames.Entry(
                                                entry.symbol(),
                                                entry.readable(),
                                                entry.readable(),
                                                entry.stable(),
                                                entry.getters()))
                                        .toList()))
                        .rename(resource),
                resource);
    }

    @Test
    public void equalReadableNamesRenameAccordingToCanonicalOccurrenceAndKind() throws Exception {
        String css = "#shared{--shared:var(--shared);filter:url(#shared);animation:pulse"
                + " 1s;content:'pulse';background:url(icon.svg#shared);}";
        var localId = new SymbolId("Local", SymbolKind.ID, "control");
        var publicId = new SymbolId("PublicNames", SymbolKind.ID, "shared");
        var localProperty = new SymbolId("Local", SymbolKind.PROPERTY, "accent");
        var publicProperty = new SymbolId("PublicNames", SymbolKind.PROPERTY, "shared");
        var animation = new SymbolId("Local", SymbolKind.ANIMATION, "pulse");
        var entries = List.of(
                new CssNames.Entry(localId, "shared", "a", false, List.of()),
                new CssNames.Entry(publicId, "shared", "shared", true, List.of()),
                new CssNames.Entry(localProperty, "--shared", "--b", false, List.of()),
                new CssNames.Entry(publicProperty, "--shared", "--shared", true, List.of()),
                new CssNames.Entry(animation, "pulse", "c", false, List.of()));
        var uses = new ArrayList<CssResource.Occurrence>();
        for (var token : CssTokens.read(css)) {
            SymbolId id;
            if (SymbolKind.ID == token.kind()) {
                id = token.start() < css.indexOf('{') ? localId : publicId;
            } else if (SymbolKind.PROPERTY == token.kind()) {
                id = token.start() == css.indexOf("--shared") ? localProperty : publicProperty;
            } else if ("pulse".equals(token.value())) {
                id = animation;
            } else {
                continue;
            }
            uses.add(new CssResource.Occurrence(id, token.start(), token.end(), List.of()));
        }
        var metadata = new CssResource(
                "Consumer", CssResource.Kind.COMPONENT, List.of(), List.of(), "", List.of(), List.of(), uses);
        String source = "class Consumer {\n"
                + "  @Css(\"\"\"\n"
                + "    selector\n"
                + "    property\n"
                + "    filter\n"
                + "    animation\n"
                + "    strings\n"
                + "    \"\"\")\n"
                + "}";
        var writer = new SourceMap.Writer()
                .append("#shared", new SourceMap.Location("Consumer.java", source, 2, 4))
                .append("{")
                .append("--shared:var(--shared);", new SourceMap.Location("Consumer.java", source, 3, 4))
                .append("filter:url(#shared);", new SourceMap.Location("Consumer.java", source, 4, 4))
                .append("animation:pulse 1s;", new SourceMap.Location("Consumer.java", source, 5, 4))
                .append(
                        "content:'pulse';background:url(icon.svg#shared);",
                        new SourceMap.Location("Consumer.java", source, 6, 4))
                .append("}");
        assertEquals(writer.css(), css);
        var resource = new CssCatalogue.Resource(
                metadata,
                css,
                JsonParser.parseString(writer.map("Consumer.css")).getAsJsonObject());
        var renamer = new CssRenamer(new CssNames(new CssNames.Policy(true, ""), entries));
        assertEquals(
                renamer.rename(resource).css(),
                "#a{--b:var(--shared);filter:url(#shared);animation:c"
                        + " 1s;content:'pulse';background:url(icon.svg#shared);}");
        var result = renamer.rename(resource);
        var map = new SourceMapConsumerV3();
        map.parse(result.map().toString());
        for (var pair : List.of(
                new String[] {"a{", "3"},
                new String[] {"--b", "4"},
                new String[] {"var(--shared)", "4"},
                new String[] {"filter:", "5"},
                new String[] {"c 1s", "6"},
                new String[] {"content:", "7"})) {
            var location = map.getMappingForLine(1, result.css().indexOf(pair[0]) + 1);
            assertNotNull(location);
            assert null != location;
            assertEquals(location.getOriginalFile(), "Consumer.java");
            assertEquals(location.getLineNumber(), Integer.parseInt(pair[1]));
            assertEquals(location.getColumnPosition(), 5);
        }
        assertEquals(result.map().getAsJsonArray("sourcesContent").get(0).getAsString(), source);
        for (var raw : List.of(
                "#a{}",
                ":root{--b:red;}",
                "body{color:var(--b);}",
                "body{animation:c 1s;}",
                "body{animation-name:'c';}",
                "#\\61 {}",
                "body{filter:url('#%61');}")) {
            var error = expectThrows(IllegalArgumentException.class, () -> renamer.checkShared(raw, "shared.css"));
            assertTrue(String.valueOf(error.getMessage()).contains("shared.css"));
        }
        renamer.checkShared(".a{content:'c';background:url(icon.svg#a);}", "shared.css");
        renamer.checkShared("#shared{--shared:red;}", "shared.css");
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssRenamer(new CssNames(new CssNames.Policy(true, ""), List.of())).rename(resource));
        var corrupt = new CssCatalogue.Resource(metadata, css.replace("#shared", "#broken"), resource.map());
        expectThrows(IllegalArgumentException.class, () -> renamer.rename(corrupt));
    }
}
