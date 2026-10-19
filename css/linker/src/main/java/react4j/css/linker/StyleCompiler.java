package react4j.css.linker;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CustomPassExecutionTime;
import com.google.javascript.jscomp.JSChunk;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.SourceMapInput;
import com.google.javascript.jscomp.jarjar.com.google.common.collect.ImmutableList;
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
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssNames;

final class StyleCompiler extends Compiler {
    private final CssLink link;
    private final Path report;
    private final Map<String, CssLink.Value> symbols = new LinkedHashMap<>();
    private final Map<String, String> compiledValues = new HashMap<>();
    private final Map<String, Map<String, String>> sources = new HashMap<>();
    private final Map<String, String> inputSources = new HashMap<>();
    private final Map<String, SourceMapConsumerV3> inputMaps = new HashMap<>();
    private @Nullable Set<String> liveValues;

    StyleCompiler(CssLink link, Path report) {
        this.link = link;
        this.report = report;
        if (!link.optimized()) {
            return;
        }
        var compiled = link.names().entries().stream()
                .collect(Collectors.toMap(CssNames.Entry::symbol, CssNames.Entry::compiled));
        for (var value : link.values()) {
            var returns = sources.computeIfAbsent(value.source(), ignored -> new HashMap<>());
            String symbol = "$react4j$css$" + link.digest() + "$" + symbols.size();
            returns.put(value.implementation() + "." + value.binding().method(), symbol);
            symbols.put(symbol, value);
            compiledValues.put(
                    symbol,
                    value.binding().symbols().stream()
                            .map(id -> Objects.requireNonNull(compiled.get(id), id.id()))
                            .collect(Collectors.joining(" ")));
        }
    }

    @Override
    public void initChunks(List<SourceFile> externs, List<JSChunk> chunks, CompilerOptions options) {
        var defines = options.getDefineReplacements();
        var settings = new ArrayList<String>();
        for (var key : List.of(CssNames.COMPRESS, CssNames.PREFIX)) {
            var value = defines.get(key);
            if (null != value) {
                if (CssNames.COMPRESS.equals(key) && !value.isTrue() && !value.isFalse()
                        || CssNames.PREFIX.equals(key) && !value.isStringLit()) {
                    throw new IllegalArgumentException("Invalid CSS define type: " + key);
                }
                settings.add("--define=" + key + "="
                        + (value.isStringLit() ? "'" + value.getString() + "'" : Boolean.toString(value.isTrue())));
            }
        }
        if (!link.names().policy().equals(CssNames.Policy.defines(settings, link.optimized()))) {
            throw new IllegalArgumentException("CSS name policy differs from Closure defines");
        }
        options.addUnknownDefinesToIgnore(ImmutableList.of(CssNames.COMPRESS, CssNames.PREFIX));
        var bindingSources = link.values().stream().map(CssLink.Value::source).collect(Collectors.toSet());
        var enrichedMaps = new HashMap<String, SourceMapInput>();
        var found = new HashSet<String>();
        for (var chunk : chunks) {
            for (var input : chunk.getInputs()) {
                String path = input.getName().replace('\\', '/');
                // J2CL modules are package-relative inside the declared .js tree artifact.
                int tree = path.lastIndexOf(".js/");
                String source = tree < 0 ? path : path.substring(tree + 4);
                if (bindingSources.contains(source)) {
                    inputSources.put(input.getName(), source);
                    var treeRoot = Path.of(input.getName()).toAbsolutePath();
                    for (int i = 0; i < Path.of(source).getNameCount(); i++) {
                        treeRoot = Objects.requireNonNull(treeRoot.getParent());
                    }
                    var map = BindingSourceMap.read(Path.of(input.getName()), treeRoot);
                    inputMaps.put(input.getName(), map.mapping());
                    enrichedMaps.put(input.getName(), map.input());
                    found.add(source);
                }
            }
        }
        if (link.optimized()) {
            for (var source : sources.keySet()) {
                if (!found.contains(source)) {
                    throw new IllegalArgumentException(
                            "CSS catalogue implementation is missing from this link: " + source);
                }
            }
            externs = new ArrayList<>(externs);
            var declarations = new StringBuilder();
            symbols.keySet()
                    .forEach(name -> declarations
                            .append("/** @const {string} */ var ")
                            .append(name)
                            .append(";\n"));
            externs.add(SourceFile.fromCode("react4j-css-symbols.externs.js", declarations.toString()));
            options.addCustomPass(CustomPassExecutionTime.BEFORE_CHECKS, (external, root) -> symbolize(root));
        }
        super.initChunks(externs, chunks, options);
        // Closure initialization clears input maps; register enriched maps afterward.
        enrichedMaps.forEach(this::addInputSourceMap);
    }

    private void symbolize(Node root) {
        var seen = new HashSet<String>();
        var parsed = new HashSet<String>();
        NodeTraversal.traverse(this, root, new NodeTraversal.AbstractPostOrderCallback() {
            @Override
            public void visit(NodeTraversal traversal, Node node, @Nullable Node parent) {
                if (!node.isScript() && !node.isReturn()) {
                    return;
                }
                String source = node.getSourceFileName();
                String generated = null == source ? null : inputSources.get(source);
                if (null == generated) {
                    return;
                }
                parsed.add(generated);
                var returns = Objects.requireNonNull(sources.get(generated));
                if (node.isReturn()) {
                    // J2CL records Java method identities independently of literal values and JSInterop names.
                    var mapping = Objects.requireNonNull(inputMaps.get(source))
                            .getMappingForLine(node.getLineno(), node.getCharno() + 1);
                    String symbol = null == mapping ? null : returns.get(mapping.getIdentifier());
                    var literal = node.getFirstChild();
                    if (null != symbol
                            && null != literal
                            && literal.isStringLit()
                            && literal.getString()
                                    .equals(Objects.requireNonNull(symbols.get(symbol))
                                            .binding()
                                            .value())) {
                        seen.add(symbol);
                        literal.replaceWith(IR.name(symbol).srcref(literal));
                        reportChangeToEnclosingScope(node);
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
                            node.replaceWith(IR.string(Objects.requireNonNull(compiledValues.get(node.getString())))
                                    .srcref(node));
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
