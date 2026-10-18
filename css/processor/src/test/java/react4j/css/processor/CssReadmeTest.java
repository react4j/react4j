package react4j.css.processor;

import static org.testng.Assert.*;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.testing.StrictGeneratedCode;

@NullMarked
public final class CssReadmeTest {
    @Test
    public void everyJavaExampleCompilesWithTheProcessorAndClientApi() throws Exception {
        var blocks = Pattern.compile("(?ms)^```java\\R(.*?)^```").matcher(Files.readString(Path.of("css/README.md")));
        var imports = new TreeSet<String>(List.of(
                "import org.jspecify.annotations.NullMarked;",
                "import react4j.css.*;",
                "import react4j.dom.DOM;",
                "import react4j.dom.proptypes.html.BtnProps;",
                "import react4j.dom.proptypes.html.CssProps;"));
        var definitions = new StringBuilder();
        var statements = new StringBuilder();
        int count = 0;
        while (blocks.find()) {
            String code = blocks.group(1);
            var sourceImports = Pattern.compile("(?m)^import [^;]+;\\R?").matcher(code);
            while (sourceImports.find()) {
                imports.add(sourceImports.group().strip());
            }
            code = sourceImports.replaceAll("");
            (code.contains("@Css") || code.contains("@Keyframes") || code.contains("@GlobalCss")
                            ? definitions
                            : statements)
                    .append(code)
                    .append('\n');
            count++;
        }
        assertTrue(count > 0, "Missing Java examples");
        var directory = Files.createTempDirectory("css-readme-");
        try {
            String header = "package readme;\n" + String.join("\n", imports) + "\n";
            String text = header + definitions + "\n@NullMarked final class ReadmeClient { static void examples() {\n"
                    + statements + "\n} }\n";
            var generated = Files.createDirectory(directory.resolve("generated"));
            var classes = Files.createDirectory(directory.resolve("classes"));
            var diagnostics = new DiagnosticCollector<JavaFileObject>();
            var compiler = Objects.requireNonNull(ToolProvider.getSystemJavaCompiler());
            String classpath = System.getProperty("java.class.path");
            try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
                var document =
                        new SimpleJavaFileObject(URI.create("string:///Readme.java"), JavaFileObject.Kind.SOURCE) {
                            @Override
                            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                                return text;
                            }
                        };
                var parsed = (JavacTask)
                        compiler.getTask(null, manager, diagnostics, List.of("-proc:none"), null, List.of(document));
                var unit = parsed.parse().iterator().next();
                var positions = Trees.instance(parsed).getSourcePositions();
                var sources = new ArrayList<Path>();
                for (var declaration : unit.getTypeDecls()) {
                    assertTrue(declaration instanceof ClassTree, declaration.toString());
                    var type = (ClassTree) declaration;
                    var source = directory.resolve(type.getSimpleName() + ".java");
                    Files.writeString(
                            source,
                            header
                                    + text.substring(
                                            Math.toIntExact(positions.getStartPosition(unit, type)),
                                            Math.toIntExact(positions.getEndPosition(unit, type))));
                    sources.add(source);
                }
                var task = compiler.getTask(
                        null,
                        manager,
                        diagnostics,
                        List.of(
                                "--release",
                                "17",
                                "-classpath",
                                classpath,
                                "-d",
                                classes.toString(),
                                "-s",
                                generated.toString()),
                        null,
                        manager.getJavaFileObjectsFromPaths(sources));
                task.setProcessors(List.of(new CssProcessor()));
                assertTrue(task.call(), diagnostics.getDiagnostics().toString());
            }
            StrictGeneratedCode.check(generated, classes, classpath);
        } finally {
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
