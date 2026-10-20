package react4j.verification;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class CoverageInstrumenter {
    record Line(String source, int line) {}

    record Edge(String source, int line, int id) {}

    interface Locations {
        List<Line> lines(Node node, boolean completed);

        List<Edge> edges(Node node);
    }

    record Result(String javascript, String variable, List<Line> lines, List<Edge> edges) {}

    private final Locations locations;
    private final String variable;
    private final List<Line> lines = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    private CoverageInstrumenter(Locations locations, String variable) {
        this.locations = locations;
        this.variable = variable;
    }

    static Result instrument(String javascript, Locations locations) {
        var compiler = new Compiler();
        var options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT);
        options.setLanguageOut(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT);
        options.setPrettyPrint(true);
        compiler.initOptions(options);
        var script = compiler.parse(SourceFile.fromCode("runtime.js", javascript));
        if (null == script || !compiler.getErrors().isEmpty()) {
            throw new IllegalArgumentException("Cannot parse coverage JavaScript: " + compiler.getErrors());
        }
        var names = new HashSet<String>();
        names(script, names);
        String variable = "__react4jCoverage";
        while (names.contains(variable)
                || names.contains(variable + "$branch")
                || names.contains(variable + "$value")) {
            variable += "_";
        }
        var instrumenter = new CoverageInstrumenter(locations, variable);
        instrumenter.visit(script);
        String header = "var " + variable + "={lines:Array(" + instrumenter.lines.size()
                + ").fill(false),edges:Array(" + instrumenter.edges.size() + ").fill(false)};\n"
                + "function " + variable + "$branch(value,index,nullish){"
                + "var taken=nullish?(value!==null&&value!==void 0):!!value;"
                + variable + ".edges[index+(taken?0:1)]=true;return value;}\n"
                + "function " + variable + "$value(value,indices){"
                + "for(var i=0;i<indices.length;i++){" + variable + ".lines[indices[i]]=true;}return value;}\n";
        var helpers = compiler.parseSyntheticCode("coverage-helpers.js", header);
        script.addChildrenToFront(Objects.requireNonNull(helpers.removeChildren()));
        restoreStrict(script);
        return new Result(
                compiler.toSource(script), variable, List.copyOf(instrumenter.lines), List.copyOf(instrumenter.edges));
    }

    private static void restoreStrict(Node node) {
        if (node.isUseStrict() && (node.isScript() || node.isBlock())) {
            node.addChildToFront(IR.exprResult(IR.string("use strict")));
        }
        for (var child = node.getFirstChild(); null != child; child = child.getNext()) {
            restoreStrict(child);
        }
    }

    private static void names(Node node, Set<String> names) {
        if (node.isName()) {
            names.add(node.getString());
        }
        for (var child = node.getFirstChild(); null != child; child = child.getNext()) {
            names(child, names);
        }
    }

    private void visit(Node node) {
        var branchEdges = locations.edges(node);
        var enteredLines = locations.lines(node, false);
        var completedLines = locations.lines(node, true);
        for (var child = node.getFirstChild(); null != child; ) {
            var next = child.getNext();
            visit(child);
            child = next;
        }
        if (!branchEdges.isEmpty()) {
            branch(node, branchEdges);
        }
        if (node.isCall() && !enteredLines.isEmpty()) {
            completed(node, enteredLines);
        }
        if (node.isReturn() || node.isExprResult()) {
            var value = node.getFirstChild();
            if (null != value && !(node.isExprResult() && value.isStringLit())) {
                completed(value, completedLines);
            }
        } else if (node.isName()
                && null != node.getFirstChild()
                && null != node.getParent()
                && NodeUtil.isNameDeclaration(node.getParent())) {
            completed(node.getFirstChild(), completedLines);
        }
        if (node.isCatch()) {
            var body = Objects.requireNonNull(node.getLastChild());
            var entry = mark(enteredLines);
            if (null != entry) {
                body.addChildToFront(entry);
            }
        } else if (node.isFunction()) {
            var body = node.getLastChild();
            if (null != body && body.isBlock()) {
                var entry = mark(enteredLines);
                if (null != entry) {
                    body.addChildToFront(entry);
                }
                var exit = mark(completedLines);
                if (null != exit) {
                    body.addChildToBack(exit);
                }
            } else if (null != body) {
                completed(body, completedLines);
            }
        } else if (NodeUtil.isStatement(node)
                && !node.isFunction()
                && !node.isBlock()
                && !(node.isExprResult()
                        && Objects.requireNonNull(node.getFirstChild()).isStringLit())
                && null != node.getParent()
                && NodeUtil.isStatementBlock(node.getParent())) {
            var statement = mark(enteredLines);
            if (null != statement) {
                statement.insertBefore(node);
            }
        }
    }

    private void branch(Node node, List<Edge> origins) {
        if (node.isSwitch()) {
            var body = Objects.requireNonNull(node.getSecondChild());
            int count = body.getChildCount();
            boolean hasDefault = false;
            for (var child = body.getFirstChild(); null != child; child = child.getNext()) {
                hasDefault |= child.isDefaultCase();
            }
            if (origins.size() != count + (hasDefault ? 0 : 1)) {
                throw new IllegalArgumentException("Coverage switch inventory does not match its arms");
            }
            if (!hasDefault) {
                body.addChildToBack(IR.defaultCase(IR.block()));
            }
            int index = 0;
            for (var child = body.getFirstChild(); null != child; child = child.getNext()) {
                int id = edges.size();
                edges.add(origins.get(index++));
                var statement = IR.exprResult(
                        IR.assign(IR.getelem(IR.getprop(IR.name(variable), "edges"), IR.number(id)), IR.trueNode()));
                var block = child.getLastChild();
                if (null == block || !block.isBlock()) {
                    throw new IllegalArgumentException("Unsupported coverage switch arm");
                }
                block.addChildToFront(statement);
            }
            return;
        }
        Node condition =
                switch (node.getToken()) {
                    case IF, WHILE, HOOK, AND, OR, COALESCE -> node.getFirstChild();
                    case DO -> node.getLastChild();
                    case FOR, CALL -> node.getSecondChild();
                    default ->
                        throw new IllegalArgumentException("Unsupported owned coverage branch: " + node.getToken());
                };
        if (null == condition || condition.isEmpty() || 2 != origins.size()) {
            throw new IllegalArgumentException("Coverage decision inventory must contain two outcomes");
        }
        int id = edges.size();
        edges.addAll(origins);
        var parent = Objects.requireNonNull(condition.getParent());
        var next = condition.getNext();
        condition.detach();
        var wrapped = IR.call(
                IR.name(variable + "$branch"),
                condition,
                IR.number(id),
                Token.COALESCE == node.getToken() ? IR.trueNode() : IR.falseNode());
        insert(parent, next, wrapped);
    }

    private void completed(Node expression, List<Line> origins) {
        if (origins.isEmpty()) {
            return;
        }
        var indices = IR.arraylit();
        for (var origin : origins) {
            indices.addChildToBack(IR.number(lines.size()));
            lines.add(origin);
        }
        var parent = Objects.requireNonNull(expression.getParent());
        var next = expression.getNext();
        expression.detach();
        insert(parent, next, IR.call(IR.name(variable + "$value"), expression, indices));
    }

    private static void insert(Node parent, @Nullable Node next, Node replacement) {
        if (null == next) {
            parent.addChildToBack(replacement);
        } else {
            replacement.insertBefore(next);
        }
    }

    private @Nullable Node mark(List<Line> origins) {
        Node expression = null;
        for (var origin : origins) {
            int id = lines.size();
            lines.add(origin);
            var assignment =
                    IR.assign(IR.getelem(IR.getprop(IR.name(variable), "lines"), IR.number(id)), IR.trueNode());
            expression = null == expression ? assignment : IR.comma(expression, assignment);
        }
        return null == expression ? null : IR.exprResult(expression);
    }
}
