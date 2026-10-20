package react4j.css.linker;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.FilePosition;
import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.gson.JsonParser;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.testng.annotations.Test;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssNames;
import react4j.css.internal.CssResource;

public final class StyleCompilerTest {
    private static List<CssResource.SymbolId> references(CssResource.SymbolKind kind, String value) {
        return Arrays.stream(value.split(" "))
                .map(token -> new CssResource.SymbolId("demo.Button", kind, token))
                .toList();
    }

    private static CssLink testLink(String name, boolean optimized, List<CssLink.Value> values) {
        var entries = new TreeMap<String, CssNames.Entry>();
        for (var value : values) {
            var tokens = value.binding().value().split(" ");
            for (int i = 0; i < tokens.length; i++) {
                var symbol = value.binding().symbols().get(i);
                entries.put(symbol.id(), new CssNames.Entry(symbol, tokens[i], tokens[i], false, List.of(value.id())));
            }
        }
        return new CssLink(
                name,
                "1".repeat(64),
                optimized,
                values,
                new CssNames(new CssNames.Policy(false, ""), List.copyOf(entries.values())),
                List.of());
    }

    private static CssLink link(boolean optimized) {
        return testLink(
                "//demo:app",
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
                new CssResource.Binding(
                        method,
                        CssResource.SymbolKind.CLASS,
                        value,
                        List.of(),
                        references(CssResource.SymbolKind.CLASS, value)));
    }

    private static final class Runner extends CommandLineRunner {
        private final Compiler compiler;

        Runner(Compiler compiler, String[] arguments) {
            this(compiler, arguments, true);
        }

        Runner(Compiler compiler, String[] arguments, boolean uncompressed) {
            super(Stream.concat(
                            Arrays.stream(arguments),
                            uncompressed ? Stream.of("--define=react4j.css.compress_names=false") : Stream.empty())
                    .toArray(String[]::new));
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
            String declaration = Files.readString(source.resolveSibling("Styled_Button.java"))
                    .lines()
                    .filter(line -> line.contains("String success()"))
                    .findFirst()
                    .orElseThrow();
            assertEquals(origin.getColumnPosition(), declaration.indexOf("success()") + 1);
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
                            new CssResource.Binding(
                                    "style",
                                    CssResource.SymbolKind.CLASS,
                                    "two",
                                    List.of(),
                                    List.of(new CssResource.SymbolId(
                                            "a.demo.Button", CssResource.SymbolKind.CLASS, "two")))));
            var link = testLink("//:app", true, values);
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
            var link = testLink(
                    "//:app",
                    true,
                    List.of(new CssLink.Value(
                            "Button",
                            "Styled_Button",
                            new CssResource.Binding(
                                    "style",
                                    CssResource.SymbolKind.CLASS,
                                    "root",
                                    List.of(),
                                    List.of(new CssResource.SymbolId(
                                            "Button", CssResource.SymbolKind.CLASS, "root"))))));
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
            var link = testLink("//:app", true, List.of(value("style", "root")));
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
            var link = testLink("//:app", true, List.of(value("style", "root")));
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
            var link = testLink(
                    "//:app",
                    true,
                    List.of(
                            new CssLink.Value(
                                    "demo.Button",
                                    "demo.Styled_Button",
                                    new CssResource.Binding(
                                            "id",
                                            CssResource.SymbolKind.ID,
                                            "shared",
                                            List.of(),
                                            references(CssResource.SymbolKind.ID, "shared"))),
                            new CssLink.Value(
                                    "demo.Button",
                                    "demo.Styled_Button",
                                    new CssResource.Binding(
                                            "animation",
                                            CssResource.SymbolKind.ANIMATION,
                                            "shared",
                                            List.of(),
                                            references(CssResource.SymbolKind.ANIMATION, "shared")))));
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

