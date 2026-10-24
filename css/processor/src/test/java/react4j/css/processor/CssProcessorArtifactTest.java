package react4j.css.processor;

import static org.testng.Assert.*;
import static react4j.css.processor.CssProcessorTest.applicationClasspath;
import static react4j.css.processor.CssProcessorTest.delete;
import static react4j.css.processor.CssProcessorTest.processorJar;
import static react4j.css.processor.CssProcessorTest.source;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssProcessorArtifactTest {
    @Test
    public void bundledClassesUseProcessorNamespace() throws Exception {
        try (var jar = new JarFile(processorJar().toFile())) {
            var unexpectedClasses = jar.stream()
                    .map(entry -> entry.getName())
                    .filter(name -> name.endsWith(".class") && !name.startsWith("react4j/css/processor/"))
                    .toList();
            assertTrue(unexpectedClasses.isEmpty(), unexpectedClasses.toString());
        }
    }

    private record Command(int exit, String output, String errors) {}

    private static Command utilityCommand(String... arguments) throws Exception {
        var command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp",
                processorJar().toString(),
                "react4j.css.processor.UtilityRules"));
        command.addAll(List.of(arguments));
        var process = new ProcessBuilder(command).start();
        var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        var errors = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        return new Command(process.waitFor(), output, errors);
    }

    private static JsonObject json(Command result, int exit) {
        assertEquals(result.exit(), exit, result.output() + result.errors());
        return JsonParser.parseString(result.output()).getAsJsonObject();
    }

    @Test
    public void packagedJvmCommandsPrepareDescribeAndExpandUsingProviderSnapshots() throws Exception {
        var directory = Files.createTempDirectory("utility-authoring-");
        try {
            String rules = """
                @utility-rules;
                @scale spacing .25rem;
                @utility flex {display:flex;}
                @utility "pad-{v}" (v:scale(spacing)|arbitrary(<length>) within [0,*]) {padding:param(v);}
                @variant hover {&:hover {@slot;}}
                """;
            String provider = "package demo; import react4j.css.*; @UtilityCssLibrary(value=\"\"\"\n" + rules
                    + "\n\"\"\",externalProperties={\"theme\"}) public class Utilities {}";
            var compilation = CssProcessorTest.compile(Map.of("Utilities.java", provider));
            assertTrue(compilation.success(), compilation.diagnostics());
            var snapshot = directory.resolve("provider.checked.json");
            Files.write(
                    snapshot,
                    Base64.getDecoder()
                            .decode(Objects.requireNonNull(
                                    compilation.files().get("classes/demo/Utilities.css.rules.json"))));
            var input = directory.resolve("rules.css");
            Files.writeString(input, rules);
            var prepared = json(utilityCommand("validate", input.toString()), 0);
            assertEquals(prepared.get("status").getAsString(), "prepared");
            assertEquals(prepared.getAsJsonArray("pendingChecks").size(), 1);
            var described = json(utilityCommand("describe", input.toString(), snapshot.toString()), 0);
            assertEquals(described.get("status").getAsString(), "checked");
            assertEquals(described.getAsJsonObject("scales").get("spacing").getAsString(), ".25rem");
            assertTrue(described
                    .getAsJsonArray("variants")
                    .get(0)
                    .getAsJsonObject()
                    .get("template")
                    .getAsString()
                    .contains("&:hover"));
            var checked = directory.resolve("checked.json");
            Files.writeString(checked, described.get("library").toString());
            var expanded = json(
                    utilityCommand(
                            "expand", checked.toString(), "hover:pad-2", "pad-[var(--theme)]", "flxe", "pad-[-1px]"),
                    1);
            assertEquals(expanded.getAsJsonArray("expansions").size(), 2);
            assertEquals(expanded.getAsJsonArray("diagnostics").size(), 2);
            assertEquals(
                    expanded.get("fingerprint").getAsString(),
                    described.get("fingerprint").getAsString());
            assertTrue(expanded.getAsJsonArray("expansions").toString().contains("browser-dependent"));
            assertTrue(expanded.getAsJsonArray("expansions").toString().contains("0.5rem"));
            var human = utilityCommand("--text", "expand", checked.toString(), "flxe");
            assertEquals(human.exit(), 1, human.errors());
            assertTrue(human.output().contains("suggestions: flex"), human.output());
            Files.writeString(checked, "{}");
            assertEquals(
                    json(utilityCommand("expand", checked.toString(), "flex"), 2)
                            .getAsJsonArray("diagnostics")
                            .get(0)
                            .getAsJsonObject()
                            .get("code")
                            .getAsString(),
                    "U_DESCRIPTOR");
            assertEquals(
                    json(utilityCommand("unknown"), 2)
                            .getAsJsonArray("diagnostics")
                            .get(0)
                            .getAsJsonObject()
                            .get("code")
                            .getAsString(),
                    "U_USAGE");
        } finally {
            delete(directory);
        }
    }

    @Test
    public void formattingCanBeDisabledWithoutJdkExports() throws Exception {
        Path directory = Files.createTempDirectory("css-format-no-exports-");
        try {
            Path input = directory.resolve("Button.java");
            Files.writeString(input, source("demo", "    color: red;", ""));
            var command = List.of(
                    Path.of(System.getProperty("java.home"), "bin", "javac").toString(),
                    "--release",
                    "21",
                    "-classpath",
                    applicationClasspath(),
                    "--processor-path",
                    processorJar().toString(),
                    "-d",
                    directory.resolve("classes").toString(),
                    "-s",
                    directory.resolve("generated").toString(),
                    "-Areact4j.css.format_generated_source=false",
                    input.toString());
            var process = new ProcessBuilder(command).redirectErrorStream(true).start();
            var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            assertEquals(exitCode, 0, output);
            assertFalse(output.contains("warning:"), output);
            assertTrue(Files.exists(directory.resolve("classes/demo/Styled_Button.class")));
            assertTrue(Files.exists(directory.resolve("classes/demo/Button.css.map")));
        } finally {
            delete(directory);
        }
    }
}
