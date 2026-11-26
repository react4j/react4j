package react4j.verification;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.proto.Mapping.OriginalMapping;
import com.google.devtools.build.runfiles.AutoBazelRepository;
import com.google.devtools.build.runfiles.Runfiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import com.sun.source.tree.Tree;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.zip.ZipFile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AutoBazelRepository
final class RuntimeCoverage implements CoverageInstrumenter.Locations {
    private record Module(
            int start, int end, int[] lines, SourceMapConsumerV3 map, Map<String, JavaCoverage> sources) {}

    private record Origin(JavaCoverage source, int line, int position) {}

    private record Branch(JavaCoverage source, JavaCoverage.Decision decision) {}

    private final TreeMap<Integer, Module> modules = new TreeMap<>();
    private final Map<String, JavaCoverage> sources = new TreeMap<>();
    private final Set<CoverageInstrumenter.Edge> boundDecisions = new HashSet<>();
    private final Set<CoverageInstrumenter.Edge> ambiguousEdges = new HashSet<>();
    private final Map<CoverageInstrumenter.Edge, CoverageInstrumenter.Line> branchEntryLines = new HashMap<>();
    private final Map<Node, Branch> expressionBranches = new IdentityHashMap<>();

    RuntimeCoverage(
            String bundle,
            Map<String, LcovReport.Source> inventory,
            Map<String, Path> sourceJars,
            List<Path> trees,
            Set<String> declared)
            throws Exception {
        var archiveSources = new HashMap<String, String>();
        for (var entry : sourceJars.entrySet()) {
            try (var zip = new ZipFile(entry.getValue().toFile())) {
                for (var file :
                        zip.stream().filter(f -> f.getName().endsWith(".java")).toList()) {
                    String name = entry.getKey() + "/" + file.getName();
                    if (null
                            != archiveSources.putIfAbsent(
                                    name,
                                    new String(zip.getInputStream(file).readAllBytes(), StandardCharsets.UTF_8))) {
                        throw new IllegalArgumentException("Repeated declared Java source: " + name);
                    }
                }
            }
        }
        for (String name : declared) {
            String text = archiveSources.get(name);
            if (null == text) {
                throw new IllegalArgumentException("Missing declared source archive entry: " + name);
            }
            if (!inventory.containsKey(name)) {
                var missing = new JavaCoverage(name, text, new LcovReport.Source(Map.of(), Map.of(), Map.of()));
                if (missing.requiresInventory || !missing.methods.isEmpty()) {
                    throw new IllegalArgumentException("Missing executable Java inventory: " + name);
                }
            }
        }
        for (var entry : inventory.entrySet()) {
            if (sourceJars.keySet().stream().noneMatch(root -> entry.getKey().startsWith(root + "/"))) {
                continue;
            }
            if (!declared.contains(entry.getKey())) {
                throw new IllegalArgumentException("Undeclared owned Java source: " + entry.getKey());
            }
            String text = archiveSources.get(entry.getKey());
            if (null == text) {
                throw new IllegalArgumentException("Missing declared executable Java source: " + entry.getKey());
            }
            sources.put(entry.getKey(), new JavaCoverage(entry.getKey(), text, entry.getValue()));
        }
        if (sources.isEmpty()) {
            throw new IllegalArgumentException("Missing owned Java executable inventory");
        }
        var mapped = new HashSet<String>();
        for (var inputTree : trees) {
            var tree = inputTree.toRealPath();
            try (var files = Files.walk(tree)) {
                for (var file : files.filter(p -> p.toString().endsWith(".impl.java.js"))
                        .sorted()
                        .toList()) {
                    String text = Files.readString(file);
                    int start = bundle.indexOf(text);
                    if (0 > start || 0 <= bundle.indexOf(text, start + 1)) {
                        throw new IllegalArgumentException(
                                "Owned J2CL module must occur exactly once in the unpruned bundle: " + file);
                    }
                    String filename = file.getFileName().toString().replace(".impl.java.js", ".js.map");
                    Path mapPath = file.resolveSibling(filename);
                    if (!Files.isRegularFile(mapPath)) {
                        throw new IllegalArgumentException("Missing J2CL source map: " + mapPath);
                    }
                    var consumer = new SourceMapConsumerV3();
                    consumer.parse(Files.readString(mapPath));
                    var owned = new HashMap<String, JavaCoverage>();
                    String directory =
                            tree.relativize(file.getParent()).toString().replace('\\', '/');
                    for (String original : consumer.getOriginalSources()) {
                        if (original.endsWith(".native_js")
                                && !original.contains("/")
                                && !original.contains("\\")
                                && Files.isRegularFile(file.resolveSibling(original))) {
                            continue;
                        }
                        String suffix = directory + "/" + original;
                        if (archiveSources.keySet().stream().noneMatch(name -> name.endsWith("/" + suffix))) {
                            throw new IllegalArgumentException("Undeclared original J2CL map source: " + suffix);
                        }
                        if (original.contains("/") || original.contains("\\")) {
                            throw new IllegalArgumentException("Unexpected J2CL Java map path: " + original);
                        }
                        for (var source : sources.entrySet()) {
                            if (source.getKey().endsWith("/" + suffix)) {
                                if (null != owned.putIfAbsent(original, source.getValue())) {
                                    throw new IllegalArgumentException("Ambiguous Java map source: " + suffix);
                                }
                                mapped.add(source.getKey());
                            }
                        }
                    }
                    consumer.visitMappings((sourceName, symbol, original, generated, end) -> {
                        var source = owned.get(sourceName);
                        if (null != source) {
                            source.position(original.getLine() + 1, original.getColumn());
                        }
                    });
                    var lineOffsets = new ArrayList<Integer>();
                    lineOffsets.add(0);
                    for (int offset = 0; offset < text.length(); offset++) {
                        if ('\n' == text.charAt(offset)) {
                            lineOffsets.add(offset + 1);
                        }
                    }
                    if (null
                            != modules.putIfAbsent(
                                    start,
                                    new Module(
                                            start,
                                            start + text.length(),
                                            lineOffsets.stream()
                                                    .mapToInt(Integer::intValue)
                                                    .toArray(),
                                            consumer,
                                            Map.copyOf(owned)))) {
                        throw new IllegalArgumentException("Repeated J2CL module interval");
                    }
                }
            }
        }
        for (var source : sources.values()) {
            if (!source.executable.isEmpty() && !mapped.contains(source.name)) {
                throw new IllegalArgumentException("Missing owned executable J2CL source: " + source.name);
            }
        }
        int previousEnd = -1;
        for (var module : modules.values()) {
            if (previousEnd > module.start()) {
                throw new IllegalArgumentException("Overlapping J2CL modules");
            }
            previousEnd = module.end();
        }
    }

