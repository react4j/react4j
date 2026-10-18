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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLink;

final class StyleCompiler extends Compiler {
    private final CssLink link;
    private final Path report;
    private final Map<String, CssLink.Value> symbols = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> sources = new HashMap<>();

    StyleCompiler(CssLink link, Path report) {
        this.link = link;
        this.report = report;
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
        var inputs = new HashSet<String>();
        for (var chunk : chunks) {
            chunk.getInputs().forEach(input -> inputs.add(input.getName().replace('\\', '/')));
        }
        for (var source : sources.keySet()) {
            if (inputs.stream().noneMatch(path -> path.equals(source) || path.endsWith("/" + source))) {
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
        var resolved = new HashMap<String, Map<String, String>>();
        NodeTraversal.traverse(this, root, new NodeTraversal.AbstractPostOrderCallback() {
            @Override
            public void visit(NodeTraversal traversal, Node node, @Nullable Node parent) {
                String source = node.getSourceFileName();
                if (null == source) {
                    return;
                }
                var returns = resolved.computeIfAbsent(source, path -> {
                    for (var entry : sources.entrySet()) {
                        if (path.equals(entry.getKey()) || path.endsWith("/" + entry.getKey())) {
                            parsed.add(entry.getKey());
                            return entry.getValue();
                        }
                    }
                    return Map.of();
                });
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
            try {
                Files.writeString(report, link.report(live));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return null;
        });
    }
}
