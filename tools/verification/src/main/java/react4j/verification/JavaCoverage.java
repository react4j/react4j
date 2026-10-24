package react4j.verification;

import com.sun.source.tree.AssertTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchExpressionTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
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
    private final List<Range> statements = new ArrayList<>();
    final List<Range> methods = new ArrayList<>();
    private final List<Range> lambdas = new ArrayList<>();
    private final Map<Range, String> constructors = new LinkedHashMap<>();
    private final List<Decision> decisions = new ArrayList<>();
    private final Map<Range, Tree> expressions = new LinkedHashMap<>();
    boolean requiresInventory;
    private int nextEdge;
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
            new TreeScanner<@Nullable Void, @Nullable Void>() {
                private String owner = "";

                @Override
                public @Nullable Void visitClass(ClassTree type, @Nullable Void unused) {
                    String previous = owner;
                    owner += (owner.isEmpty() ? "" : "_") + type.getSimpleName();
                    var declaration = range(type);
                    var packageName = unit.getPackageName();
                    String prefix =
                            null == packageName ? "" : packageName.toString().replace('.', '_') + "_";
                    if (null != declaration) {
                        constructors.put(declaration, "$ctor__" + prefix + owner + "__");
                    }
                    try {
                        return super.visitClass(type, unused);
                    } finally {
                        owner = previous;
                    }
                }

                @Override
                public @Nullable Void scan(@Nullable Tree tree, @Nullable Void unused) {
                    if (null == tree) {
                        return null;
                    }
                    if (tree instanceof ClassTree
                            && (Tree.Kind.CLASS == tree.getKind()
                                    || Tree.Kind.ENUM == tree.getKind()
                                    || Tree.Kind.RECORD == tree.getKind())) {
                        requiresInventory = true;
                    }
                    var range = range(tree);
                    if (null != range) {
                        if (tree instanceof LambdaExpressionTree) {
                            lambdas.add(range);
                        }
                        if (tree instanceof MethodTree method && null != method.getBody()) {
                            methods.add(range);
                            if (!hasMethodInventory(method, range, inventory.functions(), executable)) {
                                throw new IllegalArgumentException(
                                        "Missing executable method inventory: " + name + ":" + method.getName());
                            }
                        }
                        if (tree instanceof ReturnTree
                                || tree instanceof ExpressionStatementTree
                                || tree instanceof VariableTree) {
                            statements.add(range);
                        }
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
                                    case CONDITIONAL_EXPRESSION -> "HOOK";
                                    case CONDITIONAL_AND -> "AND";
                                    case CONDITIONAL_OR -> "OR";
                                    case WHILE_LOOP -> "WHILE";
                                    case DO_WHILE_LOOP -> "DO";
                                    case FOR_LOOP -> null == ((ForLoopTree) tree).getCondition() ? "" : "FOR";
                                    case SWITCH, SWITCH_EXPRESSION -> "SWITCH";
                                    default -> "";
                                };
                        if (!kind.isEmpty()) {
                            int count = 2;
                            var cases = tree instanceof SwitchTree selection
                                    ? selection.getCases()
                                    : tree instanceof SwitchExpressionTree selection ? selection.getCases() : null;
                            if (null != cases) {
                                count = cases.size();
                                if (cases.stream()
                                        .flatMap(c -> c.getLabels().stream())
                                        .noneMatch(label -> Tree.Kind.DEFAULT_CASE_LABEL == label.getKind())) {
                                    count++;
                                }
                            }
                            decisions.add(new Decision(range, kind, count, nextEdge));
                            nextEdge += count;
                        }
                    }
                    return super.scan(tree, unused);
                }
            }.scan(unit, null);
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

    List<CoverageInstrumenter.Line> functionProof(int position, String generated, boolean completed) {
        if (generated.isEmpty()) {
            Range selected = null;
            for (var lambda : lambdas) {
                var expression = (LambdaExpressionTree) lambda.tree();
                var body = range(expression.getBody());
                if (expression.getBody() instanceof BlockTree
                        && null != body
                        && body.contains(position)
                        && (null == selected || body.end() - body.start() < selected.end() - selected.start())) {
                    selected = body;
                }
            }
            return null == selected ? List.of() : entered(completed ? selected.lastLine() : selected.firstLine());
        }
        for (var method : methods) {
            var declaration = (MethodTree) method.tree();
            String name = declaration.getName().toString();
            boolean constructor = "<init>".equals(name) && generated.startsWith("$ctor__");
            boolean match = constructor || generated.equals(name) || generated.startsWith("m_" + name + "__");
            if (match && method.contains(position)) {
                if (completed
                        && null != declaration.getReturnType()
                        && !"void".equals(declaration.getReturnType().toString())) {
                    return List.of();
                }
                return entered(completed ? method.lastLine() : method.firstLine());
            }
        }
        if (generated.startsWith("$ctor__")) {
            for (var type : constructors.entrySet()) {
                if (generated.startsWith(type.getValue())
                        && type.getKey().contains(position)
                        && ((ClassTree) type.getKey().tree())
                                .getMembers().stream()
                                        .noneMatch(member -> member instanceof MethodTree method
                                                && "<init>".contentEquals(method.getName()))) {
                    return entered(type.getKey().firstLine());
                }
            }
        }
        return List.of();
    }

    List<CoverageInstrumenter.Line> enteredAt(int line, int position) {
        boolean executableOrigin = methods.stream().anyMatch(method -> method.contains(position))
                || statements.stream()
                        .anyMatch(statement -> statement.contains(position)
                                && statement.tree() instanceof VariableTree variable
                                && null != variable.getInitializer());
        return executableOrigin ? entered(line) : List.of();
    }

    private List<CoverageInstrumenter.Line> entered(int line) {
        return executable.contains(line) ? List.of(new CoverageInstrumenter.Line(name, line)) : List.of();
    }

    List<CoverageInstrumenter.Line> completed(int position) {
        Range selected = null;
        for (var candidate : statements) {
            if (candidate.contains(position)
                    && (null == selected || candidate.end() - candidate.start() < selected.end() - selected.start())) {
                selected = candidate;
            }
        }
        if (null == selected) {
            return List.of();
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
                if (tree instanceof SwitchExpressionTree selection) {
                    return scan(selection.getExpression(), null);
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
        // J2CL lowers a switch expression inside a generated function. Its selector tests must not
        // bind to a Java decision surrounding the expression.
        Range boundary = null;
        for (var candidate : decisions) {
            if (candidate.range().tree() instanceof SwitchExpressionTree
                    && positions.stream().allMatch(candidate.range()::contains)
                    && (null == boundary || candidate.range().start() > boundary.start())) {
                boundary = candidate.range();
            }
        }
        Decision selected = null;
        for (var candidate : decisions) {
            if (candidate.kind().equals(kind)
                    && (null == boundary || boundary.contains(candidate.range().start()))
                    && positions.stream().allMatch(candidate.range()::contains)
                    && (null == selected
                            || candidate.range().end() - candidate.range().start()
                                    < selected.range().end() - selected.range().start())) {
                selected = candidate;
            }
        }
        return selected;
    }

    LcovReport.Source report(Set<Integer> lines, Set<Integer> edges) {
        var lineCounts = new TreeMap<Integer, Long>();
        executable.forEach(line -> lineCounts.put(line, lines.contains(line) ? 1L : 0L));
        var branches = new TreeMap<LcovReport.Branch, Long>();
        for (var decision : decisions) {
            for (int edge = 0; edge < decision.edges(); edge++) {
                int identity = decision.id() + edge;
                branches.put(
                        new LcovReport.Branch(decision.range().firstLine(), decision.id(), edge),
                        edges.contains(identity) ? 1L : 0L);
            }
        }
        return new LcovReport.Source(lineCounts, branches, Map.of());
    }
}