    private @Nullable Origin origin(Node node) {
        int offset = node.getSourceOffset();
        var entry = modules.floorEntry(offset);
        if (null == entry || offset >= entry.getValue().end()) {
            return null;
        }
        var module = entry.getValue();
        int local = offset - module.start();
        int index = Arrays.binarySearch(module.lines(), local);
        if (index < 0) {
            index = -index - 2;
        }
        OriginalMapping mapping = module.map().getMappingForLine(index + 1, local - module.lines()[index] + 1);
        if (null == mapping) {
            return null;
        }
        var source = module.sources().get(mapping.getOriginalFile());
        if (null != source) {
            return new Origin(
                    source,
                    mapping.getLineNumber(),
                    source.position(mapping.getLineNumber(), mapping.getColumnPosition() - 1));
        }
        return null;
    }

    @Override
    public List<CoverageInstrumenter.Line> lines(Node node, boolean completed) {
        if (node.isFunction()) {
            var parent = node.getParent();
            String name = null != parent && parent.isMemberFunctionDef() ? parent.getString() : "";
            var result = new HashSet<CoverageInstrumenter.Line>();
            var body = node.getLastChild();
            if (null != body) {
                functionLines(body, name, completed, result);
            }
            return result.stream()
                    .sorted(Comparator.comparing(CoverageInstrumenter.Line::source)
                            .thenComparingInt(CoverageInstrumenter.Line::line))
                    .toList();
        }
        var origin = origin(node);
        if (null == origin) {
            return List.of();
        }
        return completed
                ? origin.source().completed(origin.position())
                : origin.source().enteredAt(origin.line(), origin.position());
    }

    private void functionLines(Node node, String name, boolean completed, Set<CoverageInstrumenter.Line> result) {
        if (node.isFunction() || node.isClass()) {
            return;
        }
        var origin = origin(node);
        if (null != origin) {
            result.addAll(origin.source().functionProof(origin.position(), name, completed));
        }
        for (var child = node.getFirstChild(); null != child; child = child.getNext()) {
            functionLines(child, name, completed, result);
        }
    }

