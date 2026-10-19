package react4j.css.bundler;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLink;
import react4j.css.internal.MappedText;
import react4j.css.internal.SourceMap;
import react4j.css.linker.BindingSourceMap;

final class AssetJavaScript {
    private AssetJavaScript() {}

    static MappedText rewrite(String javascript, JsonObject map, CssLink link, Map<String, String> urls) {
        var compiler = new Compiler();
        var options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT);
        compiler.initOptions(options);
        var script = compiler.parse(SourceFile.fromCode("app.js", javascript));
        if (null == script || !compiler.getErrors().isEmpty()) {
            throw new IllegalArgumentException("Invalid linked JavaScript: " + compiler.getErrors());
        }
        var names = new HashSet<String>();
        var literals = new ArrayList<Node>();
        var origins = new SourceMapConsumerV3();
        try {
            origins.parse(map.toString());
        } catch (SourceMapParseException | RuntimeException e) {
            throw new IllegalArgumentException("Invalid JavaScript source map", e);
        }
        var methods = new TreeMap<String, String>();
        link.assets()
                .forEach(asset -> methods.put(
                        asset.implementation() + "." + asset.binding().method(),
                        asset.binding().value()));
        var seen = new HashSet<String>();
        var unknown = new ArrayList<String>();
        NodeTraversal.traverse(compiler, script, new NodeTraversal.AbstractPostOrderCallback() {
            @Override
            public void visit(NodeTraversal traversal, Node node, @Nullable Node parent) {
                if (node.isName()) {
                    names.add(node.getString());
                } else if (node.isStringLit() && urls.containsKey(node.getString())) {
                    boolean binding = link.optimized();
                    if (!binding && null != parent && parent.isReturn()) {
                        var origin = origins.getMappingForLine(parent.getLineno(), parent.getCharno() + 1);
                        binding = null != origin && node.getString().equals(methods.get(origin.getIdentifier()));
                    }
                    if (binding) {
                        literals.add(node);
                        seen.add(node.getString());
                    }
                } else if (node.isStringLit() && node.getString().startsWith("react4j-asset:")) {
                    unknown.add(node.getString());
                }
            }
        });
        if (!unknown.isEmpty()) {
            throw new IllegalArgumentException("Asset URL token has no live binding: " + unknown.get(0));
        }
        if (link.optimized() && !seen.equals(urls.keySet())) {
            throw new IllegalArgumentException("Live asset bindings differ from the linked JavaScript");
        }
        String base = "$r4jAssetBase";
        while (names.contains(base)) {
            base += "_";
        }
        var edits = new ArrayList<SourceMap.Edit>();
        if (!literals.isEmpty()) {
            var scope = captureScope(script, literals);
            int prologue = scope.isScript() ? 0 : scope.getSourceOffset() + 1;
            for (var node = scope.getFirstChild(); null != node; node = node.getNext()) {
                var expression = node.getFirstChild();
                if (!node.isExprResult() || null == expression || !expression.isStringLit()) {
                    prologue = node.getSourceOffset();
                    break;
                }
                prologue = node.getSourceOffset() + node.getLength();
            }
            edits.add(new SourceMap.Edit(
                    prologue,
                    prologue,
                    (0 == prologue ? "" : ";\n") + "var " + base
                            + "=new globalThis.URL('.',globalThis.document.currentScript.src).href;\n"));
            for (var literal : literals) {
                int start = literal.getSourceOffset();
                String url = Objects.requireNonNull(urls.get(literal.getString()));
                edits.add(new SourceMap.Edit(start, start + literal.getLength(), "(" + base + "+" + quote(url) + ")"));
            }
        }
        edits.add(new SourceMap.Edit(javascript.length(), javascript.length(), "\n//# sourceMappingURL=app.js.map\n"));
        edits.sort(Comparator.comparingInt(SourceMap.Edit::start));
        var result = new MappedText(javascript, map).replace(edits);
        result.map().addProperty("file", "app.js");
        return result;
    }

    private static Node captureScope(Node script, List<Node> literals) {
        Node statement = null;
        for (var node = script.getFirstChild(); null != node; node = node.getNext()) {
            if (node.isExprResult()
                    && Objects.requireNonNull(node.getFirstChild()).isStringLit()) {
                continue;
            }
            if (null != statement) {
                return script;
            }
            statement = node;
        }
        if (null == statement || !statement.isExprResult()) {
            return script;
        }
        var call = Objects.requireNonNull(statement.getFirstChild());
        while (call.isNot() || call.isVoid()) {
            call = Objects.requireNonNull(call.getFirstChild());
        }
        if (!call.isCall()) {
            return script;
        }
        var function = Objects.requireNonNull(call.getFirstChild());
        if (function.isGetProp() && ("call".equals(function.getString()) || "apply".equals(function.getString()))) {
            function = Objects.requireNonNull(function.getFirstChild());
        }
        if (!function.isFunction()
                || !Objects.requireNonNull(function.getLastChild()).isBlock()) {
            return script;
        }
        var body = Objects.requireNonNull(function.getLastChild());
        for (var literal : literals) {
            var parent = literal.getParent();
            while (null != parent && parent != body) {
                parent = parent.getParent();
            }
            if (null == parent) {
                return script;
            }
        }
        return body;
    }

    private static String quote(String value) {
        return new JsonPrimitive(value).toString();
    }

    static JsonObject mapping(String javascript, Path supplied, CssLink link, List<Path> trees) throws IOException {
        String source = Files.readString(supplied);
        if (!source.isBlank()) {
            return JsonParser.parseString(source).getAsJsonObject();
        }
        if (link.optimized()) {
            throw new IllegalArgumentException("Optimized JavaScript requires a source map");
        }
        // Closure BUNDLE preserves input bytes but emits no map. Compose the actual companion maps.
        var sections = new TreeMap<Integer, JsonObject>();
        var names = Stream.concat(
                        link.values().stream().map(CssLink.Value::source),
                        link.assets().stream().map(CssLink.Asset::source))
                .distinct()
                .sorted()
                .toList();
        for (var name : names) {
            Path input = null;
            Path tree = null;
            for (var candidate : trees.stream().distinct().toList()) {
                var path = candidate.resolve(name);
                if (Files.isRegularFile(path)) {
                    if (null != input) {
                        throw new IllegalArgumentException("Duplicate generated binding input: " + name);
                    }
                    input = path;
                    tree = candidate;
                }
            }
            if (null == input) {
                throw new IllegalArgumentException("Missing generated binding input: " + name);
            }
            String code = Files.readString(input);
            int start = javascript.indexOf(code);
            if (start < 0) {
                continue;
            }
            if (javascript.indexOf(code, start + code.length()) >= 0) {
                throw new IllegalArgumentException("Generated binding occurs twice in BUNDLE output: " + name);
            }
            var document = BindingSourceMap.read(input, Objects.requireNonNull(tree))
                    .document()
                    .deepCopy();
            String root =
                    document.has("sourceRoot") ? document.get("sourceRoot").getAsString() : "";
            var sources = new JsonArray();
            for (var item : document.getAsJsonArray("sources")) {
                var java = Objects.requireNonNull(input.toAbsolutePath().getParent())
                        .resolve(root)
                        .resolve(item.getAsString())
                        .normalize();
                if (!java.startsWith(tree.toAbsolutePath().normalize())) {
                    throw new IllegalArgumentException("Generated binding map leaves its declared tree: " + item);
                }
                sources.add(tree.toAbsolutePath()
                        .normalize()
                        .relativize(java)
                        .toString()
                        .replace('\\', '/'));
            }
            document.add("sources", sources);
            document.remove("sourceRoot");
            sections.put(start, document);
        }
        var result = new JsonObject();
        result.addProperty("version", 3);
        result.addProperty("file", "app.js");
        var values = new JsonArray();
        for (var entry : sections.entrySet()) {
            var section = new JsonObject();
            var offset = new JsonObject();
            int start = entry.getKey();
            offset.addProperty(
                    "line",
                    javascript
                            .substring(0, start)
                            .chars()
                            .filter(c -> '\n' == c)
                            .count());
            offset.addProperty("column", start - javascript.lastIndexOf('\n', start - 1) - 1);
            section.add("offset", offset);
            section.add("map", entry.getValue());
            values.add(section);
        }
        result.add("sections", values);
        return result;
    }
}
