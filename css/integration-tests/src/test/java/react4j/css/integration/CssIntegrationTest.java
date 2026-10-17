package react4j.css.integration;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.testng.annotations.Test;

public final class CssIntegrationTest {
    @Test
    public void extractionUsesDeclaredApplicationClasspath() throws Exception {
        var styles = ButtonStyles.of();
        String root = "jcss-react4j-css-integration-ButtonStyles";
        assertEquals(styles.style(), root);
        assertEquals(styles.forState(StyleState.SUCCESS), root + " " + root + "--success");
        assertEquals(styles.forState(StyleState.CANCEL), root + " " + root + "--cancel");
        String css = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.fixture"))));
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                css,
                new CSSReaderSettings()
                        .setCustomErrorHandler(new ThrowingCSSParseErrorHandler())
                        .setCustomExceptionHandler(error -> {
                            throw new AssertionError(error);
                        })));
        assertEquals(sheet.getRuleCount(), 2);
        var layers = sheet.getAllLayerRules();
        assertEquals(layers.size(), 2);
        assertEquals(layers.get(0).getAllSelectors(), List.of("shared", "components"));
        assertEquals(layers.get(0).getRuleCount(), 0);
        var components = layers.get(1);
        assertEquals(components.getAllSelectors(), List.of("components"));
        assertEquals(components.getRuleCount(), 3);
        var rules = components.getAllStyleRules();
        assertEquals(rules.size(), 3);
        for (int index = 0; index < rules.size(); index++) {
            var rule = rules.get(index);
            assertEquals(rule.getSelectorCount(), 1);
            var members = rule.getAllSelectors().get(0).getAllMembers();
            assertTrue(members.stream().allMatch(CSSSelectorSimpleMember.class::isInstance));
            var selectors = members.stream()
                    .map(member -> ((CSSSelectorSimpleMember) member).getValue())
                    .toList();
            assertEquals(
                    selectors,
                    index == 0
                            ? List.of("." + root)
                            : List.of("." + root, "." + root + "--" + (index == 1 ? "success" : "cancel")));
            var declarations = rule.getAllDeclarations();
            assertEquals(declarations.size(), 1);
            var declaration = declarations.get(0);
            assertEquals(declaration.getProperty(), index == 0 ? "color" : "background-color");
            assertFalse(declaration.isImportant());
            var values = declaration.getExpression().getAllMembers();
            assertEquals(values.size(), 1);
            assertTrue(values.get(0) instanceof CSSExpressionMemberTermSimple);
            assertEquals(
                    ((CSSExpressionMemberTermSimple) values.get(0)).getValue(),
                    List.of("white", "green", "red").get(index));
        }
    }
}