    @Override
    public List<CoverageInstrumenter.Edge> edges(Node node) {
        var branch = expressionBranches.remove(node);
        if (null != branch) {
            return bind(node, branch.source(), branch.decision());
        }
        if (isPatternBindingGuard(node)) {
            return List.of();
        }
        String kind = node.getToken().name();
        if (node.isAnd() || node.isOr() || node.isHook()) {
            var expression = bindExpressionBranches(node);
            if (!expression.isEmpty()) {
                return expression;
            }
        }
        if (!Set.of("IF", "HOOK", "AND", "OR", "COALESCE", "WHILE", "DO", "FOR", "SWITCH", "CALL")
                .contains(kind)) {
            return List.of();
        }
        if ("CALL".equals(kind)) {
            var callee = node.getFirstChild();
            if (null == callee || !callee.isGetProp() || !"$assert__boolean__void".equals(callee.getString())) {
                return List.of();
            }
            kind = "ASSERT";
        }
        Node condition =
                switch (node.getToken()) {
                    case FOR, CALL -> node.getSecondChild();
                    case DO -> node.getLastChild();
                    default -> node.getFirstChild();
                };
        var origins = new ArrayList<Origin>();
        if (null != condition) {
            branchOrigins(condition, kind, origins);
        }
        if (node.isAnd() || node.isOr()) {
            branchOrigins(Objects.requireNonNull(node.getLastChild()), kind, origins);
        }
        var direct = origin(node);
        if (origins.isEmpty() && null != direct && null != direct.source().decision(direct.position(), kind)) {
            origins.add(direct);
        }
        JavaCoverage source;
        JavaCoverage.Decision decision;
        if (origins.isEmpty()) {
            if (null == direct) {
                return List.of();
            }
            source = direct.source();
            decision = source.expressionDecision(direct.position(), kind);
            if (null == decision) {
                return List.of();
            }
        } else {
            source = origins.get(0).source();
            if (origins.stream().anyMatch(candidate -> candidate.source() != source)) {
                throw new IllegalArgumentException("Ambiguous Java branch sources");
            }
            decision = source.decision(origins.stream().map(Origin::position).toList(), kind);
            if (null == decision) {
                throw new IllegalArgumentException("Ambiguous Java branch positions: " + source.name);
            }
        }
        return bind(node, source, decision);
    }

    private List<CoverageInstrumenter.Edge> bindExpressionBranches(Node node) {
        var parent = node.getParent();
        if (null == parent) {
            return List.of();
        }
        var context =
                switch (parent.getToken()) {
                    case IF -> node == parent.getFirstChild() ? Tree.Kind.IF : null;
                    case WHILE -> node == parent.getFirstChild() ? Tree.Kind.WHILE_LOOP : null;
                    case DO -> node == parent.getLastChild() ? Tree.Kind.DO_WHILE_LOOP : null;
                    case FOR -> node == parent.getSecondChild() ? Tree.Kind.FOR_LOOP : null;
                    case RETURN -> Tree.Kind.RETURN;
                    case NAME ->
                        NodeUtil.isNameDeclaration(Objects.requireNonNull(parent.getParent()))
                                ? Tree.Kind.VARIABLE
                                : null;
                    default -> null;
                };
        if (null == context) {
            return List.of();
        }
        var anchor = origin(parent);
        if (null == anchor) {
            return List.of();
        }
        // Nested operators can share one map location. Anchor the whole expression to its
        // authored statement and require the same short-circuit and ternary structure.
        var authored = anchor.source().expressionBranch(anchor.position(), context);
        if (null == authored.decision() && Tree.Kind.RETURN == context && isSwitchArmReturn(parent)) {
            authored = anchor.source().expressionBranch(anchor.position(), Tree.Kind.CASE);
        }
        var decision = authored.decision();
        var matches = new IdentityHashMap<Node, Branch>();
        if (null == decision || !matchExpressionBranches(node, authored, anchor.source(), matches)) {
            return List.of();
        }
        matches.remove(node);
        if (matches.isEmpty()) {
            return List.of();
        }
        expressionBranches.putAll(matches);
        return bind(node, anchor.source(), decision);
    }

