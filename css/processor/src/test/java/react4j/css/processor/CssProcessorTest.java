package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.Css;
import react4j.testing.StrictGeneratedCode;

@NullMarked
public final class CssProcessorTest {
    record Result(boolean success, String diagnostics, Map<String, String> files, Map<String, String> values) {}

    static Path processorJar() {
        return Path.of(Objects.requireNonNull(System.getProperty("css.processor.jar")));
    }

    static String applicationClasspath() throws Exception {
        return Path.of(Css.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                + File.pathSeparator
                + Path.of(NullMarked.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI());
    }

    private static Path fixtureDir() {
        return Path.of(System.getProperty("react4j.fixture_dir", "css/processor/src/test/fixtures"));
    }

    @DataProvider(name = "fixtures")
    public Object[][] fixtures() throws Exception {
        Object[][] fixtures = {
            new Object[] {"input", List.of("com/example/assets/Owner.java")},
            new Object[] {"input", List.of("com/example/interface_symbols/Owner.java")},
            new Object[] {"input", List.of("com/example/interfaces/Owner.java")},
            new Object[] {"input", List.of("com/example/symbols/Owner.java")},
            new Object[] {"bad_input", List.of("com/example/attribute_id/Button.java")},
            new Object[] {"bad_input", List.of("com/example/conflicting_symbol/Button.java")},
            new Object[] {"input", List.of("com/example/properties/Owner.java")},
            new Object[] {"bad_input", List.of("com/example/missing_property/Button.java")},
            new Object[] {"input", List.of("com/example/global/Defaults.java", "com/example/global/Theme.java")},
            new Object[] {"input", List.of("com/example/global_important/Defaults.java")},
            new Object[] {"bad_input", List.of("com/example/import_wrong_kind/Button.java")},
            new Object[] {"bad_input", List.of("com/example/bad_accessor/Button.java")},
            new Object[] {"bad_input", List.of("com/example/bare_conditional/Button.java")},
            new Object[] {"bad_input", List.of("com/example/constant/Button.java")},
            new Object[] {"bad_input", List.of("com/example/constructor/Button.java")},
            new Object[] {"bad_input", List.of("com/example/css_wide_animation_list/Button.java")},
            new Object[] {"bad_input", List.of("com/example/duplicate_keyframes/Button.java")},
            new Object[] {"bad_input", List.of("com/example/dynamic_animation_name/Button.java")},
            new Object[] {"bad_input", List.of("com/example/dynamic_animation_shorthand/Button.java")},
            new Object[] {"bad_input", List.of("com/example/escaped_animation_property/Button.java")},
            new Object[] {"bad_input", List.of("com/example/escaped_selector/Button.java")},
            new Object[] {"bad_input", List.of("com/example/extra_animation_time/Button.java")},
            new Object[] {"bad_input", List.of("com/example/factory_conflict/Button.java")},
            new Object[] {"bad_input", List.of("com/example/generic_owner/Button.java")},
            new Object[] {"bad_input", List.of("com/example/important/Button.java")},
            new Object[] {
                "bad_input", List.of("com/example/inheritance/Base.java", "com/example/inheritance/Button.java")
            },
            new Object[] {"bad_input", List.of("com/example/instance_field/Button.java")},
            new Object[] {"bad_input", List.of("com/example/instance_initializer/Button.java")},
            new Object[] {"bad_input", List.of("com/example/keyframe_animation_binding/Button.java")},
            new Object[] {"bad_input", List.of("com/example/keyframe_important/Button.java")},
            new Object[] {"bad_input", List.of("com/example/keyframe_percentage/Button.java")},
            new Object[] {"bad_input", List.of("com/example/malformed/Button.java")},
            new Object[] {"bad_input", List.of("com/example/missing_selector/Button.java")},
            new Object[] {"bad_input", List.of("com/example/missing_style/Button.java")},
            new Object[] {"bad_input", List.of("com/example/multiple_animation_names/Button.java")},
            new Object[] {"bad_input", List.of("com/example/negative_animation_duration/Button.java")},
            new Object[] {"bad_input", List.of("com/example/negative_animation_iterations/Button.java")},
            new Object[] {"bad_input", List.of("com/example/nested_keyframes/Button.java")},
            new Object[] {"bad_input", List.of("com/example/nonstatic_nested/Outer.java")},
            new Object[] {"bad_input", List.of("com/example/plain_literal/Button.java")},
            new Object[] {"bad_input", List.of("com/example/prefixed_animation/Button.java")},
            new Object[] {"bad_input", List.of("com/example/prefixed_keyframe/Button.java")},
            new Object[] {"bad_input", List.of("com/example/private_constructor/Button.java")},
            new Object[] {"bad_input", List.of("com/example/private_enclosing/Outer.java")},
            new Object[] {"bad_input", List.of("com/example/private_nested/Outer.java")},
            new Object[] {"bad_input", List.of("com/example/quoted_animation/Button.java")},
            new Object[] {"bad_input", List.of("com/example/reserved_keyframe/Button.java")},
            new Object[] {"bad_input", List.of("com/example/sealed_owner/Button.java")},
            new Object[] {"bad_input", List.of("com/example/selector_typo/Button.java")},
            new Object[] {"bad_input", List.of("com/example/static_field/Button.java")},
            new Object[] {"bad_input", List.of("com/example/unknown_animation/Button.java")},
            new Object[] {"bad_input", List.of("com/example/unowned_selector/Button.java")},
            new Object[] {"bad_input", List.of("com/example/unsupported_at_rule/Button.java")},
            new Object[] {"bad_input", List.of("com/example/unsupported_pseudo/Button.java")},
            new Object[] {"input", List.of("com/example/basic/Button.java")},
            new Object[] {"input", List.of("com/example/conditions/Button.java")},
            new Object[] {"input", List.of("Button.java")},
            new Object[] {"input", List.of("com/example/deprecated/Button.java")},
            new Object[] {"input", List.of("com/example/escapes/Button.java")},
            new Object[] {"input", List.of("com/example/keyframes/Button.java")},
            new Object[] {
                "input", List.of("com/example/naming/biz_ui/Button.java", "com/example/naming_biz/ui/Button.java")
            },
            new Object[] {"input", List.of("com/example/nested/Owner.java")},
            new Object[] {"input", List.of("com/example/selectors/Button.java")},
            new Object[] {"input", List.of("com/example/shared/Button.java", "com/example/shared/Pulse.java")},
            new Object[] {"input", List.of("com/example/shadowed/Override.java", "com/example/shadowed/String.java")},
            new Object[] {
                "input",
                List.of(
                        "com/example/singleton/Owner.java",
                        "com/example/singleton/PackageStyle.java",
                        "com/example/singleton/PublicStyle.java")
            },
        };
        for (var family : List.of("input", "bad_input")) {
            try (var files = Files.walk(fixtureDir().resolve(family))) {
                var actual = files.filter(path -> path.toString().endsWith(".java"))
                        .map(path ->
                                fixtureDir().resolve(family).relativize(path).toString())
                        .collect(Collectors.toCollection(TreeSet::new));
                var expected = new TreeSet<String>();
                for (var fixture : fixtures) {
                    if (family.equals(fixture[0])) {
                        for (var file : (List<?>) fixture[1]) {
                            expected.add((String) file);
                        }
                    }
                }
                assertEquals(actual, expected, "Complete " + family + " fixture set differs");
            }
        }
        var modes = new ArrayList<Object[]>();
        for (var fixture : fixtures) {
            for (boolean formatted : List.of(false, true)) {
                modes.add(new Object[] {fixture[0], fixture[1], formatted});
            }
        }
        return modes.toArray(Object[][]::new);
    }

    @Test(dataProvider = "fixtures")
    public void goldenFixtures(String family, List<String> names, boolean formatted) throws Exception {
        var sources = new TreeMap<String, String>();
        var packages = new TreeSet<Path>();
        for (var name : names) {
            sources.put(name, Files.readString(fixtureDir().resolve(family).resolve(name)));
            Path parent = Path.of(name).getParent();
            packages.add(null == parent ? Path.of("") : parent);
        }
        String label = names.toString();
        var result = compile(sources, List.of("-Areact4j.css.format_generated_source=" + formatted));
        assertEquals(result.success(), "input".equals(family), label + ": " + result.diagnostics());
        if (result.success()) {
            assertEquals(result.diagnostics(), "", label);
        }
        var actual = new TreeMap<String, String>();
        for (var file : result.files().entrySet()) {
            if (!file.getKey().endsWith(".class")) {
                actual.put(
                        file.getKey().substring(file.getKey().indexOf('/') + 1),
                        new String(Base64.getDecoder().decode(file.getValue()), StandardCharsets.UTF_8));
            }
        }
        if (!result.success()) {
            assertTrue(actual.isEmpty(), label + ": Invalid input generated files");
            actual.put(packages.first().resolve("diagnostics.txt").toString(), result.diagnostics());
        }
        Path expected = fixtureDir().resolve(formatted ? "expectedFormatted" : "expected");
        var maintained = new TreeMap<String, String>();
        for (var packageName : packages) {
            try (var files = Files.list(expected.resolve(packageName))) {
                for (var file : files.filter(Files::isRegularFile)
                        .filter(path -> !path.getFileName().toString().equals("BUILD.bazel"))
                        .sorted()
                        .toList()) {
                    maintained.put(expected.relativize(file).toString(), Files.readString(file));
                }
            }
        }
        if (Boolean.getBoolean("react4j.output_fixture_data")) {
            for (var name : maintained.keySet()) {
                Files.delete(expected.resolve(name));
            }
            for (var entry : actual.entrySet()) {
                Path target = expected.resolve(entry.getKey());
                Files.createDirectories(target.getParent());
                Files.writeString(target, entry.getValue());
            }
        } else {
            assertEquals(actual.keySet(), maintained.keySet(), label + ": Complete generated output set differs");
            for (var entry : actual.entrySet()) {
                assertEquals(entry.getValue(), maintained.get(entry.getKey()), label + "/" + entry.getKey());
            }
        }
    }

    @Test
    public void strictCheckerRejectsInvalidGeneratedBody() throws Exception {
        var compiled = compile(Map.of("Button.java", source("demo", "    color: red;", "")));
        assertTrue(compiled.success(), compiled.diagnostics());
        Path directory = Files.createTempDirectory("css-checker-control-");
        try {
            for (var entry : compiled.files().entrySet()) {
                Path path = directory.resolve(entry.getKey());
                Files.createDirectories(path.getParent());
                Files.write(path, Base64.getDecoder().decode(entry.getValue()));
            }
            Path source = directory.resolve("generated/demo/Styled_Button.java");
            String text = Files.readString(source);
            int end = text.lastIndexOf('}');
            Files.writeString(
                    source,
                    text.substring(0, end) + "static java.lang.String invalidBody() { return null; }\n"
                            + text.substring(end));
            var failure = expectThrows(
                    AssertionError.class,
                    () -> StrictGeneratedCode.check(
                            directory.resolve("generated"), directory.resolve("classes"), applicationClasspath()));
            String message = failure.getMessage();
            assertNotNull(message);
            assert null != message;
            assertTrue(message.contains("[NullAway] returning @Nullable expression"), message);
        } finally {
            delete(directory);
        }
    }

    static void delete(Path directory) throws Exception {
        try (var files = Files.walk(directory)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }

    static String source(String packageName, String css, String methods) {
        return "package " + packageName + ";\nimport react4j.css.Css;\n@Css(\"\"\"\n"
                + css + "\n    \"\"\")\nabstract class Button {\n"
                + "static Button of() { return Styled_Button.get(); }\n"
                + "abstract String style();\n" + methods + "\n}\n";
    }

    static Result compile(Map<String, String> sources, String... inspect) throws Exception {
        return compile(sources, List.of(), inspect);
    }

    static Result compile(Map<String, String> sources, List<String> processorOptions, String... inspect)
            throws Exception {
        Path directory = Files.createTempDirectory("css-test-");
        try {
            Path generated = Files.createDirectory(directory.resolve("generated"));
            Path classes = Files.createDirectory(directory.resolve("classes"));
            var paths = new ArrayList<Path>();
            for (var entry : sources.entrySet()) {
                Path path = directory.resolve(entry.getKey());
                Files.createDirectories(path.getParent());
                Files.writeString(path, entry.getValue());
                paths.add(path);
            }
            var compiler = ToolProvider.getSystemJavaCompiler();
            var diagnostics = new DiagnosticCollector<JavaFileObject>();
            boolean success;
            String classpath = applicationClasspath();
            try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
                var options = new ArrayList<>(List.of(
                        "--release",
                        "21",
                        "-Xlint:deprecation,removal",
                        "-classpath",
                        classpath,
                        "-d",
                        classes.toString(),
                        "-s",
                        generated.toString()));
                if (!Boolean.getBoolean("css.processor.implementation")) {
                    options.addAll(List.of("--processor-path", processorJar().toString()));
                }
                options.addAll(processorOptions);
                classpath = options.get(options.lastIndexOf("-classpath") + 1);
                var task = compiler.getTask(
                        null, manager, diagnostics, options, null, manager.getJavaFileObjectsFromPaths(paths));
                if (Boolean.getBoolean("css.processor.implementation")) {
                    task.setProcessors(List.of((Processor) Class.forName("react4j.css.processor.CssProcessor")
                            .getConstructor()
                            .newInstance()));
                }
                success = task.call();
            }
            if (success) {
                StrictGeneratedCode.check(generated, classes, classpath);
            }
            var files = new LinkedHashMap<String, String>();
            for (var root : List.of(generated, classes)) {
                try (var stream = Files.walk(root)) {
                    for (var file : stream.filter(Files::isRegularFile).sorted().toList()) {
                        files.put(
                                root.getFileName() + "/" + root.relativize(file),
                                Base64.getEncoder().encodeToString(Files.readAllBytes(file)));
                    }
                }
            }
            var values = new LinkedHashMap<String, String>();
            if (success) {
                var urls = new ArrayList<URL>();
                urls.add(classes.toUri().toURL());
                for (var entry : classpath.split(Pattern.quote(File.pathSeparator))) {
                    urls.add(Path.of(entry).toUri().toURL());
                }
                try (var loader =
                        new URLClassLoader(urls.toArray(URL[]::new), CssProcessorTest.class.getClassLoader())) {
                    for (var name : inspect) {
                        var type = loader.loadClass(name);
                        var factory = type.getDeclaredMethod("of");
                        factory.setAccessible(true);
                        var instance = factory.invoke(null);
                        for (var method : instance.getClass().getDeclaredMethods()) {
                            if (Modifier.isStatic(method.getModifiers()) || String.class != method.getReturnType()) {
                                continue;
                            }
                            method.setAccessible(true);
                            values.put(name + "." + method.getName(), (String) method.invoke(instance));
                        }
                    }
                }
            }
            String messages = diagnostics.getDiagnostics().stream()
                    .map(diagnostic -> {
                        var source = diagnostic.getSource();
                        String location = null == source
                                ? ""
                                : source.getName().replace(directory + "/", "") + ":" + diagnostic.getLineNumber() + ":"
                                        + diagnostic.getColumnNumber() + ": ";
                        return diagnostic.getKind() + " " + location
                                + diagnostic.getMessage(Locale.ROOT).replace(directory + "/", "") + "\n";
                    })
                    .collect(Collectors.joining());
            return new Result(success, messages, files, values);
        } finally {
            delete(directory);
        }
    }

    @Test
    public void generatedSourceFormattingIsOptionalAndEnabledByDefault() throws Exception {
        var sources = Map.of(
                "Button.java",
                source("demo", "    color: red;\n    &.success { color: green; }", "abstract String success();")
                        .replace("@Css(", "@Deprecated\n@Css("));
        var defaults = compile(sources, "demo.Button");
        var formatted = compile(sources, List.of("-Areact4j.css.format_generated_source=true"), "demo.Button");
        var unformatted = compile(sources, List.of("-Areact4j.css.format_generated_source=false"), "demo.Button");
        for (var result : List.of(defaults, formatted, unformatted)) {
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.diagnostics(), "");
            assertEquals(result.values(), defaults.values());
            assertEquals(result.files().keySet(), defaults.files().keySet());
            for (var file : defaults.files().keySet()) {
                if (file.endsWith(".css") || file.endsWith(".map")) {
                    assertEquals(result.files().get(file), defaults.files().get(file), file);
                }
            }
        }
        assertEquals(defaults.files(), formatted.files());
        assertNotEquals(
                formatted.files().get("generated/demo/Styled_Button.java"),
                unformatted.files().get("generated/demo/Styled_Button.java"));
    }

