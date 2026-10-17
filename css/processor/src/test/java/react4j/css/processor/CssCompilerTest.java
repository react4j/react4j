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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssCompilerTest {
    private static final String ROOT = "jcss-demo-Button";

    private static String component(String css, String... modifiers) {
        return CssCompiler.component(
                        css, ROOT, List.of(modifiers), offset -> new SourceMap.Location("Button.java", css, 0, offset))
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
                        offset -> new SourceMap.Location("Other.java", source, 0, offset))
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
}