    private static boolean isSwitchArmReturn(Node statement) {
        var block = statement.getParent();
        var arm = null != block && block.isBlock() ? block.getParent() : null;
        return null != arm && (arm.isCase() || arm.isDefaultCase());
    }

    private static boolean matchExpressionBranches(
            Node node, JavaCoverage.ExpressionBranch authored, JavaCoverage source, Map<Node, Branch> matches) {
        var decision = authored.decision();
        if (null == decision) {
            return !(node.isAnd() || node.isOr() || node.isHook()) || isPatternBindingGuard(node);
        }
        if (!decision.kind().equals(node.getToken().name())) {
            return false;
        }
        var child = node.getFirstChild();
        for (var operand : authored.operands()) {
            if (null == child || !matchExpressionBranches(child, operand, source, matches)) {
                return false;
            }
            child = child.getNext();
        }
        matches.put(node, new Branch(source, decision));
        return true;
    }

    private List<CoverageInstrumenter.Edge> bind(Node node, JavaCoverage source, JavaCoverage.Decision decision) {
        var result = new ArrayList<CoverageInstrumenter.Edge>();
        // J2CL emits a JavaScript case for each label, with all labels in a Java arm sharing one outcome.
        var arms = node.isSwitch()
                ? decision.switchArms()
                : IntStream.range(0, decision.edges()).boxed().toList();
        for (int edge : arms) {
            var origin =
                    new CoverageInstrumenter.Edge(source.name, decision.range().firstLine(), decision.id() + edge);
            result.add(origin);
            var entered = source.branchEntryLine(decision, edge);
            if (null != entered) {
                branchEntryLines.put(origin, entered);
            }
        }
        if (!boundDecisions.add(result.getFirst())) {
            ambiguousEdges.addAll(result);
            return List.of();
        }
        return result;
    }

    private static boolean isPatternBindingGuard(Node node) {
        if (!node.isAnd()) {
            return false;
        }
        final var condition = Objects.requireNonNull(node.getFirstChild());
        final var binding = Objects.requireNonNull(node.getLastChild());
        final var target = condition.getFirstChild();
        // J2CL lowers an instanceof binding into a type check followed by an assignment and true.
        return condition.isCall()
                && null != target
                && target.isGetProp()
                && "$isInstance".equals(target.getString())
                && binding.isComma()
                && Objects.requireNonNull(binding.getFirstChild()).isAssign()
                && Objects.requireNonNull(binding.getLastChild()).isTrue();
    }

    private void branchOrigins(Node node, String kind, List<Origin> origins) {
        if (node.isFunction() || node.isClass()) {
            return;
        }
        var location = origin(node);
        if (null != location && null != location.source().decision(location.position(), kind)) {
            origins.add(location);
        }
        for (var child = node.getFirstChild(); null != child; child = child.getNext()) {
            branchOrigins(child, kind, origins);
        }
    }

    String report(CoverageInstrumenter.Result instrumentation, JsonArray lineHits, JsonArray edgeHits) {
        if (lineHits.size() != instrumentation.lines().size()
                || edgeHits.size() != instrumentation.edges().size()) {
            throw new IllegalArgumentException("Browser coverage counter inventory mismatch");
        }
        var lines = new HashMap<String, Set<Integer>>();
        var edges = new HashMap<String, Set<Integer>>();
        for (int i = 0; i < lineHits.size(); i++) {
            var hit = lineHits.get(i);
            if (!hit.isJsonPrimitive() || !hit.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("Invalid browser line counter");
            }
            if (hit.getAsBoolean()) {
                var origin = instrumentation.lines().get(i);
                lines.computeIfAbsent(origin.source(), ignored -> new HashSet<>())
                        .add(origin.line());
            }
        }
        for (int i = 0; i < edgeHits.size(); i++) {
            var hit = edgeHits.get(i);
            if (!hit.isJsonPrimitive() || !hit.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("Invalid browser branch counter");
            }
            if (hit.getAsBoolean()) {
                var origin = instrumentation.edges().get(i);
                if (!ambiguousEdges.contains(origin)) {
                    edges.computeIfAbsent(origin.source(), ignored -> new HashSet<>())
                            .add(origin.id());
                    var entered = branchEntryLines.get(origin);
                    if (null != entered) {
                        lines.computeIfAbsent(entered.source(), ignored -> new HashSet<>())
                                .add(entered.line());
                    }
                }
            }
        }
        var result = new TreeMap<String, LcovReport.Source>();
        sources.forEach((name, source) -> result.put(
                name, source.report(lines.getOrDefault(name, Set.of()), edges.getOrDefault(name, Set.of()))));
        return LcovReport.write(result);
    }

