package react4j.verification;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/** Original Java obligations; browser source maps never supply the line denominator. */
@NullMarked
final class JavaCoverage {
    record Range(int start, int end, int firstLine, int lastLine, Tree tree) {
        boolean contains(int position) {
            return start <= position && position < end;
        }
    }

    record Decision(Range range, String kind, int edges, int id) {}

    final String name;
    final String text;
    final Set<Integer> executable;
    final Set<Integer> jvmLines = new HashSet<>();
    final List<Range> statements = new ArrayList<>();
    final List<Range> methods = new ArrayList<>();
    final List<Decision> decisions = new ArrayList<>();
    private final Map<Range, Tree> expressions = new LinkedHashMap<>();
    boolean requiresInventory;
    private int nextEdge;
    private final Set<Integer> browserOrigins = new HashSet<>();
    private final CompilationUnitTree unit;
    private final Trees trees;

    JavaCoverage(String name, String text, LcovReport.Source inventory) throws Exception {
        this.name = name;
        this.text = text;
        executable = inventory.lines().keySet();
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (null == compiler) {
            throw new IllegalStateException("Java coverage requires the JDK compiler");
        }
        try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
            var file = new SimpleJavaFileObject(URI.create("string:///" + name), JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return text;
                }
            };
            var task = (JavacTask)
                    compiler.getTask(null, manager, diagnostics, List.of("-proc:none"), null, List.of(file));
            unit = task.parse().iterator().next();
            trees = Trees.instance(task);
            if (diagnostics.getDiagnostics().stream().anyMatch(d -> Diagnostic.Kind.ERROR == d.getKind())) {
                throw new IllegalArgumentException(
                        "Cannot parse Java coverage source: " + name + diagnostics.getDiagnostics());
            }
            new TreeScanner<@Nullable Void, Boolean>() {
                @Override
                public @Nullable Void scan(@Nullable Tree tree, Boolean inherited) {
                    if (null == tree) {
                        return null;
                    }
                    boolean jvm = inherited || incompatible(tree);
                    if (tree instanceof ClassTree
                            && (Tree.Kind.CLASS == tree.getKind()
                                    || Tree.Kind.ENUM == tree.getKind()
                                    || Tree.Kind.RECORD == tree.getKind())) {
                        requiresInventory = true;
                    }
                    var range = range(tree);
                    if (null != range) {
                        if (tree instanceof MethodTree method && null != method.getBody()) {
                            methods.add(range);
                            if (!hasMethodInventory(method, range, inventory.functions(), executable)) {
                                throw new IllegalArgumentException(
                                        "Missing executable method inventory: " + name + ":" + method.getName());
                            }
                        }
                        if (jvm) {
                            executable.stream()
                                    .filter(line -> range.firstLine() <= line && line <= range.lastLine())
                                    .forEach(jvmLines::add);
                        }
                        if (tree instanceof ReturnTree
                                || tree instanceof ExpressionStatementTree
                                || tree instanceof VariableTree) {
                            statements.add(range);
                        }
                        if (!jvm) {
                            Tree expression =
                                    switch (tree.getKind()) {
                                        case IF -> ((IfTree) tree).getCondition();
                                        case RETURN -> ((ReturnTree) tree).getExpression();
                                        case VARIABLE -> ((VariableTree) tree).getInitializer();
                                        case EXPRESSION_STATEMENT -> ((ExpressionStatementTree) tree).getExpression();
                                        default -> null;
                                    };
                            if (null != expression) {
                                expressions.put(range, expression);
                            }
                            if (tree instanceof MethodTree
                                    || tree instanceof ReturnTree
                                    || tree instanceof ExpressionStatementTree
                                    || tree instanceof VariableTree variable && null != variable.getInitializer()) {
                                browserOrigins.add(range.firstLine());
                            }
                            String kind =
                                    switch (tree.getKind()) {
                                        case IF -> "IF";
                                        case ASSERT -> {
                                            if (null != ((AssertTree) tree).getDetail()) {
                                                throw new IllegalArgumentException(
                                                        "Unsupported owned Java assertion detail: " + name);
                                            }
                                            yield "ASSERT";
                                        }
                                        case ENHANCED_FOR_LOOP -> "FOR";
                                        case SWITCH_EXPRESSION ->
                                            throw new IllegalArgumentException(
                                                    "Unsupported owned Java switch expression: " + name);
                                        case CONDITIONAL_EXPRESSION -> "HOOK";
                                        case CONDITIONAL_AND -> "AND";
                                        case CONDITIONAL_OR -> "OR";
                                        case WHILE_LOOP -> "WHILE";
                                        case DO_WHILE_LOOP -> "DO";
                                        case FOR_LOOP -> null == ((ForLoopTree) tree).getCondition() ? "" : "FOR";
                                        case SWITCH -> "SWITCH";
                                        default -> "";
                                    };
                            if (!kind.isEmpty()) {
                                int count = 2;
                                if (tree instanceof SwitchTree selection) {
                                    count = selection.getCases().size();
                                    if (selection.getCases().stream()
                                            .noneMatch(c -> c.getExpressions().isEmpty())) {
                                        count++;
                                    }
                                }
                                decisions.add(new Decision(range, kind, count, nextEdge));
                                nextEdge += count;
                            }
                        }
                    }
                    return super.scan(tree, jvm);
                }
            }.scan(unit, false);
        }
        if (browserOrigins.stream().anyMatch(jvmLines::contains)) {
            throw new IllegalArgumentException("Ambiguous mixed-platform Java source line: " + name);
        }
        for (int line : executable) {
            if (line < 1 || line > unit.getLineMap().getLineNumber(text.length())) {
                throw new IllegalArgumentException("Invalid executable Java line: " + name + ":" + line);
            }
        }
    }

    private static boolean hasMethodInventory(
            MethodTree method, Range range, Map<String, LcovReport.Function> functions, Set<Integer> executable) {
        for (var entry : functions.entrySet()) {
            int line = entry.getValue().line();
            if (entry.getKey().contains("::" + method.getName() + " ")
                    && (range.firstLine() <= line && line <= range.lastLine())
                    && (null != method.getBody()
                                    && method.getBody().getStatements().isEmpty()
                            || executable.contains(line))) {
                return true;
            }
        }
        // JaCoCo removes private empty constructors from its executable inventory.
        return "<init>".contentEquals(method.getName())
                && method.getModifiers().getFlags().contains(Modifier.PRIVATE)
                && null != method.getBody()
                && method.getBody().getStatements().isEmpty();
    }

    private static boolean incompatible(Tree tree) {
        ModifiersTree modifiers = tree instanceof ClassTree type
                ? type.getModifiers()
                : tree instanceof MethodTree method
                        ? method.getModifiers()
                        : tree instanceof VariableTree field ? field.getModifiers() : null;
        return null != modifiers
                && modifiers.getAnnotations().stream().anyMatch(a -> {
                    String annotation = a.getAnnotationType().toString();
                    return "GwtIncompatible".equals(annotation) || annotation.endsWith(".GwtIncompatible");
                });
    }

    private @Nullable Range range(Tree tree) {
        long start = trees.getSourcePositions().getStartPosition(unit, tree);
        long end = trees.getSourcePositions().getEndPosition(unit, tree);
        return 0 > start || start >= end
                ? null
                : new Range(
                        (int) start,
                        (int) end,
                        (int) unit.getLineMap().getLineNumber(start),
                        (int) unit.getLineMap().getLineNumber(end - 1),
                        tree);
    }

    int position(int line, int column) {
        if (line < 1 || line > unit.getLineMap().getLineNumber(text.length()) || column < 0) {
            throw new IllegalArgumentException("Invalid original Java position: " + name + ":" + line + ":" + column);
        }
        int position = Math.toIntExact(unit.getLineMap().getPosition(line, column + 1));
        if (position > text.length() || unit.getLineMap().getLineNumber(position) != line) {
            throw new IllegalArgumentException("Java map column outside its line: " + name + ":" + line + ":" + column);
        }
        return position;
    }

    List<CoverageInstrumenter.Line> enteredAt(int line, int position) {
        boolean executableOrigin = methods.stream().anyMatch(method -> method.contains(position))
                || statements.stream()
                        .anyMatch(statement -> statement.contains(position)
                                && statement.tree() instanceof VariableTree variable
                                && null != variable.getInitializer());
        return executableOrigin ? entered(line) : List.of();
    }

    List<CoverageInstrumenter.Line> entered(int line) {
        return executable.contains(line) && !jvmLines.contains(line)
                ? List.of(new CoverageInstrumenter.Line(name, line))
                : List.of();
    }

    List<CoverageInstrumenter.Line> completed(int position, String kind) {
        var candidates = "FUNCTION".equals(kind) ? methods : statements;
        Range selected = null;
        for (var candidate : candidates) {
            if (candidate.contains(position)
                    && (null == selected || candidate.end() - candidate.start() < selected.end() - selected.start())) {
                selected = candidate;
            }
        }
        if (null == selected) {
            return List.of();
        }
        if ("FUNCTION".equals(kind)) {
            return selected.tree() instanceof MethodTree method && "void".equals(String.valueOf(method.getReturnType()))
                    ? entered(selected.lastLine())
                    : List.of();
        }
        var result = new HashSet<Integer>();
        new TreeScanner<@Nullable Void, @Nullable Void>() {
            @Override
            public @Nullable Void scan(@Nullable Tree tree, @Nullable Void unused) {
                if (null == tree || tree instanceof LambdaExpressionTree || tree instanceof ClassTree) {
                    return null;
                }
                var r = range(tree);
                if (null != r) {
                    result.add(r.firstLine());
                }
                if (tree instanceof ConditionalExpressionTree conditional) {
                    return scan(conditional.getCondition(), null);
                }
                if (tree instanceof BinaryTree binary
                        && (Tree.Kind.CONDITIONAL_AND == tree.getKind()
                                || Tree.Kind.CONDITIONAL_OR == tree.getKind())) {
                    return scan(binary.getLeftOperand(), null);
                }
                return super.scan(tree, unused);
            }
        }.scan(selected.tree(), null);
        return result.stream().sorted().flatMap(line -> entered(line).stream()).toList();
    }

    @Nullable
    Decision expressionDecision(int position, String kind) {
        Range selected = null;
        for (var expression : expressions.keySet()) {
            if (expression.contains(position)
                    && (null == selected
                            || expression.end() - expression.start() < selected.end() - selected.start())) {
                selected = expression;
            }
        }
        if (null == selected) {
            return null;
        }
        var candidates = new ArrayList<Decision>();
        new TreeScanner<@Nullable Void, @Nullable Void>() {
            @Override
            public @Nullable Void scan(@Nullable Tree tree, @Nullable Void unused) {
                if (null == tree || tree instanceof LambdaExpressionTree || tree instanceof ClassTree) {
                    return null;
                }
                decisions.stream()
                        .filter(d -> d.range().tree() == tree && d.kind().equals(kind))
                        .forEach(candidates::add);
                return super.scan(tree, unused);
            }
        }.scan(expressions.get(selected), null);
        return 1 == candidates.size() ? candidates.get(0) : null;
    }

    @Nullable
    Decision decision(int position, String kind) {
        return decision(List.of(position), kind);
    }

    @Nullable
    Decision decision(List<Integer> positions, String kind) {
        Decision selected = null;
        for (var candidate : decisions) {
            if (candidate.kind().equals(kind)
                    && positions.stream().allMatch(candidate.range()::contains)
                    && (null == selected
                            || candidate.range().end() - candidate.range().start()
                                    < selected.range().end() - selected.range().start())) {
                selected = candidate;
            }
        }
        return selected;
    }

    LcovReport.Source report(LcovReport.Source jvm, Set<Integer> lines, Set<Integer> edges) {
        var lineCounts = new TreeMap<Integer, Long>();
        executable.forEach(line ->
                lineCounts.put(line, jvmLines.contains(line) ? jvm.lines().get(line) : lines.contains(line) ? 1L : 0L));
        var branches = new TreeMap<LcovReport.Branch, Long>();
        jvm.branches().forEach((edge, hits) -> {
            if (jvmLines.contains(edge.line())) {
                branches.put(edge, hits);
            }
        });
        for (var decision : decisions) {
            for (int edge = 0; edge < decision.edges(); edge++) {
                int identity = decision.id() + edge;
                branches.put(
                        new LcovReport.Branch(decision.range().firstLine(), 100000 + decision.id(), edge),
                        edges.contains(identity) ? 1L : 0L);
            }
        }
        return new LcovReport.Source(lineCounts, branches, Map.of());
    }
}
