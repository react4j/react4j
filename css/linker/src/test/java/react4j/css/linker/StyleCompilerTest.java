package react4j.css.linker;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
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
            Path source = directory.resolve("demo/Styled_Button.impl.java.js");
            Files.createDirectories(source.getParent());
            String code = "class Styled_Button { style() { return 'root'; } success() { return 'root mod'; } dead() {"
                    + " return 'root dead'; } alias() { return 'root mod'; }} function helper(style) { return"
                    + " style.success(); } window.result = helper(new Styled_Button());";
            Files.writeString(source, code);
            Path other = directory.resolve("other.js");
            Files.writeString(other, "window.other = 'root dead';");
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
            assertEquals(link.readReport(Files.readString(report)), Set.of("demo.Button#success"));
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
            assertTrue(origin.getOriginalFile().endsWith("demo/Styled_Button.impl.java.js"));
            assertEquals(origin.getColumnPosition(), code.indexOf("'root mod'") + 1);
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
            assertEquals(factories.get(0).get().create(config, report).getClass(), Compiler.class);
            assertEquals(link(false).readReport(Files.readString(report)), Set.of());
            Files.writeString(config, link(true).json());
            assertTrue(new CssCompilerFactory().create(config, report) instanceof StyleCompiler);
        } finally {
            remove(directory);
        }
    }

    private static void remove(Path directory) throws Exception {
        try (var files = Files.walk(directory)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
