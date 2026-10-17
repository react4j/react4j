package org.realityforge.css.integration;

import static org.testng.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.testng.annotations.Test;

public final class CssIntegrationTest {
    @Test
    public void extractionUsesDeclaredApplicationClasspath() throws Exception {
        var styles = ButtonStyles.of();
        String root = "jcss-org-realityforge-css-integration-ButtonStyles";
        assertEquals(styles.style(), root);
        assertEquals(styles.forState(StyleState.SUCCESS), root + " " + root + "--success");
        assertEquals(styles.forState(StyleState.CANCEL), root + " " + root + "--cancel");
        String css = Files.readString(Path.of(Objects.requireNonNull(System.getProperty("css.fixture"))));
        assertTrue(css.contains("." + root + "." + root + "--success{background-color:green;"), css);
        assertTrue(css.contains("." + root + "." + root + "--cancel{background-color:red;"), css);
    }
}