    @Test
    public void equalReadableGettersLowerUsingTheirOwnCanonicalCompositionAndPolicy() throws Exception {
        Path directory = Files.createTempDirectory("css-equal-compression");
        try {
            var root = new CssResource.SymbolId("demo.Button", CssResource.SymbolKind.CLASS, "");
            var id = new CssResource.SymbolId("demo.Owner", CssResource.SymbolKind.ID, "control");
            var values = List.of(
                    new CssLink.Value(
                            "demo.Button",
                            "demo.Styled_Button",
                            new CssResource.Binding(
                                    "style", CssResource.SymbolKind.CLASS, "shared", List.of(), List.of(root))),
                    new CssLink.Value(
                            "demo.Button",
                            "demo.Styled_Button",
                            new CssResource.Binding(
                                    "id", CssResource.SymbolKind.ID, "shared", List.of(), List.of(id))));
            var table = new CssNames(
                    new CssNames.Policy(true, ""),
                    List.of(
                            new CssNames.Entry(root, "shared", "a", false, List.of("demo.Button#style")),
                            new CssNames.Entry(id, "shared", "shared", true, List.of("demo.Button#id"))));
            var link = new CssLink("//:app", "1".repeat(64), true, values, table, List.of());
            for (var method : List.of("style", "id")) {
                compile(
                        new StyleCompiler(link, directory.resolve("live.json")),
                        directory,
                        Map.of(
                                "demo/Styled_Button.impl.java.js",
                                "class Button { style() { return 'shared'; } id() { return 'shared'; } } window.result"
                                        + " = new Button()."
                                        + method + "();"),
                        false);
                String js = Files.readString(directory.resolve("app.js"));
                assertTrue(js.contains("style".equals(method) ? "\"a\"" : "\"shared\""), js);
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

    @Test
    public void composedMapsCarryFullGeneratedJavaContentThroughCompilerReset() throws Exception {
        Path directory = Files.createTempDirectory("css-java-content");
        try {
            Path source = directory.resolve("input.js/demo/Styled_Button.impl.java.js");
            Files.createDirectories(source.getParent());
            mappedSource(source, "class Button { style() { return 'root'; }} window.result=new Button().style();");
            String java = Files.readString(source.resolveSibling("Styled_Button.java"));
            Path extern = directory.resolve("extern.js");
            Files.writeString(extern, "var window;");
            Path output = directory.resolve("app.js");
            Path map = directory.resolve("app.js.map");
            var link = testLink("//:app", true, List.of(value("style", "root")));
            var compiler = new StyleCompiler(link, directory.resolve("live.json"));
            assertEquals(
                    new Runner(compiler, new String[] {
                                "--js",
                                source.toString(),
                                "--externs",
                                extern.toString(),
                                "--env",
                                "CUSTOM",
                                "--compilation_level",
                                "ADVANCED",
                                "--create_source_map",
                                map.toString(),
                                "--source_map_include_content",
                                "--js_output_file",
                                output.toString()
                            })
                            .execute(),
                    0);
            compiler.finish(output);
            assertTrue(Files.exists(map), "ADVANCED: " + Files.readString(output));
            var document = JsonParser.parseString(Files.readString(map)).getAsJsonObject();
            var contents = document.getAsJsonArray("sourcesContent");
            assertNotNull(contents);
            assert null != contents;
            int found = -1;
            var sources = document.getAsJsonArray("sources");
            for (int i = 0; i < sources.size(); i++) {
                if (sources.get(i).getAsString().endsWith("demo/Styled_Button.java")) {
                    found = i;
                }
            }
            assertTrue(found >= 0, document.toString());
            assertFalse(contents.get(found).isJsonNull(), document.toString());
            assertEquals(contents.get(found).getAsString(), java);
            var consumer = new SourceMapConsumerV3();
            consumer.parse(document.toString());
            String js = Files.readString(output);
            int literal = js.indexOf("'root'");
            if (literal < 0) {
                literal = js.indexOf("\"root\"");
            }
            assertTrue(literal >= 0, js);
            var generatedLine = (int) js.substring(0, literal)
                            .chars()
                            .filter(c -> '\n' == c)
                            .count()
                    + 1;
            int generatedColumn = literal + 1 - js.lastIndexOf('\n', literal);
            var origin = consumer.getMappingForLine(generatedLine, generatedColumn);
            assertNotNull(origin, "ADVANCED: " + document);
            assert null != origin;
            assertTrue(origin.getOriginalFile().endsWith("demo/Styled_Button.java"));
            assertEquals(origin.getLineNumber(), 3);
        } finally {
            remove(directory);
        }
    }

    @Test
    public void developmentKeepsReadableBindingsAndTheirCompanionMapOrigins() throws Exception {
        Path directory = Files.createTempDirectory("css-development-map");
        try {
            Path source = directory.resolve("input.js/demo/Styled_Button.impl.java.js");
            Files.createDirectories(source.getParent());
            String code = "class Button { style() { return 'root'; }} window.result=new Button().style();";
            mappedSource(source, code);
            var compiler = new StyleCompiler(
                    testLink("//:app", false, List.of(value("style", "root"))), directory.resolve("live.json"));
            var extern = directory.resolve("extern.js");
            Files.writeString(extern, "var window;");
            var output = directory.resolve("app.js");
            assertEquals(
                    new Runner(compiler, new String[] {
                                "--js",
                                source.toString(),
                                "--externs",
                                extern.toString(),
                                "--env",
                                "CUSTOM",
                                "--compilation_level",
                                "BUNDLE",
                                "--js_output_file",
                                output.toString()
                            })
                            .execute(),
                    0);
            assertTrue(Files.readString(output).contains(code));
            var origin = compiler.getSourceMapping(source.toString(), 1, code.indexOf("return"));
            assertNotNull(origin);
            assert null != origin;
            assertTrue(origin.getOriginalFile().endsWith("demo/Styled_Button.java"));
            assertEquals(origin.getLineNumber(), 3);
            assertEquals(origin.getIdentifier(), "demo.Styled_Button.style");
        } finally {
            remove(directory);
        }
    }

    @Test
    public void compilerRejectsInvalidOrMismatchedCssDefines() throws Exception {
        Path directory = Files.createTempDirectory("css-defines");
        try {
            var source = directory.resolve("entry.js");
            var extern = directory.resolve("extern.js");
            Files.writeString(source, "window.result='ok';");
            Files.writeString(extern, "var window;");
            var link = new CssLink(
                    "//:app",
                    "1".repeat(64),
                    true,
                    List.of(),
                    new CssNames(new CssNames.Policy(true, ""), List.of()),
                    List.of());
            for (var define : List.of(
                    "react4j.css.compress_names='false'",
                    "react4j.css.name_prefix=true",
                    "react4j.css.compress_names=false",
                    "react4j.css.name_prefix='app-'")) {
                var report = directory.resolve("live.json");
                var error = expectThrows(
                        RuntimeException.class,
                        () -> new Runner(
                                        new StyleCompiler(link, report),
                                        new String[] {
                                            "--js",
                                            source.toString(),
                                            "--externs",
                                            extern.toString(),
                                            "--env",
                                            "CUSTOM",
                                            "--compilation_level",
                                            "ADVANCED",
                                            "--define=" + define,
                                            "--js_output_file",
                                            directory.resolve("app.js").toString()
                                        },
                                        false)
                                .execute());
                var messages = new StringBuilder();
                for (Throwable cause = error; null != cause; cause = cause.getCause()) {
                    messages.append(cause.getMessage());
                }
                assertTrue(
                        messages.toString().contains("Invalid CSS define type")
                                || messages.toString().contains("CSS name policy differs"),
                        messages.toString());
                assertFalse(Files.exists(report));
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
        String packageName = owner.contains(".") ? owner.substring(0, owner.lastIndexOf('.')) : "";
        var java = new StringBuilder(packageName.isEmpty() ? "" : "package " + packageName + ";\n");
        java.append("final class ").append(name).append(" {\n");
        var methods = Pattern.compile("([\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*)\\(\\)\\s*\\{")
                .matcher(code);
        while (methods.find()) {
            String body = code.substring(methods.end(), code.indexOf('}', methods.end()));
            var literal = Pattern.compile("return\\s+(['\"])(.*?)\\1").matcher(body);
            String value = literal.find() ? literal.group(2) : "";
            String declaration = "  String " + methods.group(1) + "() { return \""
                    + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"; }\n";
            var line = (int) java.chars().filter(c -> '\n' == c).count();
            generator.addMapping(
                    name + ".java",
                    owner + "." + methods.group(1),
                    new FilePosition(line, declaration.indexOf(methods.group(1) + "()")),
                    new FilePosition(0, methods.start()),
                    new FilePosition(0, code.indexOf('}', methods.end()) + 1));
            java.append(declaration);
        }
        java.append("}\n");
        Files.writeString(path.resolveSibling(name + ".java"), java);
        var output = new StringBuilder();
        generator.appendTo(output, name + ".js");
        Files.writeString(path.resolveSibling(name + ".js.map"), output);
        Files.writeString(path, code + "\n//# sourceMappingURL=" + name + ".js.map\n");
    }

    private static void compile(StyleCompiler compiler, Path directory, Map<String, String> sources) throws Exception {
        compile(compiler, directory, sources, true);
    }

    private static void compile(
            StyleCompiler compiler, Path directory, Map<String, String> sources, boolean uncompressed)
            throws Exception {
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
        assertEquals(new Runner(compiler, arguments.toArray(String[]::new), uncompressed).execute(), 0);
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
