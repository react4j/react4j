package org.realityforge.css;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.tools.ToolProvider;

/** Bazel adapter for the isolated proof; annotation processing emits both Java and CSS. */
public final class Compile {
    private Compile() {}

    public static void main(String[] args) {
        try {
            compile(args);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("CSS compilation interrupted");
            System.exit(1);
        }
    }

    private static void compile(String[] args) throws IOException, InterruptedException {
        String tool = Path.of(args[0]).toAbsolutePath().toString();
        var srcjar = Path.of(args[1]);
        String cssOutput = Path.of(args[2]).toAbsolutePath().toString();
        Path work = Files.createTempDirectory("jcss-compile-");
        try {
            Path generated = Files.createDirectory(work.resolve("sources"));
            Path classes = Files.createDirectory(work.resolve("classes"));
            var compiler = ToolProvider.getSystemJavaCompiler();
            try (var manager = compiler.getStandardFileManager(null, null, null)) {
                var sources = new ArrayList<Path>();
                var shared = new ArrayList<String>();
                for (int i = 3; i < args.length; i++) {
                    if (args[i].endsWith(".java")) {
                        sources.add(Path.of(args[i]));
                    } else {
                        shared.add("s:" + Path.of(args[i]).toAbsolutePath());
                    }
                }
                var options = List.of(
                        "--release",
                        "17",
                        "-proc:only",
                        "-classpath",
                        System.getProperty("java.class.path"),
                        "-s",
                        generated.toString(),
                        "-d",
                        classes.toString(),
                        "-Acss.tool=" + tool);
                var task = compiler.getTask(
                        null, manager, null, options, null, manager.getJavaFileObjectsFromPaths(sources));
                task.setProcessors(List.of(new CssProcessor()));
                if (!task.call()) {
                    throw new IOException("CSS extraction failed");
                }
                try (var zip = new ZipOutputStream(Files.newOutputStream(srcjar));
                        var paths = Files.walk(generated)) {
                    for (var path : paths.filter(Files::isRegularFile).sorted().toList()) {
                        var entry = new ZipEntry(
                                generated.relativize(path).toString().replace('\\', '/'));
                        entry.setTimeLocal(LocalDateTime.of(2010, 1, 1, 0, 0));
                        zip.putNextEntry(entry);
                        Files.copy(path, zip);
                        zip.closeEntry();
                    }
                }
                var command = new ArrayList<>(List.of(tool, "bundle", cssOutput));
                command.addAll(shared);
                try (var paths = Files.walk(classes)) {
                    paths.filter(p -> p.toString().endsWith(".css"))
                            .sorted()
                            .forEach(p -> command.add("c:" + p.toAbsolutePath()));
                }
                if (CssBridge.process(command).inheritIO().start().waitFor() != 0) {
                    throw new IOException("CSS bundling failed");
                }
            }
        } finally {
            try (var paths = Files.walk(work)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
