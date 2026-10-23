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
import com.google.javascript.jscomp.parsing.parser.Parser;
import com.google.javascript.jscomp.parsing.parser.TokenType;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import com.google.javascript.rhino.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLink;
import react4j.css.internal.MappedText;
import react4j.css.internal.SourceMap;
import react4j.css.linker.BindingSourceMap;

final class AssetJavaScript {
    private static final String TOKEN_PREFIX = "react4j-asset:";
    private static final Pattern ASSET_TOKEN = assetToken(TOKEN_PREFIX);
    private static final Pattern READABLE_ASSET = assetToken("asset:");

    private AssetJavaScript() {}

    private static Pattern assetToken(String prefix) {
        var expression = new StringBuilder();
        for (char value : prefix.toCharArray()) {
            if (!expression.isEmpty()) {
                expression.append("(?:\\\\(?:\\r\\n|[\\r\\n\\u2028\\u2029]))*+");
            }
            String hex = Integer.toHexString(value);
            expression
                    .append("(?:")
                    .append(Pattern.quote(Character.toString(value)))
                    .append("|\\\\(?i:u(?:00")
                    .append(hex)
                    .append("|\\{0*")
                    .append(hex)
                    .append("\\})|x")
                    .append(hex)
                    .append("|0?")
                    .append(Integer.toOctalString(value))
                    .append(')');
            if ("bfnrtv0123456789".indexOf(value) < 0) {
                expression.append('|').append(Pattern.quote("\\" + value));
            }
            expression.append(')');
        }
        return Pattern.compile(expression.toString());
    }

    private static boolean hasAssetToken(String javascript) {
        if (javascript.contains(TOKEN_PREFIX)) {
            return true;
        }
        var matcher = ASSET_TOKEN.matcher(javascript);
        // The first escape in a token follows at most one raw prefix. Scan escapes rather than
        // matching the pattern at every position in a potentially very large development bundle.
        for (int escape = javascript.indexOf('\\'); escape >= 0; escape = javascript.indexOf('\\', escape + 1)) {
            for (int length = 0; length < TOKEN_PREFIX.length() && length <= escape; length++) {
                int start = escape - length;
                if (javascript.regionMatches(start, TOKEN_PREFIX, 0, length)
                        && matcher.region(start, javascript.length()).lookingAt()) {
                    return true;
                }
            }
        }
        return false;
    }

    static MappedText rewrite(String javascript, JsonObject map, CssLink link, Map<String, String> urls) {
        return rewrite(javascript, map, link, urls, true);
    }

