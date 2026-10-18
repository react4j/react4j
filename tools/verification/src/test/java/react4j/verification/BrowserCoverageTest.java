package react4j.verification;

import static org.testng.Assert.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class BrowserCoverageTest {
    @Test
    public void pinnedBrowserRunsNativeDomAndAsynchronousJavaScript() throws Exception {
        var result = BrowserCoverage.run(
                Path.of(Objects.requireNonNull(System.getProperty("react4j.browser.metadata"))),
                Map.of(
                        "/index.html", "<!doctype html><body><script src='/fixture.js'></script></body>",
                        "/fixture.js", """
                        const element = document.createElement('button');
                        element.style.setProperty('--accent', '#04c');
                        element.setAttribute('data-value', 'true');
                        document.body.appendChild(element);
                        Promise.resolve().then(() => {
                          window.coverageFixture = {
                            done: true,
                            result: {
                              tag: element.tagName,
                              custom: getComputedStyle(element).getPropertyValue('--accent'),
                              data: element.getAttribute('data-value'),
                              focus: document.hasFocus()
                            }
                          };
                        });
                        """),
                "window.coverageFixture && window.coverageFixture.done",
                "window.coverageFixture.result");
        assertEquals(result.get("tag").getAsString(), "BUTTON");
        assertEquals(result.get("custom").getAsString(), "#04c");
        assertEquals(result.get("data").getAsString(), "true");
        assertTrue(result.get("focus").getAsBoolean());
    }
}
