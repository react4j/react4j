package react4j.css.processor;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorAttribute;
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsConditionDeclaration;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSMediaRangeOperator;
import com.helger.css.decl.ECSSSupportsConditionOperator;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import com.helger.css.writer.CSSWriterSettings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssCompilerTest {
    private static final String ROOT = "jcss-demo-Button";

    private static String component(String css, String... modifiers) {
        return CssCompiler.component(
                        css,
                        ROOT,
                        List.of(modifiers),
                        offset -> new SourceMap.Location("Button.java", css, 0, offset),
                        Map.of())
                .output()
                .css();
    }

    private static CascadingStyleSheet parse(String css) {
        return Objects.requireNonNull(CSSReader.readFromStringReader(
                css,
                new CSSReaderSettings()
                        .setCustomErrorHandler(new ThrowingCSSParseErrorHandler())
                        .setCustomExceptionHandler(error -> {
                            throw new AssertionError(error);
                        })));
    }

    private static List<String> selectorValues(CSSSelector selector) {
        var members = selector.getAllMembers();
        assertTrue(members.stream().allMatch(CSSSelectorSimpleMember.class::isInstance));
        return members.stream()
                .map(member -> ((CSSSelectorSimpleMember) member).getValue())
                .toList();
    }

    private static String value(CSSDeclaration declaration) {
        var members = declaration.getExpression().getAllMembers();
        assertEquals(members.size(), 1);
        assertTrue(members.get(0) instanceof CSSExpressionMemberTermSimple);
        return ((CSSExpressionMemberTermSimple) members.get(0)).getValue();
    }

    @Test
    public void parsedSelectorsAreCheckedAndNamespaced() {
        String css = component(
                "content: '.success'; &:is(.success, :not(.cancel))[data-label='.success'] { color: green; }",
                "style",
                "success",
                "cancel");
        var sheet = parse(css);
        assertEquals(sheet.getRuleCount(), 2);
        var rules = sheet.getAllStyleRules();
        assertEquals(rules.size(), 2);
        assertEquals(value(Objects.requireNonNull(rules.get(0).getDeclarationOfPropertyName("content"))), "'.success'");
        var selectors = rules.get(1).getAllSelectors();
        assertEquals(selectors.size(), 1);
        var members = selectors.get(0).getAllMembers();
        assertEquals(members.size(), 3);
        assertEquals(((CSSSelectorSimpleMember) members.get(0)).getValue(), "." + ROOT);
        var alternatives = ((CSSSelectorMemberPseudoIs) members.get(1)).getAllSelectors();
        assertEquals(alternatives.size(), 2);
        assertEquals(selectorValues(alternatives.get(0)), List.of("." + ROOT + "--success"));
        var negation =
                (CSSSelectorMemberNot) alternatives.get(1).getAllMembers().get(0);
        assertEquals(negation.getSelectorCount(), 1);
        assertEquals(selectorValues(negation.getAllSelectors().get(0)), List.of("." + ROOT + "--cancel"));
        var attribute = (CSSSelectorAttribute) members.get(2);
        assertEquals(attribute.getAttrName(), "data-label");
        assertEquals(attribute.getAttrValue(), "'.success'");
        assertEquals(component("color: red;", "style"), component("/* &.fake {} */ color: red;", "style"));
    }

    @Test
    public void declarationsRetainTheirOrderAcrossNestedRules() {
        String css = component("color: red; &:hover { color: green; } color: blue;", "style");
        var sheet = parse(css);
        assertEquals(sheet.getRuleCount(), 3);
        var rules = sheet.getAllStyleRules();
        assertEquals(rules.size(), 3);
        assertEquals(
                rules.stream()
                        .map(rule -> value(Objects.requireNonNull(rule.getDeclarationOfPropertyName("color"))))
                        .toList(),
                List.of("red", "green", "blue"));
        assertEquals(
                rules.stream()
                        .map(rule -> selectorValues(rule.getAllSelectors().get(0)))
                        .toList(),
                List.of(List.of("." + ROOT), List.of("." + ROOT, ":hover"), List.of("." + ROOT)));
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
        var sheet = parse(css);
        var rules = sheet.getAllRules();
        assertEquals(rules.size(), 3);
        var media = (CSSMediaRule) rules.get(0);
        assertEquals(media.getMediaQueryCount(), 1);
        var expressions = media.getAllMediaQueries().get(0).getAllMediaExpressions();
        assertEquals(expressions.size(), 1);
        var expression = expressions.get(0);
        assertTrue(expression.isRangeContext());
        assertEquals(expression.getFeature(), "width");
        assertEquals(expression.getRangeRightOperator(), ECSSMediaRangeOperator.GREATER);
        assertEquals(
                Objects.requireNonNull(expression.getValue())
                        .getAllSimpleMembers()
                        .get(0)
                        .getValue(),
                "1px");
        assertEquals(media.getRuleCount(), 1);
        assertEquals(
                selectorValues(media.getAllStyleRules().get(0).getAllSelectors().get(0)),
                List.of("." + ROOT, "." + ROOT + "--success"));
        var nested = (CSSStyleRule) rules.get(1);
        var members = nested.getAllSelectors().get(0).getAllMembers();
        assertEquals(members.size(), 2);
        assertEquals(((CSSSelectorSimpleMember) members.get(1)).getValue(), ":disabled");
        var parents = ((CSSSelectorMemberPseudoIs) members.get(0)).getAllSelectors();
        assertEquals(parents.size(), 2);
        assertEquals(selectorValues(parents.get(0)), List.of("." + ROOT, ":hover"));
        assertEquals(selectorValues(parents.get(1)), List.of("." + ROOT, "." + ROOT + "--success"));
        var supports = (CSSSupportsRule) rules.get(2);
        var condition = supports.getAllSupportConditionMembers();
        assertEquals(condition.size(), 3);
        var display = ((CSSSupportsConditionDeclaration) condition.get(0)).getDeclaration();
        assertEquals(display.getProperty(), "display");
        assertEquals(value(display), "grid");
        assertEquals(condition.get(1), ECSSSupportsConditionOperator.AND);
        var color = ((CSSSupportsConditionDeclaration) condition.get(2)).getDeclaration();
        assertEquals(color.getProperty(), "color");
        assertEquals(value(color), "red");
        assertEquals(supports.getRuleCount(), 1);
        assertEquals(
                value(Objects.requireNonNull(
                        supports.getAllStyleRules().get(0).getDeclarationOfPropertyName("display"))),
                "grid");
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
                        Map.entry("@font-face { font-family: test; src: url(test.woff); }", "rule"),
                        Map.entry(
                                "@container (width > 1px) { &.success { color: red; } }", "Unsupported component rule"),
                        Map.entry("@layer global { &.success { color: red; } }", "Unsupported component rule"),
                        Map.entry("color red;", "Invalid CSS"),
                        Map.entry("color: red; &: { color: blue; }", "Invalid CSS"),
                        Map.entry("} body { color: red; } /*", "Invalid CSS"),
                        Map.entry("@media (width > 1px) { color: red; }", "Invalid CSS"))
                .entrySet()) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(entry.getKey(), "style", "success"));
            assertTrue(
                    String.valueOf(error.getMessage()).contains(entry.getValue()),
                    entry.getKey() + ": " + error.getMessage());
        }
        var animation = expectThrows(CssSyntax.InvalidCss.class, () -> component("ANIMATION: pulse 1s;", "style"));
        assertTrue(
                String.valueOf(animation.getMessage()).contains("Unknown local animation name"),
                animation.getMessage());
    }

    @Test
    public void diagnosticsUseOriginalCssOffsets() {
        String source = "color: red;\n\t&.typo { color: blue; }";
        var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(source, "style"));
        assertEquals(error.offset(), source.indexOf(".typo"));
        var malformed = expectThrows(CssSyntax.InvalidCss.class, () -> component("color red;", "style"));
        assertTrue(String.valueOf(malformed.getMessage()).contains("Invalid CSS"), malformed.getMessage());
    }

    @Test
    public void nestedConditionalsKeepTheirOrderAndOwnedSelectors() {
        var sheet = parse(component(
                "@media screen, print { @media (color) { &:hover { color: red; } }"
                        + " @supports (display: grid) { &.success { display: grid; } } }"
                        + " @supports (color: red) { @media (color) { &:focus { color: blue; } }"
                        + " @supports (display: flex) { &:disabled { display: flex; } } }",
                "style",
                "success"));
        assertEquals(sheet.getRuleCount(), 2);
        var media = (CSSMediaRule) sheet.getAllRules().get(0);
        assertEquals(media.getMediaQueryCount(), 2);
        assertEquals(media.getRuleCount(), 2);
        var innerMedia = (CSSMediaRule) media.getAllRules().get(0);
        assertEquals(
                selectorValues(
                        innerMedia.getAllStyleRules().get(0).getAllSelectors().get(0)),
                List.of("." + ROOT, ":hover"));
        var innerSupports = (CSSSupportsRule) media.getAllRules().get(1);
        assertEquals(
                selectorValues(innerSupports
                        .getAllStyleRules()
                        .get(0)
                        .getAllSelectors()
                        .get(0)),
                List.of("." + ROOT, "." + ROOT + "--success"));
        var supports = (CSSSupportsRule) sheet.getAllRules().get(1);
        assertEquals(supports.getRuleCount(), 2);
        assertEquals(
                selectorValues(((CSSMediaRule) supports.getAllRules().get(0))
                        .getAllStyleRules()
                        .get(0)
                        .getAllSelectors()
                        .get(0)),
                List.of("." + ROOT, ":focus"));
        assertEquals(
                selectorValues(((CSSSupportsRule) supports.getAllRules().get(1))
                        .getAllStyleRules()
                        .get(0)
                        .getAllSelectors()
                        .get(0)),
                List.of("." + ROOT, ":disabled"));
    }

    @Test
    public void whereSelectorsNamespaceUnicodeModifiersAndPreserveAttributes() {
        var sheet = parse(component("&:where(.π, [data-state='π']):focus-visible { color: red; }", "style", "π"));
        var members = sheet.getAllStyleRules().get(0).getAllSelectors().get(0).getAllMembers();
        assertEquals(members.size(), 3);
        assertEquals(((CSSSelectorSimpleMember) members.get(0)).getValue(), "." + ROOT);
        var alternatives = ((CSSSelectorMemberPseudoWhere) members.get(1)).getAllSelectors();
        assertEquals(alternatives.size(), 2);
        assertEquals(selectorValues(alternatives.get(0)), List.of("." + ROOT + "--_03c0_"));
        var attribute =
                (CSSSelectorAttribute) alternatives.get(1).getAllMembers().get(0);
        assertEquals(attribute.getAttrName(), "data-state");
        assertEquals(attribute.getAttrValue(), "'π'");
        assertEquals(((CSSSelectorSimpleMember) members.get(2)).getValue(), ":focus-visible");
    }

    @Test
    public void rejectsRootEscapesReservedModifiersAndUnanchoredSelectors() {
        for (var css : List.of("} body { color: red; } .other {", "} @media screen {")) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(css, "style"));
            assertEquals(error.getMessage(), "Component CSS must remain inside its implicit root");
            assertEquals(error.offset(), 0);
        }
        for (var css : List.of(".success { color: red; }", "button { color: red; }")) {
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(css, "style", "success"));
            assertEquals(error.getMessage(), "Component selectors must start with &");
            assertEquals(error.offset(), 0);
        }
        var reserved = expectThrows(CssSyntax.InvalidCss.class, () -> component("&.style { color: red; }", "style"));
        assertEquals(reserved.getMessage(), "Local selector .style has no matching modifier accessor");
        assertEquals(reserved.offset(), 1);
        assertEquals(component("/* no declarations */", "style"), "");
    }

    @Test
    public void parserDiagnosticsRejectPropertyHacksAndIllegalCharacters() {
        var hack = expectThrows(CssSyntax.InvalidCss.class, () -> component("*color: red;", "style"));
        assertTrue(String.valueOf(hack.getMessage()).startsWith("Invalid CSS:"), hack.getMessage());
        assertEquals(hack.offset(), 1);
        String source = "color: red;\u0001";
        var illegal = expectThrows(CssSyntax.InvalidCss.class, () -> component(source, "style"));
        assertEquals(illegal.getMessage(), "Illegal CSS character");
        assertEquals(illegal.offset(), source.length() - 1);
    }

    @Test
    public void localKeyframesAreHoistedNamespacedAndKeepTheirBlocks() {
        String source = "width: 10em; &.beating { animation: beat 800ms ease-in-out infinite alternate; }"
                + " @keyframes beat { from, 50% { transform: scale(1); } to { transform: scale(0.9); } }";
        var sheet = parse(component(source, "style", "beating"));
        assertEquals(sheet.getRuleCount(), 3);
        var frames = (CSSKeyframesRule) sheet.getAllRules().get(0);
        assertEquals(frames.getAnimationName(), ROOT + "--keyframes-beat");
        assertEquals(frames.getBlockCount(), 2);
        assertEquals(frames.getAllBlocks().get(0).getAllKeyframesSelectors(), List.of("from", "50%"));
        assertEquals(frames.getAllBlocks().get(1).getAllKeyframesSelectors(), List.of("to"));
        assertEquals(frames.getAllBlocks().get(0).getAllDeclarations().get(0).getProperty(), "transform");
        assertEquals(
                Objects.requireNonNull(sheet.getAllStyleRules().get(1).getDeclarationOfPropertyName("animation"))
                        .getExpression()
                        .getAllSimpleMembers()
                        .get(0)
                        .getValue(),
                ROOT + "--keyframes-beat");
        var other = parse(CssCompiler.component(
                        source,
                        "jcss-demo-Other",
                        List.of("style", "beating"),
                        offset -> new SourceMap.Location("Other.java", source, 0, offset),
                        Map.of())
                .output()
                .css());
        assertEquals(
                ((CSSKeyframesRule) other.getAllRules().get(0)).getAnimationName(), "jcss-demo-Other--keyframes-beat");
        var edited = parse(component(source.replace("scale(0.9)", "scale(0.8)"), "style", "beating"));
        assertEquals(((CSSKeyframesRule) edited.getAllRules().get(0)).getAnimationName(), frames.getAnimationName());
    }

    @Test
    public void animationReferencesResolveInsideConditionalAndNestedRules() {
        var sheet = parse(component(
                "@keyframes pulse {}"
                        + " @media (color) { &:hover { animation: pulse 1s; } }"
                        + " @supports (display: grid) { &:focus { animation-name: pulse; } }",
                "style"));
        var media = (CSSMediaRule) sheet.getAllRules().get(1);
        var supports = (CSSSupportsRule) sheet.getAllRules().get(2);
        for (var conditional : List.of(media, supports)) {
            var declaration =
                    conditional.getAllStyleRules().get(0).getAllDeclarations().get(0);
            assertEquals(
                    declaration.getExpression().getAllSimpleMembers().get(0).getValue(), ROOT + "--keyframes-pulse");
        }
    }

    @Test
    public void animationListsAndLonghandsResolveOnlyNames() {
        var sheet = parse(component(
                "@keyframes pulse {} @keyframes glow {} animation-name: pulse, none, glow; animation-duration:"
                        + " var(--duration); content: 'pulse'; --animation: pulse; -webkit-transform: scale(1);",
                "style"));
        var declarations = sheet.getAllStyleRules().get(0);
        assertEquals(
                Objects.requireNonNull(declarations.getDeclarationOfPropertyName("animation-name"))
                        .getExpression()
                        .getAllSimpleMembers()
                        .stream()
                        .map(CSSExpressionMemberTermSimple::getValue)
                        .toList(),
                List.of(ROOT + "--keyframes-pulse", "none", ROOT + "--keyframes-glow"));
        assertEquals(value(Objects.requireNonNull(declarations.getDeclarationOfPropertyName("content"))), "'pulse'");
        assertEquals(value(Objects.requireNonNull(declarations.getDeclarationOfPropertyName("--animation"))), "pulse");
        assertEquals(
                Objects.requireNonNull(declarations.getDeclarationOfPropertyName("animation-duration"))
                        .getExpression()
                        .getAsCSSString(new CSSWriterSettings(true), 0),
                "var(--duration)");
        for (var property : List.of("animation", "animation-name", "ANIMATION-NAME")) {
            for (var reset : List.of("initial", "INHERIT", "unset", "revert", "revert-layer", "none")) {
                var resetSheet = parse(component(property + ": " + reset + ";", "style"));
                assertEquals(
                        value(resetSheet
                                .getAllStyleRules()
                                .get(0)
                                .getAllDeclarations()
                                .get(0)),
                        reset);
            }
        }
        var unicode = parse(component("@keyframes π-Beat {} animation-name: π-Beat;", "style"));
        assertEquals(
                ((CSSKeyframesRule) unicode.getAllRules().get(0)).getAnimationName(),
                ROOT + "--keyframes-_03c0__002d_Beat");
        var uppercase = parse(component(
                "@KEYFRAMES Pulse { FROM { opacity: 0; } TO { opacity: 1; } } ANIMATION: Pulse 1S EASE;", "style"));
        assertEquals(
                ((CSSKeyframesRule) uppercase.getAllRules().get(0)).getAnimationName(), ROOT + "--keyframes-Pulse");
        var distinct =
                parse(component("@keyframes pulse {} @keyframes Pulse {} animation-name: pulse, Pulse;", "style"));
        assertEquals(distinct.getAllRules().size(), 3);
    }

    @Test
    public void shorthandSeparatesNamesFromTimingAndOtherKeywords() {
        for (var timing : List.of(
                "ease",
                "linear",
                "ease-in",
                "ease-out",
                "ease-in-out",
                "step-start",
                "step-end",
                "steps(2,end)",
                "cubic-bezier(0,0,1,1)",
                "linear(0,1)")) {
            var sheet = parse(component(
                    "@keyframes pulse {} animation: pulse 1s " + timing + " -200ms 2 alternate both paused;", "style"));
            assertEquals(
                    sheet.getAllStyleRules()
                            .get(0)
                            .getAllDeclarations()
                            .get(0)
                            .getExpression()
                            .getAllSimpleMembers()
                            .get(0)
                            .getValue(),
                    ROOT + "--keyframes-pulse");
        }
        for (var keyword : List.of("linear", "infinite", "normal", "forwards", "running")) {
            var sheet = parse(
                    component("@keyframes " + keyword + " {} animation: " + keyword + " 1s " + keyword + ";", "style"));
            var members = sheet.getAllStyleRules()
                    .get(0)
                    .getAllDeclarations()
                    .get(0)
                    .getExpression()
                    .getAllSimpleMembers();
            assertEquals(members.get(0).getValue(), keyword);
            assertEquals(members.get(2).getValue(), ROOT + "--keyframes-" + keyword);
            var longhand = parse(component("@keyframes " + keyword + " {} animation-name: " + keyword + ";", "style"));
            assertEquals(
                    value(longhand.getAllStyleRules()
                            .get(0)
                            .getAllDeclarations()
                            .get(0)),
                    ROOT + "--keyframes-" + keyword);
        }
        for (var css : List.of(
                "animation: 1s ease infinite alternate both paused;",
                "animation: none none;",
                "animation: -0s -0;",
                "@keyframes pulse {} animation: pulse .5s, pulse 1e2ms reverse backwards running;")) {
            assertFalse(component(css, "style").isEmpty());
        }
    }

    @DataProvider
    public Object[][] invalidAnimations() {
        return Map.ofEntries(
                        Map.entry("@keyframes pulse {} @keyframes pulse {}", "Duplicate local keyframe"),
                        Map.entry("@keyframes none {}", "Reserved keyframe"),
                        Map.entry("@keyframes default {}", "Reserved keyframe"),
                        Map.entry("@keyframes INITIAL {}", "Reserved keyframe"),
                        Map.entry("@keyframes \"pulse\" {}", "Invalid CSS"),
                        Map.entry("@keyframes 'pulse' {}", "Invalid CSS"),
                        Map.entry("@keyframes pu\\6c se {}", "unescaped, unquoted"),
                        Map.entry("@-webkit-keyframes pulse {}", "unprefixed"),
                        Map.entry("@keyframes pulse {} -webkit-animation: pulse 1s;", "unprefixed animation"),
                        Map.entry("@keyframes pulse {} -moz-animation-name: pulse;", "unprefixed animation"),
                        Map.entry("&:hover { @keyframes pulse {} }", "outer level"),
                        Map.entry("@media (color) { @keyframes pulse {} }", "outer level"),
                        Map.entry("@keyframes pulse { 101% { opacity: 0; } }", "percentages"),
                        Map.entry("@keyframes pulse { -1% { opacity: 0; } }", "Invalid CSS"),
                        Map.entry("@keyframes pulse { middle { opacity: 0; } }", "Invalid CSS"),
                        Map.entry("@keyframes pulse { to { animation-name: pulse; } }", "inside keyframes"),
                        Map.entry("@keyframes pulse { to { -webkit-animation: pulse 1s; } }", "unprefixed animation"),
                        Map.entry("animation-name: missing;", "Unknown local animation"),
                        Map.entry("anim\\61 tion-name: missing;", "Escaped component property"),
                        Map.entry("anim\\61 tion: missing 1s;", "Escaped component property"),
                        Map.entry("@keyframes pulse {} animation-name: Pulse;", "Unknown local animation"),
                        Map.entry("animation-name: var(--animation);", "static identifiers"),
                        Map.entry("animation-name: 'pulse';", "unescaped, unquoted"),
                        Map.entry("animation-name: pulse glow;", "Unknown local animation"),
                        Map.entry("animation-name: none none;", "one name"),
                        Map.entry("animation: inherit 1s;", "stand alone"),
                        Map.entry("animation: unknown 1s;", "Unknown local animation"),
                        Map.entry("animation: none / none;", "static identifiers"),
                        Map.entry("animation: steps(2,end) steps(2,end);", "shorthand function"),
                        Map.entry("animation: var(--animation);", "separate longhands"),
                        Map.entry("animation: calc(1s);", "static identifiers"),
                        Map.entry("animation: -1s;", "duration"),
                        Map.entry("animation: 1s 2s 3s;", "two times"),
                        Map.entry("animation: -1;", "iteration"),
                        Map.entry("animation: 1 2;", "iteration"),
                        Map.entry("@keyframes pulse {} animation: pulse pulse;", "one name"),
                        Map.entry("animation: , none;", "Invalid CSS"),
                        Map.entry("animation: none,;", "Invalid CSS"))
                .entrySet()
                .stream()
                .map(entry -> new Object[] {entry.getKey(), entry.getValue()})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidAnimations")
    public void invalidKeyframesAndAnimationBindingsFail(String css, String message) {
        var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(css, "style"));
        assertTrue(String.valueOf(error.getMessage()).contains(message), css + ": " + error.getMessage());
    }

    @Test
    public void animationDiagnosticsUseOriginalNameOffsets() {
        String source = "@keyframes pulse {}\n&:hover { animation-name: typo; }";
        var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(source, "style"));
        assertEquals(error.offset(), source.indexOf("typo"));
    }

    @DataProvider
    public Object[][] animationKeywords() {
        return List.of(
                        "ease",
                        "linear",
                        "ease-in",
                        "ease-out",
                        "ease-in-out",
                        "step-start",
                        "step-end",
                        "infinite",
                        "normal",
                        "reverse",
                        "alternate",
                        "alternate-reverse",
                        "forwards",
                        "backwards",
                        "both",
                        "running",
                        "paused")
                .stream()
                .map(keyword -> new Object[] {keyword})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "animationKeywords")
    public void everyShorthandKeywordRetainsItsMeaningUntilItsSlotIsFilled(String keyword) {
        var unnamed = parse(component("animation: " + keyword + ";", "style"));
        assertEquals(
                value(unnamed.getAllStyleRules().get(0).getAllDeclarations().get(0)), keyword);
        String definition = "@keyframes " + keyword + " {} ";
        var named = parse(component(definition + "animation: " + keyword + " 1s " + keyword + ";", "style"));
        var terms = named.getAllStyleRules()
                .get(0)
                .getAllDeclarations()
                .get(0)
                .getExpression()
                .getAllSimpleMembers();
        assertEquals(
                terms.stream().map(CSSExpressionMemberTermSimple::getValue).toList(),
                List.of(keyword, "1s", ROOT + "--keyframes-" + Names.encode(keyword)));
        var longhand = parse(component(definition + "animation-name: " + keyword + ";", "style"));
        assertEquals(
                value(longhand.getAllStyleRules().get(0).getAllDeclarations().get(0)),
                ROOT + "--keyframes-" + Names.encode(keyword));
    }

    @DataProvider
    public Object[][] animationShorthands() {
        return new Object[][] {
            {
                "pulse 1s ease 200ms 2 alternate both paused",
                ROOT + "--keyframes-pulse 1s ease 200ms 2 alternate both paused"
            },
            {
                "200ms pulse ease 1s infinite normal none running",
                "200ms " + ROOT + "--keyframes-pulse ease 1s infinite normal none running"
            },
            {"pulse 1e2MS +.5s .5", ROOT + "--keyframes-pulse 1e2MS +.5s .5"},
            {"+0s -0ms pulse +0", "+0s -0ms " + ROOT + "--keyframes-pulse +0"},
            {
                ".5s steps(2,end) pulse,glow 1s cubic-bezier(0,0,1,1)",
                ".5s steps(2,end) " + ROOT + "--keyframes-pulse," + ROOT + "--keyframes-glow 1s cubic-bezier(0,0,1,1)"
            },
            {"pulse 1s steps(var(--count),end)", ROOT + "--keyframes-pulse 1s steps(var(--count),end)"},
            {"none,pulse 1s,glow 2s", "none," + ROOT + "--keyframes-pulse 1s," + ROOT + "--keyframes-glow 2s"},
            {"pulse,pulse", ROOT + "--keyframes-pulse," + ROOT + "--keyframes-pulse"},
            {"pulse/**/1s/**/ease-in", ROOT + "--keyframes-pulse 1s ease-in"}
        };
    }

    @Test(dataProvider = "animationShorthands")
    public void shorthandOrderingNumbersFunctionsAndListsKeepTheirSemantics(String authored, String expected) {
        var sheet = parse(component("@keyframes pulse {} @keyframes glow {} animation: " + authored + ";", "style"));
        assertEquals(
                sheet.getAllStyleRules()
                        .get(0)
                        .getAllDeclarations()
                        .get(0)
                        .getExpression()
                        .getAsCSSString(new CSSWriterSettings(true), 0),
                expected);
    }

    @DataProvider
    public Object[][] invalidAnimationVariations() {
        var cases = new ArrayList<Object[]>();
        for (var keyword : List.of("none", "default", "initial", "inherit", "unset", "revert", "revert-layer")) {
            for (var name : List.of(keyword, keyword.toUpperCase(Locale.ROOT))) {
                cases.add(new Object[] {
                    "@keyframes " + name + " {}", "inherit".equals(keyword) ? "Invalid CSS" : "Reserved keyframe"
                });
            }
        }
        for (var property : List.of("animation", "animation-name", "ANIMATION", "ANIMATION-NAME")) {
            for (var value : List.of("'pulse'", "\"pulse\"", "pu\\6c se", "pu\\00006cse")) {
                cases.add(new Object[] {"@keyframes pulse {} " + property + ": " + value + ";", "unescaped, unquoted"});
            }
            for (var value : List.of("", ",pulse", "pulse,", "pulse,,pulse")) {
                cases.add(new Object[] {property + ": " + value + ";", "Invalid CSS"});
            }
            for (var keyword : List.of("initial", "inherit", "unset", "revert", "revert-layer")) {
                for (var value : List.of(keyword + ",pulse", "pulse," + keyword, "pulse " + keyword)) {
                    cases.add(new Object[] {"@keyframes pulse {} " + property + ": " + value + ";", "stand alone"});
                }
            }
            cases.add(new Object[] {"@keyframes pulse { to { " + property + ": none; } }", "inside keyframes"});
            cases.add(new Object[] {"@keyframes pulse { to { " + property + ": initial; } }", "inside keyframes"});
        }
        for (var prefix : List.of("-webkit-", "-moz-", "-ms-", "-o-")) {
            cases.add(new Object[] {"@" + prefix + "keyframes pulse {}", "unprefixed"});
            for (var property : List.of("animation", "animation-name")) {
                cases.add(new Object[] {prefix + property + ": none;", "unprefixed animation"});
                cases.add(new Object[] {
                    "@keyframes pulse { to { " + prefix + property + ": none; } }", "unprefixed animation"
                });
            }
        }
        for (var wrapper : List.of(
                "&:hover { %s }",
                "&.active { %s }",
                "@media (color) { %s }",
                "@supports (display: grid) { %s }",
                "@media (color) { @supports (display: grid) { %s } }")) {
            cases.add(new Object[] {wrapper.formatted("@keyframes pulse {}"), "outer level"});
        }
        for (var selector : List.of("100.01%", "101%", "1e3%")) {
            cases.add(new Object[] {"@keyframes pulse { " + selector + " { opacity: 0; } }", "percentages"});
        }
        for (var selector : List.of("-1%", "middle", "50", "from to", "from,", ",to")) {
            cases.add(new Object[] {"@keyframes pulse { " + selector + " { opacity: 0; } }", "Invalid CSS"});
        }
        for (var shorthand : List.of("pulse -0.01s", "pulse -1e2ms")) {
            cases.add(new Object[] {"@keyframes pulse {} animation: " + shorthand + ";", "duration must not be negative"
            });
        }
        for (var shorthand : List.of("pulse -.1", "pulse 1 2", "pulse infinite 2")) {
            cases.add(new Object[] {"@keyframes pulse {} animation: " + shorthand + ";", "iteration count"});
        }
        for (var property : List.of("animation", "animation-name", "color", "--value")) {
            cases.add(new Object[] {"@keyframes pulse {} " + property + ": pulse !important;", "precedence"});
            cases.add(new Object[] {"@keyframes pulse { to { " + property + ": pulse !important; } }", "precedence"});
        }
        for (var property : List.of("anim\\61 tion", "anim\\000061tion-name", "-webkit-anim\\61 tion", "c\\6f lor")) {
            cases.add(new Object[] {property + ": pulse;", "Escaped component property"});
            cases.add(new Object[] {"@keyframes pulse { to { " + property + ": pulse; } }", "Escaped component property"
            });
        }
        cases.add(new Object[] {"} @keyframes pulse {} .other {", "inside its implicit root"});
        cases.add(new Object[] {
            "@keyframes pulse {} &:hover { color: red; } @keyframes pulse {}", "Duplicate local keyframe"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidAnimationVariations")
    public void invalidAnimationVariationsAreRejected(String css, String message) {
        var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(css, "style", "active"));
        assertTrue(String.valueOf(error.getMessage()).contains(message), css + ": " + error.getMessage());
    }

    @Test
    public void keyframeBlocksPreserveRepeatedOffsetsEasingAndDeclarationOrder() {
        var sheet = parse(component(
                "@keyframes pulse { from,0%,0.5%,1e2% { opacity: 0; animation-timing-function: steps(2,end); }"
                        + " 50% { opacity: 0.5; opacity: 0.6; } 50% { transform: scale(0.9); } to { opacity: 1; } }"
                        + " animation-name: pulse;",
                "style"));
        var frames = (CSSKeyframesRule) sheet.getAllRules().get(0);
        var blocks = frames.getAllBlocks();
        assertEquals(blocks.size(), 4);
        assertEquals(blocks.get(0).getAllKeyframesSelectors(), List.of("from", "0%", "0.5%", "1e2%"));
        assertEquals(blocks.get(0).getAllDeclarations().get(1).getProperty(), "animation-timing-function");
        assertEquals(
                blocks.get(1).getAllDeclarations().stream()
                        .map(CssCompilerTest::value)
                        .toList(),
                List.of("0.5", "0.6"));
        assertEquals(blocks.get(2).getAllKeyframesSelectors(), List.of("50%"));
        assertEquals(blocks.get(2).getAllDeclarations().get(0).getProperty(), "transform");
        assertEquals(blocks.get(3).getAllKeyframesSelectors(), List.of("to"));
    }

    @Test
    public void animationNamesAreCaseSensitiveAndStringsAndCustomPropertiesRemainUntouched() {
        var sheet = parse(component(
                "@keyframes Pulse {} @keyframes pulse {} animation-name: Pulse,pulse;"
                        + " content: 'pulse Pulse'; --animation-name: pulse;",
                "style"));
        var declarations = sheet.getAllStyleRules().get(0).getAllDeclarations();
        assertEquals(
                declarations.get(0).getExpression().getAllSimpleMembers().stream()
                        .map(CSSExpressionMemberTermSimple::getValue)
                        .toList(),
                List.of(ROOT + "--keyframes-Pulse", ROOT + "--keyframes-pulse"));
        assertEquals(value(declarations.get(1)), "'pulse Pulse'");
        assertEquals(value(declarations.get(2)), "pulse");
    }

    @DataProvider
    public Object[][] localAnimationIdentifiers() {
        return new Object[][] {
            {"pulse-out", "pulse_002d_out"},
            {"pulse_out", "pulse_005f_out"},
            {"-pulse", "_002d_pulse"},
            {"--pulse", "_002d__002d_pulse"},
            {"_002d_pulse", "_005f_002d_005f_pulse"},
            {"π", "_03c0_"},
            {"é", "_00e9_"},
            {"𝄞", "_d834__dd1e_"}
        };
    }

    @Test(dataProvider = "localAnimationIdentifiers")
    public void localIdentifiersAndTheirReferencesAreEncodedWithoutCollisions(String name, String encoded) {
        for (var property : List.of("animation-name", "animation")) {
            var sheet = parse(component("@keyframes " + name + " {} " + property + ": " + name + ";", "style"));
            String expected = ROOT + "--keyframes-" + encoded;
            assertEquals(((CSSKeyframesRule) sheet.getAllRules().get(0)).getAnimationName(), expected);
            assertEquals(
                    value(sheet.getAllStyleRules().get(0).getAllDeclarations().get(0)), expected);
        }
    }

    @Test
    public void sharedAnimationAliasesRespectShorthandGrammarAndTrackRealReferences() {
        var imports = Map.of("pulse", "shared-pulse", "linear", "shared-linear", "--custom", "shared-custom");
        for (var entry : Map.of(
                        "animation: linear 1s;", List.<String>of(),
                        "animation: linear 1s linear;", List.of("linear"),
                        "animation: pulse 1s steps(2), linear 2s linear;", List.of("pulse", "linear"),
                        "animation-name: --custom;", List.of("--custom"),
                        "animation-name: pulse, pulse;", List.of("pulse"),
                        "content: 'pulse'; --value: pulse;", List.<String>of())
                .entrySet()) {
            String source = entry.getKey();
            var result = CssCompiler.component(
                    source,
                    ROOT,
                    List.of("style"),
                    offset -> new SourceMap.Location("Button.java", source, 0, offset),
                    imports);
            assertEquals(result.imports(), new HashSet<>(entry.getValue()));
            var sheet = parse(result.output().css());
            assertEquals(sheet.getAllStyleRules().size(), 1);
            for (var alias : entry.getValue()) {
                assertTrue(
                        result.output().css().contains(imports.get(alias)),
                        result.output().css());
            }
        }
        for (var alias : List.of("pulse", "linear", "éclair", "_pulse", "-pulse", "--pulse", "--")) {
            CssCompiler.checkAlias(alias);
        }
        for (var alias : List.of(
                "", "0pulse", "-0pulse", "-", "pulse name", "pulse;", "'pulse'", "none", "DEFAULT", "inherit")) {
            expectThrows(IllegalArgumentException.class, () -> CssCompiler.checkAlias(alias));
        }
        expectThrows(
                CssSyntax.InvalidCss.class,
                () -> CssCompiler.component(
                        "@keyframes pulse { from { opacity: 0; } }",
                        ROOT,
                        List.of("style"),
                        offset -> new SourceMap.Location("Button.java", "", 0, offset),
                        imports));
    }

    @Test
    public void standaloneKeyframesReuseValidationAndMapAuthoredFrames() {
        String source = "from, 50% { opacity: 0.4; } to { opacity: 1; }";
        var result = CssCompiler.keyframes(
                source, "shared-pulse", offset -> new SourceMap.Location("Pulse.java", source, 0, offset));
        var frames =
                (CSSKeyframesRule) parse(result.output().css()).getAllRules().get(0);
        assertEquals(frames.getAnimationName(), "shared-pulse");
        assertEquals(frames.getAllBlocks().size(), 2);
        assertTrue(result.imports().isEmpty());
        assertTrue(result.output().map("Pulse.css").contains("Pulse.java"));
        for (var invalid : List.of(
                "from { animation-name: none; }",
                "101% { opacity: 1; }",
                "from { opacity: 0 !important; }",
                "from { opacity 0; }",
                "} body { color: red; } @keyframes extra {")) {
            expectThrows(
                    CssSyntax.InvalidCss.class,
                    () -> CssCompiler.keyframes(
                            invalid,
                            "shared-pulse",
                            offset -> new SourceMap.Location("Pulse.java", invalid, 0, offset)));
        }
    }

    @Test
    public void globalRulesPreserveSelectorsNestingOrderAndCheckedAnimationReferences() {
        String css = ":root { --accent: blue; } body > button[data-value='pulse']:hover {"
                + " color: red; &.active { animation-name: pulse; } color: blue; }"
                + " @media screen { @supports (display: grid) { body .child { animation: pulse 1s linear; } } }";
        var result = CssCompiler.global(
                css,
                offset -> new SourceMap.Location("Defaults.java", css, 0, offset),
                Map.of("pulse", "shared-pulse"));
        assertEquals(result.imports(), Set.of("pulse"));
        var sheet = parse(result.output().css());
        assertEquals(sheet.getAllRules().size(), 3);
        var root = sheet.getAllStyleRules().get(0);
        assertEquals(root.getSelectorsAsCSSString(new CSSWriterSettings(true), 0), ":root");
        assertEquals(value(Objects.requireNonNull(root.getDeclarationOfPropertyName("--accent"))), "blue");
        var style = sheet.getAllStyleRules().get(1);
        assertEquals(style.getRuleCount(), 2);
        var nested = (CSSStyleRule) style.getAllRules().get(0);
        assertEquals(
                value(Objects.requireNonNull(nested.getDeclarationOfPropertyName("animation-name"))), "shared-pulse");
        assertTrue(result.output().css().contains("color:blue;"));
        assertTrue(result.output().map("Defaults.css").contains("Defaults.java"));
        for (var source : List.of("/* empty */", "body {}", "body { content: 'pulse'; --animation: pulse; }")) {
            var empty = CssCompiler.global(
                    source,
                    offset -> new SourceMap.Location("Defaults.java", source, 0, offset),
                    Map.of("pulse", "shared-pulse"));
            assertTrue(empty.imports().isEmpty());
        }
        for (var source : List.of(
                "@import 'other.css';",
                "@keyframes pulse { to {opacity:0;} }",
                "@font-face {font-family:test;}",
                "body {color: red !important;}",
                "body {animation-name: missing;}",
                "body {animation-name: var(--name);}",
                "@media screen { body {animation: pulse 1s var(--timing);} }")) {
            expectThrows(
                    CssSyntax.InvalidCss.class,
                    () -> CssCompiler.global(
                            source,
                            offset -> new SourceMap.Location("Defaults.java", source, 0, offset),
                            Map.of("pulse", "shared-pulse")));
        }
    }
}