    private static MappedText rewrite(
            String javascript, JsonObject map, CssLink link, Map<String, String> urls, boolean appendMap) {
        var compiler = new Compiler();
        var options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT);
        compiler.initOptions(options);
        var script = compiler.parse(SourceFile.fromCode("app.js", javascript));
        if (null == script || !compiler.getErrors().isEmpty()) {
            throw new IllegalArgumentException("Invalid linked JavaScript: " + compiler.getErrors());
        }
        var names = new HashSet<String>();
        var literals = new ArrayList<Literal>();
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
                }
            }
        });
        // Node columns saturate at 4095. The parser's public trees retain exact token ranges.
        var program = Objects.requireNonNull(new Parser(
                        new Parser.Config(),
                        new ErrorReporter() {
                            @Override
                            protected void reportError(SourcePosition position, String message) {
                                throw new IllegalArgumentException("Invalid linked JavaScript: " + message);
                            }

                            @Override
                            protected void reportWarning(SourcePosition position, String message) {}
                        },
                        new com.google.javascript.jscomp.parsing.parser.SourceFile("app.js", javascript))
                .parseProgram());
        var decoded = new HashMap<String, String>();
        visit(program, null, (tree, parent) -> {
            if (tree instanceof LiteralExpressionTree expression && TokenType.STRING == expression.literalToken.type) {
                String raw = expression.literalToken.asLiteral().value;
                if (!ASSET_TOKEN.matcher(raw).find()
                        && !READABLE_ASSET.matcher(raw).find()) {
                    return;
                }
                String value = decoded.computeIfAbsent(raw, literal -> {
                    var parsed = Objects.requireNonNull(
                            compiler.parse(SourceFile.fromCode("literal.js", "(" + literal + ");")));
                    var statement = Objects.requireNonNull(parsed.getFirstChild());
                    return Objects.requireNonNull(statement.getFirstChild()).getString();
                });
                if (urls.containsKey(value)) {
                    boolean binding = link.optimized();
                    if (!binding && parent instanceof ReturnStatementTree) {
                        var start = parent.location.start;
                        var origin = origins.getMappingForLine(start.line + 1, start.column + 1);
                        binding = null != origin && value.equals(methods.get(origin.getIdentifier()));
                    }
                    if (binding) {
                        literals.add(new Literal(tree.location.start.offset, tree.location.end.offset, value));
                        seen.add(value);
                    }
                } else if (value.startsWith(TOKEN_PREFIX)) {
                    unknown.add(value);
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
            var scope = captureScope(program, literals);
            int prologue = scope instanceof ProgramTree ? 0 : scope.location.start.offset + 1;
            var statements = scope instanceof ProgramTree global ? global.sourceElements : scope.asBlock().statements;
            for (var statement : statements) {
                if (!(statement instanceof ExpressionStatementTree expression)
                        || !(expression.expression instanceof LiteralExpressionTree literal)
                        || TokenType.STRING != literal.literalToken.type) {
                    prologue = statement.location.start.offset;
                    break;
                }
                prologue = statement.location.end.offset;
            }
            edits.add(new SourceMap.Edit(
                    prologue,
                    prologue,
                    (0 == prologue ? "" : ";\n") + "var " + base
                            + "=new globalThis.URL('.',globalThis.document.currentScript.src).href;\n"));
            for (var literal : literals) {
                String url = Objects.requireNonNull(urls.get(literal.value()));
                edits.add(new SourceMap.Edit(literal.start(), literal.end(), "(" + base + "+" + quote(url) + ")"));
            }
        }
        if (appendMap) {
            edits.add(new SourceMap.Edit(
                    javascript.length(), javascript.length(), "\n//# sourceMappingURL=app.js.map\n"));
        }
        edits.sort(Comparator.comparingInt(SourceMap.Edit::start));
        var result = new MappedText(javascript, map).replace(edits);
        if (appendMap) {
            result.map().addProperty("file", "app.js");
        }
        return result;
    }

    private record Literal(int start, int end, String value) {}

    private static void visit(
            ParseTree tree, @Nullable ParseTree parent, BiConsumer<ParseTree, @Nullable ParseTree> visitor) {
        visitor.accept(tree, parent);
        // The pinned parser model exposes child trees through public fields, without a visitor API.
        for (var field : tree.getClass().getFields()) {
            try {
                var value = field.get(tree);
                if (value instanceof ParseTree child) {
                    visit(child, tree instanceof ParenExpressionTree ? parent : tree, visitor);
                } else if (value instanceof Iterable<?> children) {
                    for (var item : children) {
                        if (item instanceof ParseTree child) {
                            visit(child, tree instanceof ParenExpressionTree ? parent : tree, visitor);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Inaccessible JavaScript parse-tree field", e);
            }
        }
    }

    private static ParseTree unparenthesized(ParseTree tree) {
        while (tree instanceof ParenExpressionTree parentheses) {
            tree = parentheses.expression;
        }
        return tree;
    }

    private static ParseTree captureScope(ProgramTree script, List<Literal> literals) {
        ParseTree statement = null;
        for (var node : script.sourceElements) {
            if (node instanceof ExpressionStatementTree expression
                    && expression.expression instanceof LiteralExpressionTree literal
                    && TokenType.STRING == literal.literalToken.type) {
                continue;
            }
            if (null != statement) {
                return script;
            }
            statement = node;
        }
        if (!(statement instanceof ExpressionStatementTree expression)) {
            return script;
        }
        var call = unparenthesized(expression.expression);
        while (call instanceof UnaryExpressionTree unary
                && ("!".equals(unary.operator.toString()) || "void".equals(unary.operator.toString()))) {
            call = unparenthesized(unary.operand);
        }
        if (!(call instanceof CallExpressionTree invocation)) {
            return script;
        }
        var function = unparenthesized(invocation.operand);
        if (function instanceof MemberExpressionTree member
                && ("call".equals(member.memberName.getValue()) || "apply".equals(member.memberName.getValue()))) {
            function = unparenthesized(member.operand);
        }
        if (!(function instanceof FunctionDeclarationTree declaration)
                || !(declaration.functionBody instanceof BlockTree body)) {
            return script;
        }
        for (var literal : literals) {
            if (literal.start() < body.location.start.offset || literal.end() > body.location.end.offset) {
                return script;
            }
        }
        return body;
    }

    private static String quote(String value) {
        return new JsonPrimitive(value).toString();
    }

    private record Input(int start, String code, JsonObject map, boolean assets) {}

    static MappedText finalizeBundle(
            String javascript, Path supplied, CssLink link, List<Path> trees, Map<String, String> urls)
            throws IOException {
        // BUNDLE inputs have already been checked by Closure. Only generated bindings need edits.
        // Reserved optimized tokens take the full validation path, including malformed/unbound tokens.
        if (link.optimized() || !Files.readString(supplied).isBlank() || hasAssetToken(javascript)) {
            return rewrite(javascript, mapping(javascript, supplied, link, trees), link, urls);
        }
        var inputs = inputs(javascript, link, trees);
        var output = new StringBuilder(javascript.length() + 128);
        var sections = new JsonArray();
        int cursor = 0;
        var position = new Position(0, 0);
        for (var input : inputs.values()) {
            output.append(javascript, cursor, input.start());
            position = position.advance(javascript, cursor, input.start());
            var mapped = input.assets()
                    ? rewrite(input.code(), input.map(), link, urls, false)
                    : new MappedText(input.code(), input.map());
            var section = new JsonObject();
            section.add("offset", position.json());
            section.add("map", mapped.map());
            sections.add(section);
            output.append(mapped.text());
            position = position.advance(mapped.text(), 0, mapped.text().length());
            cursor = input.start() + input.code().length();
        }
        output.append(javascript, cursor, javascript.length()).append("\n//# sourceMappingURL=app.js.map\n");
        return new MappedText(output.toString(), document(sections));
    }

    private record Position(int line, int column) {
        Position advance(String text, int start, int end) {
            int nextLine = line;
            int nextColumn = column;
            for (int i = start; i < end; i++) {
                if ('\n' == text.charAt(i)) {
                    nextLine++;
                    nextColumn = 0;
                } else {
                    nextColumn++;
                }
            }
            return new Position(nextLine, nextColumn);
        }

        JsonObject json() {
            var result = new JsonObject();
            result.addProperty("line", line);
            result.addProperty("column", column);
            return result;
        }
    }

    static JsonObject mapping(String javascript, Path supplied, CssLink link, List<Path> trees) throws IOException {
        String source = Files.readString(supplied);
        if (!source.isBlank()) {
            return JsonParser.parseString(source).getAsJsonObject();
        }
        if (link.optimized()) {
            throw new IllegalArgumentException("Optimized JavaScript requires a source map");
        }
        var sections = new JsonArray();
        var position = new Position(0, 0);
        int cursor = 0;
        for (var input : inputs(javascript, link, trees).values()) {
            position = position.advance(javascript, cursor, input.start());
            var section = new JsonObject();
            section.add("offset", position.json());
            section.add("map", input.map());
            sections.add(section);
            cursor = input.start();
        }
        return document(sections);
    }

    private static TreeMap<Integer, Input> inputs(String javascript, CssLink link, List<Path> trees)
            throws IOException {
        // Closure BUNDLE preserves input bytes but emits no map. Compose the actual companion maps.
        var sections = new TreeMap<Integer, Input>();
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
            boolean assets =
                    link.assets().stream().anyMatch(asset -> asset.source().equals(name));
            sections.put(start, new Input(start, code, document, assets));
        }
        return sections;
    }

    private static JsonObject document(JsonArray sections) {
        var result = new JsonObject();
        result.addProperty("version", 3);
        result.addProperty("file", "app.js");
        result.add("sections", sections);
        return result;
    }
}