    public static void main(String[] args) throws Exception {
        if (10 != args.length) {
            throw new IllegalArgumentException(
                    "Expected bundle, browser metadata, two source archives, two module sets, source"
                            + " inventory, two class archives, output LCOV");
        }
        var runfiles = Runfiles.preload().withSourceRepository(AutoBazelRepository_RuntimeCoverage.NAME);
        var paths = new ArrayList<Path>();
        for (int i = 0; i < 9; i++) {
            paths.add(Path.of(Objects.requireNonNull(
                    runfiles.rlocation("_main/" + Path.of(args[i]).normalize()))));
        }
        var destination = Path.of(args[9]);
        Files.deleteIfExists(destination);
        String bundle = Files.readString(paths.get(0));
        var declared = Set.copyOf(Files.readAllLines(paths.get(6)));
        var collector = new RuntimeCoverage(
                bundle,
                JavaInventory.read(
                        Map.of("core/src/main/java", paths.get(7), "dom/src/main/java", paths.get(8)), declared),
                Map.of("core/src/main/java", paths.get(2), "dom/src/main/java", paths.get(3)),
                paths.subList(4, 6),
                declared);
        var instrumentation = CoverageInstrumenter.instrument(bundle, collector);
        var runs = new ArrayList<JsonObject>();
        for (String environment : List.of("development", "production")) {
            var defines = new JsonObject();
            defines.addProperty("goog.DEBUG", true);
            defines.addProperty("react4j.environment", environment);
            defines.addProperty("react4j.examples.environment", environment);
            defines.addProperty("arez.environment", "development");
            defines.addProperty("arez.enable_property_introspection", "true");
            defines.addProperty("arez.enable_spies", "true");
            defines.addProperty("arez.enable_names", "true");
            defines.addProperty("arez.check_invariants", "true");
            defines.addProperty("arez.check_api_invariants", "true");
            defines.addProperty("arez.enable_task_interceptor", "true");
            defines.addProperty("braincheck.environment", "development");
            defines.addProperty("braincheck.check_invariants", "true");
            defines.addProperty("braincheck.check_api_invariants", "true");
            defines.addProperty("braincheck.verbose_error_messages", "true");
            var assets = Map.of(
                    "/index.html",
                    "<!doctype html><body><script>var CLOSURE_DEFINES=" + defines
                            + ";window.coverageError=null;window.addEventListener('error',e=>window.coverageError=String(e.error||e.message));</script><script"
                            + " src='/runtime.js'></script>",
                    "/runtime.js",
                    instrumentation.javascript()
                            + "\n"
                            + "Promise.resolve(window.runRuntimeCoverageTests()).then(()=>window.coverageDone=true,e=>window.coverageError=String(e));");
            var result = BrowserCoverage.run(
                    paths.get(1),
                    assets,
                    "window.coverageError||window.coverageDone",
                    "({error:window.coverageError,counters:window['" + instrumentation.variable() + "']})");
            if (!result.get("error").isJsonNull()) {
                throw new IllegalStateException(
                        "Runtime browser test failed (" + environment + "): " + result.get("error"));
            }
            runs.add(result.getAsJsonObject("counters"));
        }
        Files.writeString(
                destination,
                collector.report(
                        instrumentation,
                        union(
                                runs.stream()
                                        .map(run -> run.getAsJsonArray("lines"))
                                        .toList(),
                                instrumentation.lines().size()),
                        union(
                                runs.stream()
                                        .map(run -> run.getAsJsonArray("edges"))
                                        .toList(),
                                instrumentation.edges().size())));
    }

    private static JsonArray union(List<JsonArray> runs, int size) {
        var hits = new boolean[size];
        for (var run : runs) {
            if (size != run.size()) {
                throw new IllegalArgumentException("Browser coverage counter inventory mismatch");
            }
            for (int i = 0; i < size; i++) {
                var value = run.get(i);
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
                    throw new IllegalArgumentException("Invalid browser coverage counter");
                }
                hits[i] |= value.getAsBoolean();
            }
        }
        var result = new JsonArray();
        for (boolean hit : hits) {
            result.add(hit);
        }
        return result;
    }
}
