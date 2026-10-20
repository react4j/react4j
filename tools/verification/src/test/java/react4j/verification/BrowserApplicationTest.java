package react4j.verification;

import static org.testng.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.TreeMap;
import org.testng.annotations.Test;

public final class BrowserApplicationTest {
    @Test
    public void runtimeOptimizationDefinesAndMapsAreIndependent() throws Exception {
        for (var runtime : new String[] {"development", "production"}) {
            for (var level : new String[] {"BUNDLE", "ADVANCED"}) {
                for (var maps : new String[] {"maps", "no_maps"}) {
                    var directory = Path.of(Objects.requireNonNull(
                            System.getProperty("application." + runtime + "." + level + "." + maps)));
                    var assets = new TreeMap<String, byte[]>();
                    try (var files = Files.walk(directory)) {
                        for (var file : files.filter(Files::isRegularFile).toList()) {
                            String name = directory.relativize(file).toString();
                            assets.put("/" + name, Files.readAllBytes(file));
                            if (name.endsWith(".js") || name.endsWith(".html") || name.endsWith(".css")) {
                                String contents = Files.readString(file);
                                if ("no_maps".equals(maps)) {
                                    assertFalse(contents.stripTrailing().endsWith("sourceMappingURL=app.js.map"), name);
                                    assertFalse(contents.contains("/*# sourceMappingURL="), name);
                                }
                            }
                            assertEquals(name.endsWith(".map") && "no_maps".equals(maps), false, name);
                        }
                    }
                    if ("BUNDLE".equals(level)) {
                        assertFalse(assets.containsKey("/index.html"));
                        assertTrue(assets.containsKey("/config.js"));
                        assets.put(
                                "/index.html",
                                ("<div id='app'></div><link rel='stylesheet' href='css/application.css'><script"
                                                + " src='config.js'></script><script src='react.js'></script><script"
                                                + " src='react-dom.js'></script><script src='app.js'></script>")
                                        .getBytes(StandardCharsets.UTF_8));
                    } else {
                        assertFalse(assets.containsKey("/config.js"));
                    }
                    assertEquals(Files.exists(directory.resolve("app.js.map")), "maps".equals(maps));
                    assertEquals(Files.exists(directory.resolve("css/application.css.map")), "maps".equals(maps));
                    var result = BrowserCoverage.runBytes(
                            Path.of(Objects.requireNonNull(System.getProperty("react4j.browser.metadata"))),
                            assets,
                            "window.applicationSettings && document.getElementById('icon').naturalWidth > 0 &&"
                                    + " document.getElementById('photo').naturalWidth > 0",
                            "Object.assign(window.applicationSettings, {developmentReact:"
                                    + " Object.prototype.hasOwnProperty.call(React.createElement('div'), '_store'),"
                                    + " padding: getComputedStyle(document.getElementById('card')).padding})");
                    assertTrue(result.getAsJsonPrimitive("text").isString());
                    assertTrue(result.getAsJsonPrimitive("flag").isBoolean());
                    assertTrue(result.getAsJsonPrimitive("number").isNumber());
                    assertTrue(result.getAsJsonPrimitive("viewNames").isString());
                    assertEquals(result.get("viewNames").getAsString(), "false");
                    assertEquals(result.get("padding").getAsString(), "24px");
                    assertEquals(result.get("text").getAsString(), "custom=string");
                    assertTrue(result.get("flag").getAsBoolean());
                    assertEquals(result.get("number").getAsDouble(), 12.5);
                    assertEquals(result.get("environment").getAsString(), runtime);
                    assertEquals(result.get("debug").getAsBoolean(), "development".equals(runtime));
                    assertEquals(result.get("developmentReact").getAsBoolean(), "development".equals(runtime));
                }
            }
        }
    }
}
