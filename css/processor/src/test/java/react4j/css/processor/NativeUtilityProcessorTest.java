package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssNames;
import react4j.css.internal.CssRenamer;

@NullMarked
public final class NativeUtilityProcessorTest {
    private static final String IMPORTS = "package demo;\nimport react4j.css.*;\n";
    private static final String RULES = """
        @utility-rules;
        @scale spacing .25rem;
        @tokens color <color> { ink: rgb(var(--ink)); }
        @utility flex { display:flex; }
        @utility "p-{size}" (size: scale(spacing) | arbitrary(<length-percentage>)) { padding:param(size); }
        @utility "text-{tone}" (tone: tokens(color) | arbitrary(<color>)) { color:param(tone); }
        @utility owned { --accent: gold; }
        @variant hover { @media (hover:hover) { &:hover { @slot; } } }
        """;

    private static String library(String rules) {
        return IMPORTS + "@UtilityCssLibrary(value=\"\"\"\n" + rules
                + "\n"
                + "\"\"\",externalProperties={\"ink\",\"future\"},externalClasses={\"hook\"}) public class Utilities"
                + " {}";
    }

    private static String styles(String candidates) {
        return IMPORTS
                + "@UtilityStyles(Utilities.class) @Css(\"\"\"\n"
                + "color:red;\n"
                + "\"\"\") abstract class Button { static Button of(){return Styled_Button.get();} @UtilityCss(\""
                + candidates + "\") abstract String style(); }";
    }