    @Test
    public void processorDependenciesAreNotApplicationDependencies() throws Exception {
        for (var type : List.of(
                "com.helger.css.CSSSourceLocation",
                "com.palantir.javapoet.TypeSpec",
                "org.realityforge.proton.AbstractStandardProcessor")) {
            String text = source("demo", "    color: red;", "")
                    .replace("abstract String style();", "abstract String style(); " + type + " dependency;");
            var result = compile(Map.of("Button.java", text));
            assertFalse(result.success());
            assertTrue(
                    result.diagnostics()
                            .contains("package " + type.substring(0, type.lastIndexOf('.')) + " does not exist"),
                    result.diagnostics());
        }
    }

    @Test
    public void bindingsAndIndependentCompilationsAreReproducible() throws Exception {
        String a =
                source("com.biz_ui", "    color: red;\n    &.success { color: green; }", "abstract String success();");
        String b = source("com_biz.ui", "    color: blue;", "");
        var first = compile(Map.of("a/Button.java", a), "com.biz_ui.Button");
        assertTrue(first.success(), first.diagnostics());
        assertEquals(first.values().get("com.biz_ui.Button.style"), "jcss-com-biz_005f_ui-Button");
        assertEquals(
                first.values().get("com.biz_ui.Button.success"),
                "jcss-com-biz_005f_ui-Button jcss-com-biz_005f_ui-Button--success");
        var second = compile(Map.of("b/Button.java", b), "com_biz.ui.Button");
        assertTrue(second.success(), second.diagnostics());
        assertNotEquals(
                first.values().get("com.biz_ui.Button.style"), second.values().get("com_biz.ui.Button.style"));
        var combined = new LinkedHashMap<String, String>();
        combined.put("a/Button.java", a);
        combined.put("b/Button.java", b);
        var together = compile(combined);
        assertTrue(together.success(), together.diagnostics());
        var expected = new LinkedHashMap<>(first.files());
        expected.putAll(second.files());
        assertEquals(together.files(), expected);
        var reverse = new LinkedHashMap<String, String>();
        reverse.put("b/Button.java", b);
        reverse.put("a/Button.java", a);
        assertEquals(compile(reverse).files(), together.files());
        assertEquals(compile(combined).files(), together.files());
    }

