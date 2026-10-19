package react4j.css.linker;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.FilePosition;
import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.testng.annotations.Test;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssResource;

public final class StyleCompilerTest {
    private static CssLink link(boolean optimized) {
        return new CssLink(
                "//demo:app",
                "1".repeat(64),
                optimized,
                List.of(
                        value("style", "root"),
                        value("success", "root mod"),
                        value("dead", "root dead"),
                        value("alias", "root mod")));
    }

    private static CssLink.Value value(String method, String value) {
        return new CssLink.Value(
                "demo.Button",
                "demo.Styled_Button",
                new CssResource.Binding(method, CssResource.SymbolKind.CLASS, value));
    }

    private static final class Runner extends CommandLineRunner {
        private final Compiler compiler;

        Runner(Compiler compiler, String[] arguments) {
            super(arguments);
            this.compiler = compiler;
        }

        @Override
        protected Compiler createCompiler() {
            return compiler;
        }

        int execute() throws Exception {
            return doRun();
        }
    }

    @Test
    public void finalValuesSurviveInliningAndLowerToOrdinaryStringsWithSourceMaps() throws Exception {
        Path directory = Files.createTempDirectory("css-linker");
        try {
            Path source = directory.resolve("input.js/demo/Styled_Button.impl.java.js");
            Files.createDirectories(source.getParent());
            String code = "class Styled_Button { style() { return 'root'; } success() { return 'root mod'; } dead() {"
                    + " return 'root dead'; } alias() { return 'root mod'; }} function helper(style) { return"
                    + " style.success(); } window.result = helper(new Styled_Button());";
            mappedSource(source, code);
            Path other = directory.resolve("other.js");
            Files.writeString(other, "function unrelated() { return 'root dead'; } window.other = unrelated();");
            Path extern = directory.resolve("extern.js");
            Files.writeString(extern, "var window;");
            Path output = directory.resolve("app.js");
            Path map = directory.resolve("app.js.map");
            Path report = directory.resolve("live.json");
            var link = link(true);
            var compiler = new StyleCompiler(link, report);
            int result = new Runner(compiler, new String[] {
                        "--js",
                        source.toString(),
                        "--js",
                        other.toString(),
                        "--externs",
                        extern.toString(),
                        "--env",
                        "CUSTOM",
                        "--compilation_level",
                        "ADVANCED",
                        "--js_output_file",
                        output.toString(),
                        "--create_source_map",
                        map.toString(),
                        "--source_map_include_content"
                    })
                    .execute();
            assertEquals(result, 0);
            compiler.finish(output);
            assertEquals(
                    link.readReport(Files.readString(report), CssLink.digest(output)), Set.of("demo.Button#success"));
            String js = Files.readString(output);
            assertFalse(js.contains("$react4j$css$"), js);
            assertFalse(js.contains("Styled_Button"), js);
            assertTrue(js.contains("root mod"), js);
            assertTrue(js.contains("root dead"), js);
            var consumer = new SourceMapConsumerV3();
            consumer.parse(Files.readString(map));
            int start = js.indexOf("root mod");
            var origin = consumer.getMappingForLine(1, start + 1);
            assertNotNull(origin);
            assert null != origin;
            assertTrue(origin.getOriginalFile().endsWith("demo/Styled_Button.java"), origin.toString());
            assertEquals(origin.getColumnPosition(), code.indexOf("success()") + 1);
        } finally {
            remove(directory);
        }
    }

    @Test
    public void factoryIsDiscoverableAndDevelopmentExplicitlyRetainsAll() throws Exception {
        Path directory = Files.createTempDirectory("css-factory");
        try {
            var factories = ServiceLoader.load(CompilerFactory.class).stream().toList();
            assertEquals(factories.size(), 1);
            Path config = directory.resolve("config.json");
            Path report = directory.resolve("live.json");
            Files.writeString(config, link(false).json());
            var factory = factories.get(0).get();
            var compiler = factory.create(config, report);
            Path source = directory.resolve("entry.js");
            Path extern = directory.resolve("extern.js");
            Path output = directory.resolve("app.js");
            Files.writeString(source, "window.result = 'ok';");
            Files.writeString(extern, "var window;");
            assertFalse(Files.exists(report));
            for (var level : List.of("BUNDLE", "SIMPLE")) {
                compiler = factory.create(config, report);
                assertEquals(
                        new Runner(compiler, new String[] {
                                    "--js",
                                    source.toString(),
                                    "--externs",
                                    extern.toString(),
                                    "--env",
                                    "CUSTOM",
                                    "--compilation_level",
                                    level,
                                    "--js_output_file",
                                    output.toString()
                                })
                                .execute(),
                        0);
                factory.finish(compiler, output);
                assertEquals(link(false).readReport(Files.readString(report), CssLink.digest(output)), Set.of());
            }
            Files.writeString(config, link(true).json());
            var incomplete = factory.create(config, report);
            expectThrows(IllegalArgumentException.class, () -> factory.finish(incomplete, output));
        } finally {
            remove(directory);
        }
    }

