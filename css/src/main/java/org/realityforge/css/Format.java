package org.realityforge.css;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.tools.ToolProvider;
import org.jspecify.annotations.Nullable;

/** Formats only CSS text blocks, preserving their cooked CSS through Java escaping. */
public final class Format {
    private record Edit(int start, int end, String text) {}

    private Format() {}

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || !(args[1].equals("check") || args[1].equals("write"))) {
            throw new IllegalArgumentException("Usage: format <css-tool> <check|write> <Java files...>");
        }
        boolean changed = false;
        for (int i = 2; i < args.length; i++) {
            var path = Path.of(args[i]);
            String before = Files.readString(path);
            String after = format(path, args[0]);
            if (!before.equals(after)) {
                changed = true;
                if (args[1].equals("write")) {
                    Files.writeString(path, after);
                } else {
                    System.err.println(path + ": embedded CSS requires formatting");
                }
            }
        }
        if (changed && args[1].equals("check")) {
            System.exit(1);
        }
    }

    static String format(Path path, String tool) throws IOException {
        var compiler = ToolProvider.getSystemJavaCompiler();
        try (var manager = compiler.getStandardFileManager(null, null, null)) {
            var task = (JavacTask) compiler.getTask(
                    null, manager, null, List.of("-proc:none"), null, manager.getJavaFileObjects(path));
            var unit = task.parse().iterator().next();
            var trees = Trees.instance(task);
            String source = Files.readString(path);
            var edits = new ArrayList<Edit>();
            new TreePathScanner<@Nullable Void, List<String>>() {
                @Override
                public @Nullable Void visitClass(ClassTree type, List<String> enclosing) {
                    var names = new ArrayList<>(enclosing);
                    names.add(type.getSimpleName().toString());
                    for (var annotation : type.getModifiers().getAnnotations()) {
                        String annotationName = annotation.getAnnotationType().toString();
                        if (!annotationName.equals("Css") && !annotationName.equals(Css.class.getName())) {
                            continue;
                        }
                        var expression = annotation.getArguments().get(0);
                        if (expression instanceof AssignmentTree assignment) {
                            expression = assignment.getExpression();
                        }
                        if (!(expression instanceof LiteralTree literal)
                                || !(literal.getValue() instanceof String value)) {
                            throw new IllegalArgumentException("@Css requires a literal Java text block");
                        }
                        var start = (int) trees.getSourcePositions().getStartPosition(unit, expression);
                        var end = (int) trees.getSourcePositions().getEndPosition(unit, expression);
                        var block = TextBlock.decode(source.substring(start, end), start, value);
                        var methods = type.getMembers().stream()
                                .filter(MethodTree.class::isInstance)
                                .map(MethodTree.class::cast)
                                .filter(method ->
                                        method.getModifiers().getFlags().contains(Modifier.ABSTRACT))
                                .map(method -> method.getName().toString())
                                .toList();
                        String binary = (unit.getPackageName() == null ? "" : unit.getPackageName() + ".")
                                + String.join("$", names);
                        try {
                            var result = CssBridge.compile(tool, block.value(), Names.root(binary), methods);
                            int lineStart = source.lastIndexOf('\n', start) + 1;
                            int leading = lineStart;
                            while (leading < start
                                    && (source.charAt(leading) == ' ' || source.charAt(leading) == '\t')) {
                                leading++;
                            }
                            String indent = source.substring(lineStart, leading) + "    ";
                            String escaped =
                                    result.formatted().replace("\\", "\\\\").replace("\"\"\"", "\\\"\"\"");
                            String replacement = "\"\"\"\n"
                                    + escaped.lines()
                                            .map(line -> line.isEmpty() ? "\n" : indent + line + "\n")
                                            .reduce("", String::concat)
                                    + indent + "\"\"\"";
                            edits.add(new Edit(start, end, replacement));
                        } catch (IOException e) {
                            throw new IllegalArgumentException(path + ": " + e.getMessage(), e);
                        }
                    }
                    return super.visitClass(type, names);
                }
            }.scan(unit, List.of());
            var result = new StringBuilder(source);
            for (var edit : edits.stream()
                    .sorted(Comparator.comparingInt(Edit::start).reversed())
                    .toList()) {
                result.replace(edit.start(), edit.end(), edit.text());
            }
            return result.toString();
        }
    }
}