    @Test
    public void parserRejectsMismatchedAndUnownedSelectorsBeforeGeneration() throws Exception {
        for (var css : List.of(
                "&.sucess { color: red; }",
                "/* &.success {} */ color: red;",
                "content: \"&.success\";",
                "& .success { color: red; }",
                "color red;")) {
            var result = compile(Map.of("Button.java", source("demo", "    " + css, "abstract String success();")));
            assertFalse(result.success(), css);
            assertTrue(result.files().isEmpty(), result.files().toString());
            assertTrue(result.diagnostics().contains("Button.java:"), result.diagnostics());
        }
    }

    @Test
    public void diagnosticsFollowJavaEscapesAndContinuedLines() throws Exception {
        String text = source(
                "demo",
                "    color: red;\\\n    background-color: white;\n    &.sucess { color: blue; }",
                "abstract String success();");
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Button.java:6:6: Local selector .sucess"), result.diagnostics());
    }

    @Test
    public void animationDiagnosticsMapMultilineListsThroughJavaEscapesAndCrLf() throws Exception {
        String text = source(
                        "demo",
                        "    @keyframes pulse {}\n    animation: pu\\\n    lse 1s;\n"
                                + "    animation-name:\n      pulse,\n      typo;",
                        "")
                .replace("\n", "\r\n");
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success());
        assertTrue(
                result.diagnostics().contains("Button.java:9:7: Unknown local animation name: typo"),
                result.diagnostics());
        assertTrue(result.files().isEmpty(), result.files().keySet().toString());
    }

    @Test
    public void authorErrorsCannotBeSuppressedAndRemainLocalToTheirOwner() throws Exception {
        String peer = source("demo", "    @keyframes pulse {}", "").replace("Button", "Peer");
        for (var name : List.of("pulse", "jcss-demo-Peer--keyframes-pulse")) {
            String bad = source("demo", "    animation-name: " + name + ";", "")
                    .replace("@Css(", "@SuppressWarnings(\"all\") @Css(");
            var result = compile(Map.of("Button.java", bad, "Peer.java", peer));
            assertFalse(result.success());
            assertEquals(
                    result.diagnostics()
                            .lines()
                            .filter(line -> line.startsWith("ERROR "))
                            .count(),
                    1L,
                    result.diagnostics());
            assertFalse(result.diagnostics().contains("WARNING "), result.diagnostics());
            assertTrue(result.diagnostics().contains("Unknown local animation name: " + name), result.diagnostics());
            assertTrue(result.files().containsKey("classes/demo/Peer.css"));
            assertTrue(result.files().containsKey("classes/demo/Peer.css.map"));
            assertTrue(result.files().containsKey("generated/demo/Styled_Peer.java"));
            assertFalse(
                    result.files().keySet().stream().anyMatch(path -> path.contains("Button")),
                    result.files().keySet().toString());
        }
    }

    @Test
    public void sourceMapsFollowJavaEscapesContinuedLinesAndNestedRules() throws Exception {
        String text = source(
                        "demo",
                        "    color: r" + "\\" + "u0065d;\\\n    background-color: white;\n"
                                + "    @media (color) {\n      &.success { color: green; }\n    }",
                        "abstract String success();")
                .replace("\n", "\r\n");
        var result = compile(Map.of("Button.java", text));
        assertTrue(result.success(), result.diagnostics());
        var css = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css")), StandardCharsets.UTF_8);
        var map = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css.map")), StandardCharsets.UTF_8);
        var parsed = JsonParser.parseString(map).getAsJsonObject();
        assertEquals(parsed.getAsJsonArray("sourcesContent").get(0).getAsString(), text);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        for (var pair : Map.of(
                        "background-color:white", "background-color: white",
                        "@media", "@media",
                        ".jcss-demo-Button.jcss-demo-Button--success", "&.success",
                        "color:green", "color: green")
                .entrySet()) {
            int generated = css.indexOf(pair.getKey());
            int original = text.indexOf(pair.getValue());
            assertTrue(generated >= 0, css);
            var location = Objects.requireNonNull(consumer.getMappingForLine(1, generated + 1));
            assertEquals(location.getOriginalFile(), "demo/Button.java");
            assertEquals(
                    location.getLineNumber(),
                    (int) text.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - text.lastIndexOf('\n', original - 1));
        }
    }

    @Test
    public void keyframeSourceMapsIncludeDefinitionsFramesAndRenamedReferences() throws Exception {
        String text = source("demo", """
            @keyframes pulse {
              from { opacity: 0; }
              to { opacity: 1; }
            }
            &:hover { animation: pulse 1s ease; }
            """, "");
        var result = compile(Map.of("Button.java", text));
        assertTrue(result.success(), result.diagnostics());
        var css = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css")), StandardCharsets.UTF_8);
        var map = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css.map")), StandardCharsets.UTF_8);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        for (var pair : Map.of(
                        "@keyframes jcss-demo-Button--keyframes-pulse", "@keyframes pulse",
                        "from{", "from",
                        "to{", "to",
                        "opacity:0", "opacity: 0",
                        "animation:jcss-demo-Button--keyframes-pulse", "animation: pulse")
                .entrySet()) {
            int generated = css.indexOf(pair.getKey());
            int original = text.indexOf(pair.getValue());
            assertTrue(generated >= 0, css);
            var location = Objects.requireNonNull(consumer.getMappingForLine(1, generated + 1));
            assertEquals(location.getOriginalFile(), "demo/Button.java");
            assertEquals(
                    location.getLineNumber(),
                    (int) text.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - text.lastIndexOf('\n', original - 1));
        }
    }

    @Test
    public void invalidJavaContractsFailClearly() throws Exception {
        for (var method : List.of(
                "abstract int success();", "abstract String success(int value);", "abstract <T> String success();")) {
            var result = compile(Map.of("Button.java", source("demo", "    &.success { color: red; }", method)));
            assertFalse(result.success());
            assertTrue(
                    result.diagnostics().contains("CSS accessor success must take no arguments and return String"),
                    result.diagnostics());
        }
        var expression = compile(Map.of(
                "Button.java",
                source("demo", "    color: red;", "")
                        .replace("@Css(\"\"\"\n    color: red;\n    \"\"\")", "@Css(\"color: \" + \"red;\")")));
        assertFalse(expression.success());
        assertTrue(expression.diagnostics().contains("literal Java text block"), expression.diagnostics());
    }

    @DataProvider
    public Object[][] invalidJavaContracts() {
        String base =
                source("demo", "    color: red;", "").replace("static Button of() { return Styled_Button.get(); }", "");
        String owner =
                "@Css requires an accessible abstract, non-generic style class extending Object; nested classes must"
                        + " be static";
        String constructor = "@Css style classes must not declare constructors";
        String accessor =
                "CSS accessor style must take no arguments and return String without type parameters or throws";
        var cases = new ArrayList<Object[]>();
        for (var declaration : List.of(
                "class Button", "abstract class Button<T>", "interface Button", "@interface Button", "enum Button")) {
            String text = base.replace("abstract class Button", declaration);
            if ("enum Button".equals(declaration)) {
                text = text.replace("enum Button {", "enum Button {;");
            }
            cases.add(new Object[] {declaration, text, owner});
        }
        cases.add(new Object[] {
            "subclass",
            base.replace("abstract class Button", "abstract class Button extends Parent") + "class Parent {}",
            owner
        });
        cases.add(new Object[] {
            "record",
            base.replace("abstract class Button", "record Button()").replace("abstract String style();", ""),
            owner
        });
        cases.add(new Object[] {
            "sealed",
            base.replace("abstract class Button", "sealed abstract class Button permits Child")
                    + "final class Child extends Button { String style() { return \"\"; } }",
            "@Css style classes must not be sealed"
        });
        String nested = base.replace("@Css(", "class Outer { @Css(") + "}";
        cases.add(new Object[] {"nonstatic nested", nested, owner});
        cases.add(new Object[] {
            "private nested", nested.replace("abstract class Button", "private abstract static class Button"), owner
        });
        cases.add(new Object[] {
            "private enclosing",
            nested.replace("@Css(", "private static class Hidden { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                    + "}",
            "@Css style classes must be accessible from their package"
        });
        for (var declaration : List.of(
                "Button() {}",
                "public Button() {}",
                "protected Button() {}",
                "private Button() {}",
                "@Deprecated Button() {}",
                "@Deprecated(forRemoval = true) Button() {}",
                "<T> Button() {}",
                "Button() {} private Button(int value) {}",
                "Button(int value) {}",
                "Button(String... values) {}",
                "Button() throws Exception {}",
                "Button() throws RuntimeException {}",
                "private Button() {} Button(int value) {}")) {
            cases.add(new Object[] {
                declaration,
                base.replace("abstract String style();", declaration + " abstract String style();"),
                constructor
            });
        }
        for (var visibility : List.of("", "public ", "protected ", "private ")) {
            for (var modifiers : List.of("", "final ", "static ", "static final ", "static volatile ", "transient ")) {
                cases.add(new Object[] {
                    "field " + visibility + modifiers,
                    base.replace(
                            "abstract String style();",
                            visibility + modifiers + "int value = 1; abstract String style();"),
                    "@Css style classes must not declare fields"
                });
            }
        }
        for (var declaration : List.of(
                "String value;",
                "static final String VALUE = \"constant\";",
                "final int[] values = {};",
                "int first, second;",
                "int value; { value = 1; }")) {
            cases.add(new Object[] {
                declaration,
                base.replace("abstract String style();", declaration + " abstract String style();"),
                "@Css style classes must not declare fields"
            });
        }
        cases.add(new Object[] {
            "instance initializer",
            base.replace("abstract String style();", "{ System.nanoTime(); } abstract String style();"),
            "@Css style classes must not declare instance initializers"
        });
        for (var method : List.of(
                "abstract String get();",
                "String get() { return style(); }",
                "public final String get() { return style(); }",
                "static String get() { return \"\"; }",
                "static int get() { return 1; }",
                "static void get() {}",
                "static final Button get() { return null; }",
                "static <T> T get() { return null; }")) {
            cases.add(new Object[] {
                "factory conflict " + method,
                base.replace("abstract String style();", method + " abstract String style();"),
                "@Css method get() conflicts with the generated singleton factory"
            });
        }
        for (var prefix : List.of("", "public ", "protected ")) {
            String text = base.replace("@Css(", "class Outer { class Middle { @Css(")
                            .replace("abstract class Button", prefix + "abstract class Button")
                    + "}}";
            cases.add(new Object[] {"deep inner " + prefix, text, owner});
        }
        for (var method : List.of(
                "abstract int style();",
                "abstract String[] style();",
                "abstract Object style();",
                "abstract CharSequence style();",
                "abstract String style(int value);",
                "abstract String style(String... values);",
                "abstract <T> String style();",
                "abstract String style() throws Exception;",
                "abstract String style() throws RuntimeException;")) {
            cases.add(new Object[] {method, base.replace("abstract String style();", method), accessor});
        }
        for (var method : List.of("", "String style() { return \"\"; }", "static String style() { return \"\"; }")) {
            cases.add(new Object[] {
                "nonabstract root " + method,
                base.replace("abstract String style();", method),
                "@Css style classes require abstract String style()"
            });
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidJavaContracts")
    public void invalidJavaContractsReportOneAuthorErrorWithoutGeneratingFiles(
            String label, String text, String message) throws Exception {
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success(), label);
        assertEquals(
                result.diagnostics()
                        .lines()
                        .filter(line -> line.startsWith("ERROR "))
                        .count(),
                1L,
                result.diagnostics());
        assertTrue(result.diagnostics().contains(message), label + ": " + result.diagnostics());
        assertFalse(result.diagnostics().contains("WARNING "), result.diagnostics());
        assertFalse(result.diagnostics().contains("generated/"), result.diagnostics());
        assertTrue(result.files().isEmpty(), label + ": " + result.files().keySet());
    }

    @DataProvider
    public Object[][] validJavaContracts() {
        var cases = new ArrayList<Object[]>();
        for (var accessor : List.of("", "public ", "protected ")) {
            String text = source("demo", "    color: red;", "")
                    .replace("abstract String style();", accessor + "abstract String style();");
            cases.add(new Object[] {accessor, text});
        }
        String base = source("demo", "    color: red;", "");
        cases.add(new Object[] {"public owner", base.replace("abstract class Button", "public abstract class Button")});
        cases.add(new Object[] {
            "concrete helpers",
            base.replace(
                    "abstract String style();",
                    "abstract String style(); static String helper() { return \"\"; } final int size() { return 0; }")
        });
        cases.add(new Object[] {
            "nullable accessor",
            base.replace(
                            "import react4j.css.Css;",
                            "import react4j.css.Css;\nimport org.jspecify.annotations.Nullable;")
                    .replace("abstract String style();", "abstract @Nullable String style();")
        });
        for (var annotation : List.of("@Deprecated", "@Deprecated(forRemoval = true)")) {
            cases.add(new Object[] {annotation + " owner", base.replace("@Css(", annotation + " @Css(")});
            cases.add(new Object[] {
                annotation + " default package",
                base.replace("package demo;\n", "").replace("@Css(", annotation + " @Css(")
            });
            cases.add(new Object[] {
                annotation + " accessor",
                base.replace("abstract String style();", annotation + " abstract String style();")
            });
            cases.add(new Object[] {
                annotation + " enclosing",
                base.replace("@Css(", annotation + " class Outer { @Css(")
                                .replace("abstract class Button", "abstract static class Button")
                                .replace("Styled_Button", "Outer_Styled_Button")
                        + "}"
            });
        }
        cases.add(new Object[] {
            "generic enclosing",
            base.replace("@Css(", "class Outer<T> { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                            .replace("Styled_Button", "Outer_Styled_Button")
                    + "}"
        });
        cases.add(new Object[] {
            "protected enclosing",
            base.replace("@Css(", "class Outer { protected static class Middle { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                            .replace("Styled_Button", "Outer_Middle_Styled_Button")
                    + "}}"
        });
        for (var enclosing : List.of("interface Outer", "enum Outer {; }", "record Outer()")) {
            String wrapper = enclosing.replace(" {; }", "") + " {" + (enclosing.startsWith("enum") ? ";" : "");
            cases.add(new Object[] {
                enclosing,
                base.replace("@Css(", wrapper + " @Css(")
                                .replace("abstract class Button", "abstract static class Button")
                                .replace("Styled_Button", "Outer_Styled_Button")
                        + "}"
            });
        }
        cases.add(new Object[] {
            "static style inside inner enclosing",
            base.replace("@Css(", "class Outer { class Middle { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                            .replace("Styled_Button", "Outer_Middle_Styled_Button")
                    + "}}"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "validJavaContracts")
    public void validJavaContractsCompileWithoutDiagnostics(String label, String text) throws Exception {
        var result = compile(Map.of(text.contains(" Outer") ? "Outer.java" : "Button.java", text));
        assertTrue(result.success(), label + ": " + result.diagnostics());
        assertEquals(result.diagnostics(), "", label);
        assertFalse(result.files().isEmpty(), label);
    }

    @Test
    public void generatedSuppressionsPreserveDeprecationWarningsAndSuppressionInAuthoredClients() throws Exception {
        for (var annotation : List.of("@Deprecated", "@Deprecated(forRemoval = true)")) {
            for (var owner : List.of(false, true)) {
                String text = source("demo", "    color: red;", "");
                text = owner
                        ? text.replace("@Css(", annotation + " @Css(")
                        : text.replace("abstract String style();", annotation + " abstract String style();");
                String client = "package demo; class Client { String style() { return Button.of().style(); } }";
                var result = compile(Map.of("Button.java", text, "Client.java", client));
                assertTrue(result.success(), result.diagnostics());
                assertEquals(result.diagnostics().lines().count(), 1L, result.diagnostics());
                assertTrue(result.diagnostics().contains("WARNING Client.java:"), result.diagnostics());
                assertTrue(result.diagnostics().contains(owner ? "demo.Button" : "style()"), result.diagnostics());
                var suppressed = compile(Map.of(
                        "Button.java",
                        text,
                        "Client.java",
                        client.replace(
                                "class Client", "@SuppressWarnings({\"deprecation\", \"removal\"}) class Client")));
                assertTrue(suppressed.success(), suppressed.diagnostics());
                assertEquals(suppressed.diagnostics(), "");
            }
        }
    }

    @Test
    public void nestedTypesUseTheirBinaryOwnerIdentity() throws Exception {
        String nested = "package demo;\nimport react4j.css.Css;\nclass Outer {\n"
                + "@Css(\"\"\"\ncolor: red;\n\"\"\")\nabstract static class Button {\n"
                + "static Button of() { return Outer_Styled_Button.get(); }\nabstract String style();\n}\n}\n";
        var result = compile(Map.of("Outer.java", nested), "demo.Outer$Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Outer$Button.style"), "jcss-demo-Outer_0024_Button");
    }

    @Test
    public void defaultPackageEnclosingTypeDoesNotShadowHolder() throws Exception {
        String text = source("demo", "    color: red;", "")
                        .replace("package demo;\n", "")
                        .replace("@Css(", "class Internal { @Css(")
                        .replace("abstract class Button", "abstract static class Button")
                        .replace("Styled_Button", "Internal_Styled_Button")
                + "}";
        var result = compile(Map.of("Internal.java", text), "Internal$Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("Internal$Button.style"), "jcss-Internal_0024_Button");
    }

    @Test
    public void bindingsDoNotShadowJavaTypes() throws Exception {
        for (var packageName : List.of("demo", "")) {
            for (var name : List.of("String", "Override", "NullMarked", "Generated", "SuppressWarnings", "Internal")) {
                String text = source("demo", "    color: red;", "")
                        .replace("class Button", "class " + name)
                        .replace("static Button of()", "static " + name + " of()")
                        .replace("Styled_Button", "Styled_" + name)
                        .replace("abstract String style()", "abstract java.lang.String style()");
                if (packageName.isEmpty()) {
                    text = text.replace("package demo;\n", "");
                }
                String owner = packageName.isEmpty() ? name : packageName + "." + name;
                var result = compile(Map.of(name + ".java", text), owner);
                assertTrue(result.success(), result.diagnostics());
                assertEquals(
                        result.values().get(owner + ".style"), "jcss-" + (packageName.isEmpty() ? "" : "demo-") + name);
            }
        }
    }

    @DataProvider(name = "shadowedTypes")
    public Object[][] shadowedTypes() {
        var cases = new ArrayList<Object[]>();
        for (var name : List.of("String", "Override", "SuppressWarnings", "NullMarked", "Generated", "Internal")) {
            for (boolean nested : List.of(false, true)) {
                for (boolean formatted : List.of(false, true)) {
                    cases.add(new Object[] {name, nested, formatted});
                }
            }
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "shadowedTypes")
    public void generatedTypesAvoidPeerAndInheritedTypeNames(String name, boolean nested, boolean formatted)
            throws Exception {
        String text = source("demo", "    color: red;", "")
                .replace("abstract String style()", "abstract java.lang.String style()");
        // Exercise SuppressWarnings as well as the annotations and return type always emitted.
        text = text.replace("@Css(", "@Deprecated\n@Css(");
        if (nested) {
            text = text.replace("abstract class Button {", "abstract class Button { static class " + name + " {} ");
        }
        var sources = new LinkedHashMap<String, String>();
        sources.put("Button.java", text);
        if (!nested) {
            sources.put(name + ".java", "package demo; class " + name + " {}\n");
        }
        var result = compile(sources, List.of("-Areact4j.css.format_generated_source=" + formatted), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
    }

    @DataProvider(name = "singletonOwners")
    public Object[][] singletonOwners() {
        var cases = new ArrayList<Object[]>();
        for (var packageName : List.of("demo", "")) {
            for (var access : List.of("", "public ")) {
                for (var enclosing : List.of("", "Outer", "Outer.Middle")) {
                    for (boolean formatted : List.of(false, true)) {
                        cases.add(new Object[] {packageName, access, enclosing, formatted});
                    }
                }
            }
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "singletonOwners")
    public void singletonVisibilityIdentityAndNestedNames(
            String packageName, String access, String enclosing, boolean formatted) throws Exception {
        String generatedName = enclosing.isEmpty() ? "Styled_Button" : enclosing.replace('.', '_') + "_Styled_Button";
        String text = source("demo", "    color: red;", "").replace("Styled_Button", generatedName);
        if (packageName.isEmpty()) {
            text = text.replace("package demo;\n", "");
        }
        text = text.replace(
                "abstract class Button",
                access + "abstract " + (enclosing.isEmpty() ? "" : "static ") + "class Button");
        if (!enclosing.isEmpty()) {
            String wrappers = access + "class Outer { "
                    + ("Outer.Middle".equals(enclosing) ? access + "static class Middle { " : "");
            text = text.replace("@Css(", wrappers + "@Css(") + ("Outer.Middle".equals(enclosing) ? "}}" : "}");
        }
        String sourceName = enclosing.isEmpty() ? "Button.java" : "Outer.java";
        var result = compile(Map.of(sourceName, text), List.of("-Areact4j.css.format_generated_source=" + formatted));
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        String prefix = packageName.isEmpty() ? "" : packageName + ".";
        String ownerName = prefix + (enclosing.isEmpty() ? "" : enclosing.replace('.', '$') + "$") + "Button";
        Path directory = Files.createTempDirectory("css-singleton-");
        try {
            for (var entry : result.files().entrySet()) {
                if (entry.getKey().endsWith(".class")) {
                    Path target = directory.resolve(entry.getKey().substring("classes/".length()));
                    Files.createDirectories(target.getParent());
                    Files.write(target, Base64.getDecoder().decode(entry.getValue()));
                }
            }
            try (var loader = new URLClassLoader(
                    new URL[] {directory.toUri().toURL()}, CssProcessorTest.class.getClassLoader())) {
                var owner = loader.loadClass(ownerName);
                var generated = loader.loadClass(prefix + generatedName);
                assertEquals(Modifier.isPublic(owner.getModifiers()), !access.isEmpty());
                assertEquals(generated.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE), 0);
                assertTrue(Modifier.isFinal(generated.getModifiers()));
                var constructors = generated.getDeclaredConstructors();
                assertEquals(constructors.length, 1);
                assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
                assertEquals(constructors[0].getParameterCount(), 0);
                assertEquals(generated.getDeclaredFields().length, 0);
                var nested = generated.getDeclaredClasses();
                assertEquals(nested.length, 1);
                assertEquals(nested[0].getSimpleName(), "Internal");
                assertTrue(Modifier.isPrivate(nested[0].getModifiers()));
                assertTrue(Modifier.isStatic(nested[0].getModifiers()));
                assertTrue(Modifier.isFinal(nested[0].getModifiers()));
                var fields = nested[0].getDeclaredFields();
                assertEquals(fields.length, 1);
                assertSame(fields[0].getType(), owner);
                assertTrue(Modifier.isPrivate(fields[0].getModifiers()));
                assertTrue(Modifier.isStatic(fields[0].getModifiers()));
                assertTrue(Modifier.isFinal(fields[0].getModifiers()));
                var factory = generated.getDeclaredMethod("get");
                assertSame(factory.getReturnType(), owner);
                assertTrue(Modifier.isStatic(factory.getModifiers()));
                assertEquals(factory.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE), 0);
                factory.setAccessible(true);
                var tasks = new ArrayList<Callable<Object>>();
                for (int i = 0; i < 32; i++) {
                    tasks.add(() -> factory.invoke(null));
                }
                var executor = Executors.newFixedThreadPool(8);
                try {
                    var answers = executor.invokeAll(tasks);
                    var singleton = factory.invoke(null);
                    assertNotNull(singleton);
                    assertSame(singleton.getClass(), generated);
                    for (var answer : answers) {
                        assertSame(answer.get(), singleton);
                    }
                } finally {
                    executor.shutdownNow();
                }
            }
        } finally {
            delete(directory);
        }
    }

    @Test
    public void singletonAllocationWaitsForTheFirstGet() throws Exception {
        for (boolean formatted : List.of(false, true)) {
            String owner = source("demo", "    color: red;", "");
            var result = compile(
                    Map.of("Button.java", owner), List.of("-Areact4j.css.format_generated_source=" + formatted));
            assertTrue(result.success(), result.diagnostics());
            var generated = new String(
                    Base64.getDecoder().decode(result.files().get("generated/demo/Styled_Button.java")),
                    StandardCharsets.UTF_8);
            // Instrument the emitted constructor to observe allocation without adding authored state.
            String instrumented =
                    generated.replace("private Styled_Button() {", "private Styled_Button() { Probe.count++;");
            assertNotEquals(instrumented, generated);
            Path directory = Files.createTempDirectory("css-lazy-singleton-");
            try {
                Path classes = Files.createDirectory(directory.resolve("classes"));
                Path input = directory.resolve("Button.java");
                Path implementation = directory.resolve("Styled_Button.java");
                Path probe = directory.resolve("Probe.java");
                Files.writeString(input, owner);
                Files.writeString(implementation, instrumented);
                Files.writeString(probe, "package demo; public class Probe { public static int count; }");
                var compiler = ToolProvider.getSystemJavaCompiler();
                var diagnostics = new DiagnosticCollector<JavaFileObject>();
                try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
                    assertTrue(
                            compiler.getTask(
                                            null,
                                            manager,
                                            diagnostics,
                                            List.of(
                                                    "--release",
                                                    "21",
                                                    "-proc:none",
                                                    "-classpath",
                                                    applicationClasspath(),
                                                    "-d",
                                                    classes.toString()),
                                            null,
                                            manager.getJavaFileObjectsFromPaths(List.of(input, implementation, probe)))
                                    .call(),
                            diagnostics.getDiagnostics().toString());
                }
                try (var loader = new URLClassLoader(
                        new URL[] {classes.toUri().toURL()}, CssProcessorTest.class.getClassLoader())) {
                    var counter = loader.loadClass("demo.Probe").getField("count");
                    var type = Class.forName("demo.Styled_Button", true, loader);
                    assertEquals(counter.getInt(null), 0);
                    var factory = type.getDeclaredMethod("get");
                    factory.setAccessible(true);
                    var singleton = factory.invoke(null);
                    assertEquals(counter.getInt(null), 1);
                    assertSame(factory.invoke(null), singleton);
                    assertEquals(counter.getInt(null), 1);
                }
            } finally {
                delete(directory);
            }
        }
    }

    @DataProvider(name = "helperMethods")
    public Object[][] helperMethods() {
        var cases = new ArrayList<Object[]>();
        for (var access : List.of("", "public ", "protected ", "private ")) {
            for (var modifiers : List.of("", "final ", "static ", "static final ")) {
                for (boolean formatted : List.of(false, true)) {
                    String method = access + modifiers + "String helper() { return \"helper\"; }";
                    cases.add(new Object[] {method, "helper()", formatted});
                }
            }
        }
        for (var access : List.of("", "public ", "protected ")) {
            for (boolean formatted : List.of(false, true)) {
                cases.add(new Object[] {
                    access + "static Button get() { return Styled_Button.get(); }", "get().style()", formatted
                });
                cases.add(new Object[] {
                    access + "static Object get() { return Styled_Button.get(); }",
                    "((Button) get()).style()",
                    formatted
                });
                cases.add(new Object[] {
                    access + "static <T> Button get() { return Styled_Button.get(); }", "get().style()", formatted
                });
                cases.add(new Object[] {
                    access + "static <T> Object get() { return Styled_Button.get(); }",
                    "((Button) get()).style()",
                    formatted
                });
            }
        }
        for (boolean formatted : List.of(false, true)) {
            cases.add(new Object[] {
                "static Button get() throws Exception { return Styled_Button.get(); }", "get().style()", formatted
            });
            cases.add(new Object[] {"private String get() { return style(); }", "get()", formatted});
            cases.add(new Object[] {"static String get(int value) { return \"overload\"; }", "get(1)", formatted});
            cases.add(new Object[] {
                "<T> String helper(T value) throws Exception { return style(); }", "helper(1)", formatted
            });
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "helperMethods")
    public void concreteAndStaticHelpersRemainCallable(String method, String invocation, boolean formatted)
            throws Exception {
        String text = source("demo", "    color: red;", method)
                .replace(
                        "static Button of() { return Styled_Button.get(); }",
                        "static Button of() throws Exception { Button value = Styled_Button.get(); value.check();"
                                + " return value; }")
                .replace(
                        "abstract String style();",
                        "void check() throws Exception { if (" + invocation
                                + ".isEmpty()) throw new AssertionError(); } abstract String style();");
        var result = compile(
                Map.of("Button.java", text),
                List.of("-Areact4j.css.format_generated_source=" + formatted),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
    }

    @Test
    public void generatedConstructorCannotBeCalledByPackagePeers() throws Exception {
        for (var access : List.of("", "public ")) {
            String text = source("demo", "    color: red;", "")
                    .replace("abstract class Button", access + "abstract class Button");
            var result = compile(Map.of(
                    "Button.java",
                    text,
                    "Client.java",
                    "package demo; class Client { Button value = new Styled_Button(); }"));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains("Styled_Button() has private access"), result.diagnostics());
        }
    }

    @Test
    public void publicStyleIsUsableFromAnotherPackageThroughAuthoredApi() throws Exception {
        String text = source("demo", "    color: red;", "")
                .replace("abstract class Button", "public abstract class Button")
                .replace("abstract String style()", "public abstract String style()")
                .replace("static Button of()", "public static Button of()");
        var result = compile(Map.of(
                "Button.java",
                text,
                "other/Client.java",
                "package other; import demo.Button; class Client { String style() { return Button.of().style(); } }"));
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
    }

    @Test
    public void publicStyleDoesNotExposeGeneratedImplementationAcrossPackages() throws Exception {
        String text =
                source("demo", "    color: red;", "").replace("abstract class Button", "public abstract class Button");
        var result = compile(Map.of(
                "Button.java",
                text,
                "Client.java",
                "package other; import demo.Styled_Button; class Client { Styled_Button value; }"));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Styled_Button is not public"), result.diagnostics());
    }

    @Test
    public void stringAccessorsRetainTheirNominalTypeWithTypeAnnotations() throws Exception {
        String text = source("demo", "    color: red;", "")
                .replace("abstract String style()", "abstract @org.jspecify.annotations.NonNull String style()");
        var result = compile(Map.of("Button.java", text), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
    }
}