    @Test
    public void generatedFilesWithTheSameBasenameResolveByTheirFullPackage() throws Exception {
        Path directory = Files.createTempDirectory("css-packages");
        try {
            var values = List.of(
                    value("style", "one"),
                    new CssLink.Value(
                            "a.demo.Button",
                            "a.demo.Styled_Button",
                            new CssResource.Binding("style", CssResource.SymbolKind.CLASS, "two")));
            var link = new CssLink("//:app", "1".repeat(64), true, values);
            compile(
                    new StyleCompiler(link, directory.resolve("live.json")),
                    directory,
                    Map.of(
                            "demo/Styled_Button.impl.java.js",
                                    "class One { style() { return 'one'; }} window.one = new One().style();",
                            "a/demo/Styled_Button.impl.java.js",
                                    "class Two { style() { return 'two'; }} window.two = new Two().style();"));
            assertEquals(
                    link.readReport(
                            Files.readString(directory.resolve("live.json")),
                            CssLink.digest(directory.resolve("app.js"))),
                    Set.of("demo.Button#style", "a.demo.Button#style"));
        } finally {
            remove(directory);
        }
    }

    @Test
    public void defaultPackageBindingDoesNotCaptureASameNamedFileInAnotherPackage() throws Exception {
        Path directory = Files.createTempDirectory("css-default-package");
        try {
            var link = new CssLink(
                    "//:app",
                    "1".repeat(64),
                    true,
                    List.of(new CssLink.Value(
                            "Button",
                            "Styled_Button",
                            new CssResource.Binding("style", CssResource.SymbolKind.CLASS, "root"))));
            compile(
                    new StyleCompiler(link, directory.resolve("live.json")),
                    directory,
                    Map.of(
                            "Styled_Button.impl.java.js", "class Generated { style() { return 'root'; }}",
                            "a/Styled_Button.impl.java.js",
                                    "class Other { text() { return 'root'; }} window.result = new Other().text();"));
            assertEquals(
                    link.readReport(
                            Files.readString(directory.resolve("live.json")),
                            CssLink.digest(directory.resolve("app.js"))),
                    Set.of());
        } finally {
            remove(directory);
        }
    }

    @Test
    public void missingOrChangedGeneratedBindingsFailWithTheirIdentity() throws Exception {
        Path directory = Files.createTempDirectory("css-invalid-link");
        try {
            var link = new CssLink("//:app", "1".repeat(64), true, List.of(value("style", "root")));
            for (var sources : List.of(
                    Map.of("other.js", "window.result = 'root';"),
                    Map.of(
                            "demo/Styled_Button.impl.java.js",
                            "class Button { style() { return 'changed'; }} window.result = new Button().style();"))) {
                var error = expectThrows(
                        RuntimeException.class,
                        () -> compile(new StyleCompiler(link, directory.resolve("live.json")), directory, sources));
                var messages = new StringBuilder();
                for (Throwable cause = error; null != cause; cause = cause.getCause()) {
                    messages.append(cause.getMessage());
                }
                assertTrue(
                        messages.toString().contains("CSS catalogue implementation is missing")
                                || messages.toString().contains("Generated CSS binding differs from its catalogue"),
                        messages.toString());
                assertFalse(Files.exists(directory.resolve("live.json")));
            }
        } finally {
            remove(directory);
        }
    }

