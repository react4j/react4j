package org.realityforge.css.processor;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

public final class CssCompilerTest {
    private static final String ROOT = "jcss-demo-Button";

    private static String component(String css, String... modifiers) {
        return CssCompiler.component(css, ROOT, List.of(modifiers));
    }

    @Test
    public void parsedSelectorsAreCheckedAndNamespaced() {
        String css = component(
                "content: '.success'; &:is(.success, :not(.cancel))[data-label='.success'] { color: green; }",
                "style",
                "success",
                "cancel");
        assertTrue(css.contains(ROOT + ":is(." + ROOT + "--success,:not(." + ROOT + "--cancel))"), css);
        assertTrue(css.contains("content:'.success'"), css);
        assertTrue(css.contains("[data-label='.success']"), css);
        assertEquals(component("color: red;", "style"), component("/* &.fake {} */ color: red;", "style"));
    }

    @Test
    public void declarationsRetainTheirOrderAcrossNestedRules() {
        String css = component("color: red; &:hover { color: green; } color: blue;", "style");
        assertTrue(css.indexOf("red") < css.indexOf("green"), css);
        assertTrue(css.indexOf("green") < css.indexOf("blue"), css);
        assertFalse(css.contains("&"), css);
        assertEquals(css, component("color: red; &:hover { color: green; } color: blue;", "style"));
    }

    @Test
    public void conditionalRulesAndSelectorListSpecificityArePreserved() {
        String css = component(
                "@media (width > 1px) { &.success { color: green; } }"
                        + " &:hover, &.success { &:disabled { opacity: 0.5; } }"
                        + " @supports (display: grid) and (color: red) { &.success { display: grid; } }",
                "style",
                "success");
        assertTrue(css.contains("@media"), css);
        assertTrue(css.contains("@supports (display:grid) and (color:red)"), css);
        assertTrue(css.contains(":is(." + ROOT + ":hover,." + ROOT + "." + ROOT + "--success):disabled"), css);
        // Reparse emitted conditions as shared CSS to check serialization grammar.
        CssCompiler.bundle(List.of(css), List.of());
    }

    @Test
    public void invalidOrUnownedCssFailsWithoutRecovery() {
        for (var entry : Map.ofEntries(
                        Map.entry("&.sucess { color: red; }", "no matching modifier accessor"),
                        Map.entry("/* &.success {} */ color: red;", "no matching local selector"),
                        Map.entry("content: '&.success';", "no matching local selector"),
                        Map.entry("& .success { color: red; }", "same owned element"),
                        Map.entry("& + & { color: red; }", "same owned element"),
                        Map.entry("&:has(.success) { color: red; }", "same owned element"),
                        Map.entry("&::part(success) { color: red; }", "same owned element"),
                        Map.entry("&:global { color: red; }", "same owned element"),
                        Map.entry("&:is(body) { color: red; }", "same owned element"),
                        Map.entry("&.\\73 uccess { color: red; }", "Escaped selector"),
                        Map.entry("color: red !important;", "precedence"),
                        Map.entry("@keyframes pulse { to { opacity: 0; } }", "rule"),
                        Map.entry("@font-face { font-family: test; src: url(test.woff); }", "rule"),
                        Map.entry(
                                "@container (width > 1px) { &.success { color: red; } }", "Unsupported component rule"),
                        Map.entry("@layer global { &.success { color: red; } }", "Unsupported component rule"),
                        Map.entry("color red;", "Invalid CSS"),
                        Map.entry("color: red; &: { color: blue; }", "Invalid CSS"),
                        Map.entry("} body { color: red; } /*", "Invalid CSS"),
                        Map.entry("@media (width > 1px) { color: red; }", "Invalid CSS"))
                .entrySet()) {
            var error = expectThrows(CssCompiler.InvalidCss.class, () -> component(entry.getKey(), "style", "success"));
            assertTrue(
                    String.valueOf(error.getMessage()).contains(entry.getValue()),
                    entry.getKey() + ": " + error.getMessage());
        }
        var animation = expectThrows(CssCompiler.InvalidCss.class, () -> component("ANIMATION: pulse 1s;", "style"));
        assertTrue(String.valueOf(animation.getMessage()).contains("keyframe bindings"), animation.getMessage());
    }

    @Test
    public void diagnosticsUseOriginalCssOffsets() {
        String source = "color: red;\n\t&.typo { color: blue; }";
        var error = expectThrows(CssCompiler.InvalidCss.class, () -> component(source, "style"));
        assertEquals(error.offset(), source.indexOf(".typo"));
        var malformed = expectThrows(CssCompiler.InvalidCss.class, () -> component("color red;", "style"));
        assertTrue(String.valueOf(malformed.getMessage()).contains("Invalid CSS"), malformed.getMessage());
    }

    @Test
    public void sharedAndComponentLayersAreDeterministicAndChecked() {
        String fragment = component("color: var(--color);", "style");
        var shared = List.of(":root { --color: red; } button { color: blue; }");
        String bundled = CssCompiler.bundle(shared, List.of(fragment));
        assertEquals(bundled, CssCompiler.bundle(shared, List.of(fragment)));
        assertTrue(bundled.startsWith("@layer shared,components;@layer shared{"), bundled);
        assertTrue(bundled.contains("}@layer components{" + fragment), bundled);
        expectThrows(
                CssCompiler.InvalidCss.class,
                () -> CssCompiler.bundle(List.of("button { color: red !important; }"), List.of(fragment)));
        expectThrows(
                CssCompiler.InvalidCss.class,
                () -> CssCompiler.bundle(List.of("@import 'missing.css';"), List.of(fragment)));
        expectThrows(
                CssCompiler.InvalidCss.class,
                () -> CssCompiler.bundle(List.of("@layer components { body { color: red; } }"), List.of(fragment)));
    }
}
