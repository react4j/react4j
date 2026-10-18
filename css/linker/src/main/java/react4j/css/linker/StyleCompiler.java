package react4j.css.linker;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CustomPassExecutionTime;
import com.google.javascript.jscomp.JSChunk;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLink;

final class StyleCompiler extends Compiler {
    private final CssLink link;
    private final Path report;
    private final Map<String, CssLink.Value> symbols = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> sources = new HashMap<>();
    private final Map<String, String> inputSources = new HashMap<>();
    private @Nullable Set<String> liveValues;

    StyleCompiler(CssLink link, Path report) {
        this.link = link;
        this.report = report;
        if (!link.optimized()) {
            return;
        }
        // Aliases returning the same value from one generated implementation have identical liveness semantics.
        for (var value : link.values()) {
            var returns = sources.computeIfAbsent(value.source(), ignored -> new HashMap<>());
            if (!returns.containsKey(value.binding().value())) {
                String symbol = "$react4j$css$" + link.digest() + "$" + symbols.size();
                returns.put(value.binding().value(), symbol);
                symbols.put(symbol, value);
            }
        }
    }

    @Override
    public void initChunks(List<SourceFile> externs, List<JSChunk> chunks, CompilerOptions options) {
        if (!link.optimized()) {
            super.initChunks(externs, chunks, options);
            return;
        }
        var found = new HashSet<String>();
        for (var chunk : chunks) {
            for (var input : chunk.getInputs()) {
                String path = input.getName().replace('\\', '/');
                // J2CL modules are package-relative inside the declared .js tree artifact.
                int tree = path.lastIndexOf(".js/");
                String source = tree < 0 ? path : path.substring(tree + 4);
                if (sources.containsKey(source)) {
                    inputSources.put(input.getName(), source);
                    found.add(source);
                }
            }
        }
        for (var source : sources.keySet()) {
            if (!found.contains(source)) {
                throw new IllegalArgumentException("CSS catalogue implementation is missing from this link: " + source);
            }
        }
        var extended = new ArrayList<>(externs);
        var declarations = new StringBuilder();
        symbols.keySet()
                .forEach(name -> declarations
                        .append("/** @const {string} */ var ")
                        .append(name)
                        .append(";\n"));
        extended.add(SourceFile.fromCode("react4j-css-symbols.externs.js", declarations.toString()));
        options.addCustomPass(CustomPassExecutionTime.BEFORE_CHECKS, (external, root) -> symbolize(root));
        super.initChunks(extended, chunks, options);
    }

    private void symbolize(Node root) {
        var seen = new HashSet<String>();
        var parsed = new HashSet<String>();
        NodeTraversal.traverse(this, root, new NodeTraversal.AbstractPostOrderCallback() {
            @Override
            public void visit(NodeTraversal traversal, Node node, @Nullable Node parent) {
                if (!node.isScript() && !(node.isStringLit() && null != parent && parent.isReturn())) {
                    return;
                }
                String source = node.getSourceFileName();
                String generated = null == source ? null : inputSources.get(source);
                if (null == generated) {
                    return;
                }
                parsed.add(generated);
                var returns = Objects.requireNonNull(sources.get(generated));
                if (node.isStringLit() && null != parent && parent.isReturn()) {
                    String symbol = returns.get(node.getString());
                    if (null != symbol) {
                        seen.add(symbol);
                        node.replaceWith(IR.name(symbol).srcref(node));
                        reportChangeToEnclosingScope(parent);
                    }
                }
            }
        });
        for (var source : parsed) {
            for (var symbol : Objects.requireNonNull(sources.get(source)).values()) {
                if (!seen.contains(symbol)) {
                    throw new IllegalArgumentException("Generated CSS binding differs from its catalogue: "
                            + Objects.requireNonNull(symbols.get(symbol)).id() + " in " + source);
                }
            }
        }
    }

    @Override
    public void performPostCompilationTasks() {
        super.performPostCompilationTasks();
        if (!link.optimized()) {
            return;
        }
        runInCompilerThread(() -> {
            var live = new HashSet<String>();
            NodeTraversal.traverse(this, getRoot().getLastChild(), new NodeTraversal.AbstractPostOrderCallback() {
                @Override
                public void visit(NodeTraversal traversal, Node node, @Nullable Node parent) {
                    if (node.isName()) {
                        var value = symbols.get(node.getString());
                        if (null != value) {
                            live.add(value.id());
                            node.replaceWith(IR.string(value.binding().value()).srcref(node));
                            reportChangeToEnclosingScope(parent);
                        }
                    }
                }
            });
            liveValues = Set.copyOf(live);
            return null;
        });
    }

    void finish(Path javascript) throws IOException {
        var live = liveValues;
        if (link.optimized() && null == live) {
            throw new IllegalArgumentException(
                    "Missing final CSS liveness evidence; use an optimizing Closure compilation");
        }
        Files.writeString(report, link.report(null == live ? Set.of() : live, CssLink.digest(javascript)));
    }
}