    private static String text(CssProcessorTest.Result result, String path) {
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void nativeGettersUseEvaluatedConstantsInheritanceAndLocalCss() throws Exception {
        var result = CssProcessorTest.compile(
                Map.of(
                        "Utilities.java", library(RULES),
                        "Contract.java",
                                IMPORTS
                                        + "interface Contract { String PREFIX=\"hover:\";"
                                        + " @UtilityCss(PREFIX+\"text-ink p-3\") String style(); }",
                        "Button.java",
                                styles("flex")
                                        .replace("class Button {", "class Button implements Contract {")
                                        .replace("@UtilityCss(\"flex\") abstract String style();", "")),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(
                result.values().get("demo.Button.style"),
                "jcss-demo-Utilities--class-hover_003a_text_002d_ink jcss-demo-Utilities--class-p_002d_3"
                        + " jcss-demo-Button");
        assertTrue(text(result, "classes/demo/Button.css").contains("color:red"));
        var descriptor = JsonParser.parseString(text(result, "classes/demo/Utilities.css.rules.json"))
                .getAsJsonObject();
        assertEquals(descriptor.get("kind").getAsString(), "checked");
        assertEquals(descriptor.get("owner").getAsString(), "demo.Utilities");
        assertFalse(text(result, "classes/demo/Utilities.css").contains("padding"));
        assertTrue(text(result, "classes/demo/Utilities.css.json").contains("accent"));
    }

    @Test
    public void broaderTypesReachGeneratedAccessorsAndCheckedExpansionMetadata() throws Exception {
        String rules = RULES + """
            @scale duration 1ms;
            @utility "duration-{v}" (v: scale(duration) | arbitrary(<time>) within [0ms,*]) { transition-duration:param(v); }
            @utility "shadow-{v}" (v: arbitrary(<box-shadow>)) { box-shadow:param(v); }
            @utility "cols-{v}" (v: arbitrary(<grid-tracks>)) { grid-template-columns:param(v); }
            @utility "aspect-{v}" (v: bare(<ratio>)) { aspect-ratio:param(v); }
            @utility "content-{v}" (v: arbitrary(<string>)) { content:param(v); }
            """;
        String candidates =
                "duration-150 shadow-[0_-1px_5px_1px_rgba(var(--ink),.2)] cols-[200px_minmax(0,1fr)] aspect-16/9"
                        + " content-['Save_changes'] content-['Save\u2003changes']";
        var result = CssProcessorTest.compile(
                Map.of("Utilities.java", library(rules), "Button.java", styles(candidates)), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        var fragments = JsonParser.parseString(text(result, "classes/demo/Button.css.fragments.json"))
                .getAsJsonObject()
                .getAsJsonArray("fragments")
                .asList();
        assertEquals(fragments.size(), 6);
        String output = fragments.stream()
                .map(item -> item.getAsJsonObject().get("css").getAsString())
                .collect(Collectors.joining());
        for (var expected : List.of(
                "transition-duration:150ms",
                "0 -1px 5px 1px rgba(var(--ink),.2)",
                "200px minmax(0,1fr)",
                "aspect-ratio:16/9",
                "'Save changes'",
                "'Save\u2003changes'")) {
            assertTrue(output.contains(expected), output);
        }
        var invalid = CssProcessorTest.compile(
                Map.of("Utilities.java", library(rules), "Button.java", styles("duration-[-1ms] shadow-[0_0_-1px]")));
        assertFalse(invalid.success());
        assertTrue(invalid.diagnostics().contains("U_VALUE_RANGE"), invalid.diagnostics());
        assertTrue(invalid.diagnostics().contains("U_VALUE_TYPE"), invalid.diagnostics());
    }

    @Test
    public void conditionalVariantsBindNamesAndRejectMalformedUnusedDefinitions() throws Exception {
        String rules = RULES + """
            @utility setup {container:card / inline-size;--density:compact;}
            @variant "cq-{v}" (v:arbitrary(<length>)) {
              @container card (0px <= width < param(v)) {
                @container card style(--density:compact) { @starting-style {@slot;} }
              }
            }
            """;
        var result = CssProcessorTest.compile(
                Map.of("Utilities.java", library(rules), "Button.java", styles("cq-[24rem]:flex")), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        String fragments = text(result, "classes/demo/Button.css.fragments.json");
        assertTrue(fragments.contains("jcss-demo-Utilities--container-card"), fragments);
        assertTrue(fragments.contains("--jcss-demo-Utilities--density"), fragments);
        assertTrue(fragments.contains("@starting-style"), fragments);
        var invalid = CssProcessorTest.compile(
                Map.of("Utilities.java", library(rules + "@variant bad {@container (width > 3%) {@slot;}}")));
        assertFalse(invalid.success());
        assertTrue(invalid.diagnostics().contains("U_CONTAINER_CONDITION"), invalid.diagnostics());
    }

    @Test
    public void anchorsOwnTheirNamespaceAndExternalHooksCanFirstAppearDownstream() throws Exception {
        String rules = RULES + """
            @utility trigger {anchor-name:--trigger;}
            @utility panel {position-anchor:--trigger;anchor-scope:--trigger;left:anchor(--trigger left);}
            @utility "left-{v}" (v:arbitrary(<length-percentage>)) {left:param(v);}
            """;
        var result = CssProcessorTest.compile(
                Map.of(
                        "Utilities.java",
                        library(rules).replace("externalClasses=", "externalAnchors={\"--host\"},externalClasses="),
                        "Button.java",
                        styles("panel left-[anchor(--host_left)]")),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        String fragments = text(result, "classes/demo/Button.css.fragments.json");
        assertTrue(fragments.contains("--jcss-demo-Utilities--anchor-trigger"), fragments);
        assertTrue(fragments.contains("anchor(--host left)"), fragments);
        assertTrue(text(result, "classes/demo/Utilities.css.json").contains("ANCHOR"));
        var invalid = CssProcessorTest.compile(
                Map.of("Utilities.java", library(rules + "@utility bad {anchor-name:var(--future);}")));
        assertFalse(invalid.success());
        assertTrue(invalid.diagnostics().contains("U_DYNAMIC_NAME"), invalid.diagnostics());
    }

    @Test
    public void finiteUtilitiesRetainTheirExistingRawAnchorBehavior() throws Exception {
        String provider =
                IMPORTS + """
                    @UtilityCssLibrary(value=\
                    """ + "\"\"\"\n" + """
                    .anchor {anchor-name:var(--host);}
                    .panel {position-anchor:var(--host);left:anchor(--host left);}
                    """ + "\n\"\"\",externalProperties={\"host\"}) public class Utilities {}";
        var result = CssProcessorTest.compile(
                Map.of("Utilities.java", provider, "Button.java", styles("anchor panel")), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        String css = text(result, "classes/demo/Utilities.css");
        assertTrue(css.contains("anchor-name:var(--host)"), css);
        assertTrue(css.contains("anchor(--host left)"), css);
    }

    @Test
    public void candidateDiagnosticsLocateLiteralsAndConstantExpressionsAndKeepBatchPeers() throws Exception {
        String button = IMPORTS + """
            interface Tokens { String PREFIX="hover:"; }
            @UtilityStyles(Utilities.class) abstract class Button {
              @UtilityCss("text-[12px] flex") abstract String style();
              @UtilityCss(Tokens.PREFIX+"text-[3rem] missing") abstract String bad();
            }
            """;
        var result = CssProcessorTest.compile(Map.of("Utilities.java", library(RULES), "Button.java", button));
        assertFalse(result.success());
        String errors = result.diagnostics();
        for (String expected : List.of(
                "U_VALUE_TYPE",
                "U_UNKNOWN_UTILITY",
                "text-[12px]",
                "hover:text-[3rem]",
                "locationPrecision: literal",
                "locationPrecision: expression",
                "decodedOffset:",
                "Defined at demo/Utilities.java:")) {
            assertTrue(errors.contains(expected), errors);
        }
        assertFalse(errors.contains("Exception"), errors);
        assertFalse(result.files().containsKey("classes/demo/Button.css.fragments.json"));
    }

    @Test
    public void nativeProviderStabilityCoversOwnedSymbolsAndDownstreamCandidates() throws Exception {
        String provider = library(RULES + "@utility trigger {anchor-name:--trigger;}")
                .replace("public class Utilities {}", "@CssStable public class Utilities {}");
        var result = CssProcessorTest.compile(
                Map.of("Utilities.java", provider, "Button.java", styles("p-2")), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        var resource = JsonParser.parseString(text(result, "classes/demo/Utilities.css.json"))
                .getAsJsonObject();
        for (var symbol : resource.getAsJsonArray("symbols")) {
            assertTrue(symbol.getAsJsonObject().get("stable").getAsBoolean(), symbol.toString());
        }
        var fragments = JsonParser.parseString(text(result, "classes/demo/Button.css.fragments.json"))
                .getAsJsonObject();
        for (var symbol : fragments
                .getAsJsonArray("fragments")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("metadata")
                .getAsJsonArray("symbols")) {
            assertTrue(symbol.getAsJsonObject().get("stable").getAsBoolean(), symbol.toString());
        }
    }

    @Test
    public void nativeResourceStabilityKeepsTheSelectedOwnedProperty() throws Exception {
        String provider = IMPORTS
                + "@UtilityCssLibrary(\"\"\"\n"
                + "@utility-rules; @utility props {--a:1;--b:2;}\n"
                + "\"\"\") abstract class Utilities { @CssStable @CssCustomProperty(name=\"a\") abstract String a();"
                + " @CssCustomProperty(name=\"b\") abstract String b(); }";
        var result = CssProcessorTest.compile(Map.of("Utilities.java", provider));
        assertTrue(result.success(), result.diagnostics());
        for (var symbol : JsonParser.parseString(text(result, "classes/demo/Utilities.css.json"))
                .getAsJsonObject()
                .getAsJsonArray("symbols")) {
            var item = symbol.getAsJsonObject();
            assertEquals(
                    item.get("stable").getAsBoolean(),
                    "a".equals(item.get("name").getAsString()),
                    item.toString());
        }
    }

    @Test
    public void nativeProvidersRetainExistingSymbolAccessorContracts() throws Exception {
        String rules = RULES + "@keyframes pulse { to { opacity:1; } } @utility target { & #target {color:gold;} }";
        String provider = library(rules)
                .replace(
                        "public class Utilities {}",
                        "public abstract class Utilities { static Utilities of(){return Styled_Utilities.get();}"
                                + " @CssCustomProperty abstract String accent(); @CssId abstract String target();"
                                + " @CssAnimation abstract String pulse(); }");
        var result = CssProcessorTest.compile(Map.of("Utilities.java", provider), "demo.Utilities");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Utilities.accent"), "--jcss-demo-Utilities--accent");
        assertEquals(result.values().get("demo.Utilities.target"), "jcss-demo-Utilities--id-target");
        assertEquals(result.values().get("demo.Utilities.pulse"), "jcss-demo-Utilities--keyframes-pulse");
        var directory = Files.createTempDirectory("native-utility-exports-");
        try {
            writeClasses(result, directory);
            var consumer = CssProcessorTest.compile(
                    Map.of("Button.java", styles("p-2")),
                    List.of("-classpath", directory + File.pathSeparator + CssProcessorTest.applicationClasspath()));
            assertTrue(consumer.success(), consumer.diagnostics());
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void unusedDefinitionsAndCandidateFailuresAreCheckedTogether() throws Exception {
        var unused = CssProcessorTest.compile(
                Map.of("Utilities.java", library(RULES + "@utility bad { color:var(--missing); }")));
        assertFalse(unused.success());
        assertTrue(unused.diagnostics().contains("U_EXTERNAL_PROPERTY"), unused.diagnostics());
        var unusedHook = CssProcessorTest.compile(
                Map.of("Utilities.java", library(RULES + "@utility bad { & .probe { color:red; } }")));
        assertFalse(unusedHook.success());
        assertTrue(unusedHook.diagnostics().contains("U_EXTERNAL_CLASS"), unusedHook.diagnostics());
        var unusedToken = CssProcessorTest.compile(
                Map.of("Utilities.java", library(RULES + "@tokens unused <color> { x:var(--missing); }")));
        assertFalse(unusedToken.success());
        assertTrue(unusedToken.diagnostics().contains("U_EXTERNAL_PROPERTY"), unusedToken.diagnostics());
        var unusedAnchor = CssProcessorTest.compile(Map.of(
                "Utilities.java", library(RULES + "@tokens unused <length> { x:anchor-size(--missing width); }")));
        assertFalse(unusedAnchor.success());
        assertTrue(unusedAnchor.diagnostics().contains("U_ANCHOR_NAME"), unusedAnchor.diagnostics());
        var candidates = CssProcessorTest.compile(
                Map.of("Utilities.java", library(RULES), "Button.java", styles("missing text-[12px] [th]:p-2")));
        assertFalse(candidates.success());
        for (var code : List.of("U_UNKNOWN_UTILITY", "U_VALUE_TYPE", "U_SELECTOR_GUARD")) {
            assertTrue(candidates.diagnostics().contains(code), candidates.diagnostics());
        }
        var malformed =
                CssProcessorTest.compile(Map.of("Utilities.java", library("@utility-rules; .finite {color:red;}")));
        assertFalse(malformed.success());
        assertTrue(malformed.diagnostics().contains("U_SYNTAX"), malformed.diagnostics());
    }

    @Test
    public void separateAndHeaderConsumersRequestPreviouslyUnseenCandidates() throws Exception {
        var provider = CssProcessorTest.compile(Map.of(
                "Utilities.java",
                library(RULES).replace("public class Utilities {}", "@CssStable public class Utilities {}")));
        assertTrue(provider.success(), provider.diagnostics());
        var directory = Files.createTempDirectory("native-utility-provider-");
        try {
            writeClasses(provider, directory);
            String classpath = directory + File.pathSeparator + CssProcessorTest.applicationClasspath();
            var consumer = CssProcessorTest.compile(
                    Map.of("Button.java", styles("p-137 hover:text-[red]")),
                    List.of("-classpath", classpath),
                    "demo.Button");
            assertTrue(consumer.success(), consumer.diagnostics());
            var fragments = JsonParser.parseString(text(consumer, "classes/demo/Button.css.fragments.json"))
                    .getAsJsonObject();
            assertTrue(fragments.toString().contains("\"stable\":true"), fragments.toString());
            assertTrue(Objects.requireNonNull(consumer.values().get("demo.Button.style"))
                    .contains("class-p_002d_137"));
            Files.delete(directory.resolve("demo/Utilities.css.rules.json"));
            var missingDescriptor =
                    CssProcessorTest.compile(Map.of("Button.java", styles("p-138")), List.of("-classpath", classpath));
            assertFalse(missingDescriptor.success());
            assertTrue(
                    missingDescriptor.diagnostics().contains("Missing checked native utility descriptor"),
                    missingDescriptor.diagnostics());
            Files.delete(directory.resolve("demo/Utilities.css.json"));
            Files.delete(directory.resolve("demo/Utilities.css.map"));
            Files.delete(directory.resolve("demo/Utilities.css"));
            var header = CssProcessorTest.compile(
                    Map.of("Button.java", styles("p-138")), List.of("-classpath", classpath), "demo.Button");
            assertTrue(header.success(), header.diagnostics());
            assertTrue(Objects.requireNonNull(header.values().get("demo.Button.style"))
                    .contains("class-p_002d_138"));
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void inlineHeaderRequestsRecoverAuthoredOriginsAtCollectionAndCompression() throws Exception {
        String java = library(RULES);
        var provider = CssProcessorTest.compile(Map.of("Utilities.java", java));
        assertTrue(provider.success(), provider.diagnostics());
        var directory = Files.createTempDirectory("native-header-origins-");
        try {
            var authority = directory.resolve("provider");
            var headers = directory.resolve("headers");
            var consumerTree = directory.resolve("consumer");
            writeClasses(provider, authority);
            writeClasses(provider, headers);
            try (var files = Files.walk(headers)) {
                for (var file : files.filter(Files::isRegularFile)
                        .filter(file -> !file.toString().endsWith(".class"))
                        .toList()) {
                    Files.delete(file);
                }
            }
            var consumer = CssProcessorTest.compile(
                    Map.of("Button.java", styles("p-137")),
                    List.of("-classpath", headers + File.pathSeparator + CssProcessorTest.applicationClasspath()));
            assertTrue(consumer.success(), consumer.diagnostics());
            assertTrue(
                    text(consumer, "classes/demo/Button.css.fragments.json").contains("demo/Utilities.utilities.css"));
            writeClasses(consumer, consumerTree);
            var catalogue = CssCatalogue.read(List.of(authority, consumerTree));
            var resource = Objects.requireNonNull(catalogue.resources().get("demo.Utilities"));
            var names = CssNames.create(catalogue, new CssNames.Policy(true, "app-"));
            var renamed = new CssRenamer(
                            names,
                            catalogue.resources().values().stream()
                                    .map(CssCatalogue.Resource::metadata)
                                    .toList())
                    .rename(resource);
            assertFalse(renamed.css().contains("class-p_002d_137"));
            var map = renamed.map();
            assertEquals(map.getAsJsonArray("sources").get(0).getAsString(), "demo/Utilities.java");
            assertEquals(map.getAsJsonArray("sourcesContent").get(0).getAsString(), java);
            var mapped = new SourceMapConsumerV3();
            mapped.parse(map.toString());
            int at = renamed.css().indexOf("padding:");
            assertTrue(at >= 0, renamed.css());
            int line = (int) renamed.css()
                            .substring(0, at)
                            .chars()
                            .filter(c -> '\n' == c)
                            .count()
                    + 1;
            int column = at - renamed.css().lastIndexOf('\n', at - 1);
            var origin = Objects.requireNonNull(mapped.getMappingForLine(line, column));
            int authored = java.indexOf("padding:param(size)");
            assertEquals(origin.getOriginalFile(), "demo/Utilities.java");
            assertEquals(
                    origin.getLineNumber(),
                    (int) java.substring(0, authored)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(origin.getColumnPosition(), authored - java.lastIndexOf('\n', authored - 1));
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    @Test
    public void nativeFilesUsePreparedSidecarsAndValidateSourceIdentity() throws Exception {
        var directory = Files.createTempDirectory("native-utility-input-");
        try {
            var source = directory.resolve("rules.css");
            String authored = RULES.replace("\n", "\r\n");
            Files.writeString(source, authored);
            var jar = directory.resolve("rules.jar");
            packageInput(source, jar);
            try (var zip = new ZipFile(jar.toFile())) {
                assertTrue(zip.stream().anyMatch(entry -> entry.getName().endsWith(".rules.json")));
                assertFalse(zip.stream().anyMatch(entry -> entry.getName().endsWith(".classes.json")));
            }
            var options = List.of("-classpath", jar + File.pathSeparator + CssProcessorTest.applicationClasspath());
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Utilities.java",
                            IMPORTS
                                    + "@UtilityCssLibrary(file=\"rules.css\",externalProperties=\"ink\") public class"
                                    + " Utilities {}",
                            "Button.java",
                            styles("p-250")),
                    options,
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertTrue(Objects.requireNonNull(result.values().get("demo.Button.style"))
                    .contains("class-p_002d_250"));
            assertTrue(text(result, "classes/demo/Utilities.css.rules.json").contains("demo/rules.css"));
            var tree = directory.resolve("classes");
            writeClasses(result, tree);
            var resource = Objects.requireNonNull(
                    CssCatalogue.read(List.of(tree)).resources().get("demo.Utilities"));
            assertEquals(resource.map().getAsJsonArray("sources").get(0).getAsString(), "demo/rules.css");
            assertEquals(resource.map().getAsJsonArray("sourcesContent").get(0).getAsString(), authored);
            var changed = directory.resolve("changed.jar");
            try (var original = new ZipFile(jar.toFile());
                    var output = new ZipOutputStream(Files.newOutputStream(changed))) {
                for (var entry : original.stream().toList()) {
                    output.putNextEntry(new ZipEntry(entry.getName()));
                    var bytes = original.getInputStream(entry).readAllBytes();
                    output.write(
                            entry.getName().endsWith(".css")
                                    ? (RULES + "\n/* changed */").getBytes(StandardCharsets.UTF_8)
                                    : bytes);
                    output.closeEntry();
                }
            }
            var stale = CssProcessorTest.compile(
                    Map.of(
                            "Utilities.java",
                            IMPORTS
                                    + "@UtilityCssLibrary(file=\"rules.css\",externalProperties=\"ink\") class"
                                    + " Utilities {}"),
                    List.of("-classpath", changed + File.pathSeparator + CssProcessorTest.applicationClasspath()));
            assertFalse(stale.success());
            assertTrue(stale.diagnostics().contains("differs from its source"), stale.diagnostics());
        } finally {
            CssProcessorTest.delete(directory);
        }
    }

    private static void packageInput(Path source, Path jar) throws Exception {
        String classpath = Boolean.getBoolean("css.processor.implementation")
                ? System.getProperty("java.class.path")
                : CssProcessorTest.processorJar().toString();
        var process = new ProcessBuilder(
                        Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                        "-classpath",
                        classpath,
                        "react4j.css.processor.CssInputJar",
                        jar.toString(),
                        "demo/rules.css",
                        source.toString())
                .redirectErrorStream(true)
                .start();
        var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(process.waitFor(), 0, output);
    }

    private static void writeClasses(CssProcessorTest.Result result, Path directory) throws Exception {
        for (var entry : result.files().entrySet()) {
            if (entry.getKey().startsWith("classes/")) {
                var path = directory.resolve(entry.getKey().substring("classes/".length()));
                Files.createDirectories(path.getParent());
                Files.write(path, Base64.getDecoder().decode(entry.getValue()));
            }
        }
    }
}
