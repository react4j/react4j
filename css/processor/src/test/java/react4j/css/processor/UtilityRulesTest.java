package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.utility.language.UtilityLanguage;

public final class UtilityRulesTest {
    private record Result(int status, String text) {}

    private static Result run(String... args) {
        var bytes = new ByteArrayOutputStream();
        try (var output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            int code = UtilityRules.run(args, output);
            return new Result(code, bytes.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void authoringCommandsShareCheckedBehaviorAndKeepTheirExplicitFileBoundary() throws Exception {
        var directory = Files.createTempDirectory("utility-rules-command-");
        try {
            String rules = """
                @utility-rules;
                @tokens size <length> { small:2px; }
                @utility flex {display:flex;}
                @utility "pad-{v}" (v:tokens(size)|arbitrary(<length>)) {padding:param(v);}
                @variant hover {&:hover {@slot;}}
                """;
            var source = directory.resolve("rules.css");
            Files.writeString(source, rules);
            var snapshot = directory.resolve("checked.json");
            Files.writeString(
                    snapshot,
                    UtilityLanguage.bind(
                                    UtilityLanguage.prepare(new UtilityLanguage.Source("rules.css", rules)),
                                    "demo.Rules",
                                    new UtilityLanguage.Bindings(Set.of()))
                            .json());
            assertEquals(run("validate", source.toString()).status(), 0);
            assertTrue(run("--text", "validate", source.toString()).text().contains("Provider ownership"));
            var described = run("--text", "describe", source.toString(), snapshot.toString());
            assertEquals(described.status(), 0);
            assertTrue(described.text().contains("small=2px"), described.text());
            assertTrue(described.text().contains("&:hover"), described.text());
            var expanded = run("expand", snapshot.toString(), "flex", "pad-small");
            assertEquals(expanded.status(), 0);
            assertEquals(
                    JsonParser.parseString(expanded.text())
                            .getAsJsonObject()
                            .getAsJsonArray("expansions")
                            .size(),
                    2);
            assertEquals(
                    run("--text", "expand", snapshot.toString(), "flex", "missing")
                            .status(),
                    1);
            Files.writeString(source, "@utility-rules; @utility bad {padding:param(unknown);}");
            assertEquals(run("validate", source.toString()).status(), 1);
            assertTrue(run("--text", "validate", source.toString()).text().contains("U_PARAMETER"));
            assertEquals(
                    run("validate", directory.resolve("absent.css").toString()).status(), 2);
            assertEquals(
                    run("--text", "validate", directory.resolve("absent.css").toString())
                            .status(),
                    2);
            Files.writeString(snapshot, "{}");
            assertTrue(run("expand", snapshot.toString(), "flex").text().contains("U_DESCRIPTOR"));
            for (String[] args : new String[][] {
                {}, {"--text"}, {"invalid", "input"}, {"expand", "input"}, {"validate", "input", "provider", "extra"}
            }) {
                assertEquals(run(args).status(), 2);
            }
        } finally {
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
