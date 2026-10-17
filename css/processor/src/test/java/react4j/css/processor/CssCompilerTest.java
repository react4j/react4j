package react4j.css.processor;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> component(entry.getKey(), "style", "success"));
            assertTrue(
                    String.valueOf(error.getMessage()).contains(entry.getValue()),
                    entry.getKey() + ": " + error.getMessage());
        }
        var animation = expectThrows(CssSyntax.InvalidCss.class, () -> component("ANIMATION: pulse 1s;", "style"));
        assertTrue(String.valueOf(animation.getMessage()).contains("keyframe bindings"), animation.getMessage());
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
}