    @Test
    public void missingMalformedAndUnidentifiedBindingMapsFailClosed() throws Exception {
        Path directory = Files.createTempDirectory("css-invalid-binding-map");
        try {
            Path source = directory.resolve("input.js/demo/Styled_Button.impl.java.js");
            Path map = source.resolveSibling("Styled_Button.js.map");
            Path extern = directory.resolve("extern.js");
            Path report = directory.resolve("live.json");
            Files.createDirectories(source.getParent());
            Files.writeString(extern, "var window;");
            var link = new CssLink("//:app", "1".repeat(64), true, List.of(value("style", "root")));
            for (var failure : List.of("missing", "malformed", "unidentified")) {
                mappedSource(source, "class Button { style() { return 'root'; }} window.result=new Button().style();");
                if ("missing".equals(failure)) {
                    Files.delete(map);
                } else if ("malformed".equals(failure)) {
                    Files.writeString(map, "{");
                } else {
                    Files.writeString(map, Files.readString(map).replace("demo.Styled_Button.style", "other.method"));
                }
                var error = expectThrows(
                        RuntimeException.class,
                        () -> new Runner(new StyleCompiler(link, report), new String[] {
                                    "--js",
                                    source.toString(),
                                    "--externs",
                                    extern.toString(),
                                    "--env",
                                    "CUSTOM",
                                    "--compilation_level",
                                    "ADVANCED",
                                    "--js_output_file",
                                    directory.resolve("app.js").toString()
                                })
                                .execute());
                var messages = new StringBuilder();
                for (Throwable cause = error; null != cause; cause = cause.getCause()) {
                    messages.append(cause.getMessage());
                }
                assertTrue(
                        messages.toString().contains("Invalid generated CSS binding source map")
                                || messages.toString().contains("Generated CSS binding differs from its catalogue"),
                        messages.toString());
                assertFalse(Files.exists(report));
            }
        } finally {
            remove(directory);
        }
    }

    @Test
    public void equalGetterTextKeepsEachBindingIdentity() throws Exception {
        Path directory = Files.createTempDirectory("css-equal-bindings");
        try {
            var link = new CssLink(
                    "//:app",
                    "1".repeat(64),
                    true,
                    List.of(
                            new CssLink.Value(
                                    "demo.Button",
                                    "demo.Styled_Button",
                                    new CssResource.Binding("id", CssResource.SymbolKind.ID, "shared")),
                            new CssLink.Value(
                                    "demo.Button",
                                    "demo.Styled_Button",
                                    new CssResource.Binding("animation", CssResource.SymbolKind.ANIMATION, "shared"))));
            for (var method : List.of("id", "animation")) {
                compile(
                        new StyleCompiler(link, directory.resolve("live.json")),
                        directory,
                        Map.of(
                                "demo/Styled_Button.impl.java.js",
                                "class Button { id() { return 'shared'; } animation() { return 'shared'; } }"
                                        + "window.result = new Button()." + method + "();"));
                assertEquals(
                        link.readReport(
                                Files.readString(directory.resolve("live.json")),
                                CssLink.digest(directory.resolve("app.js"))),
                        Set.of("demo.Button#" + method));
            }
        } finally {
            remove(directory);
        }
    }

    private static void mappedSource(Path path, String code) throws Exception {
        if (!path.toString().endsWith(".impl.java.js")) {
            Files.writeString(path, code);
            return;
        }
        String name = path.getFileName().toString().replace(".impl.java.js", "");
        String relative = path.toString().replace('\\', '/');
        relative = relative.substring(relative.lastIndexOf(".js/") + 4).replace(".impl.java.js", "");
        String owner = relative.replace('/', '.');
        var generator = new SourceMapGeneratorV3();
        var methods = Pattern.compile("([\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*)\\(\\)\\s*\\{")
                .matcher(code);
        while (methods.find()) {
            generator.addMapping(
                    name + ".java",
                    owner + "." + methods.group(1),
                    new FilePosition(0, methods.start()),
                    new FilePosition(0, methods.start()),
                    new FilePosition(0, code.indexOf('}', methods.end()) + 1));
        }
        var output = new StringBuilder();
        generator.appendTo(output, name + ".js");
        Files.writeString(path.resolveSibling(name + ".js.map"), output);
        Files.writeString(path, code + "\n//# sourceMappingURL=" + name + ".js.map\n");
    }

    private static void compile(StyleCompiler compiler, Path directory, Map<String, String> sources) throws Exception {
        var arguments = new ArrayList<String>();
        for (var entry : new TreeMap<>(sources).entrySet()) {
            Path path = directory.resolve("input.js").resolve(entry.getKey());
            Files.createDirectories(path.getParent());
            mappedSource(path, entry.getValue());
            arguments.addAll(List.of("--js", path.toString()));
        }
        Path extern = directory.resolve("extern.js");
        Files.writeString(extern, "var window;");
        Path output = directory.resolve("app.js");
        arguments.addAll(List.of(
                "--externs",
                extern.toString(),
                "--env",
                "CUSTOM",
                "--compilation_level",
                "ADVANCED",
                "--js_output_file",
                output.toString()));
        assertEquals(new Runner(compiler, arguments.toArray(String[]::new)).execute(), 0);
        compiler.finish(output);
    }

    private static void remove(Path directory) throws Exception {
        try (var files = Files.walk(directory)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
