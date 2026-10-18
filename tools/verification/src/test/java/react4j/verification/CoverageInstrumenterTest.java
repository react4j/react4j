package react4j.verification;

import static org.testng.Assert.*;

import com.google.javascript.rhino.Node;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CoverageInstrumenterTest {
    private static final class Locations implements CoverageInstrumenter.Locations {
        private int nextEdge;

        @Override
        public List<CoverageInstrumenter.Line> lines(Node node, boolean completed) {
            return List.of(new CoverageInstrumenter.Line(completed ? "completed.js" : "entered.js", node.getLineno()));
        }

        @Override
        public List<CoverageInstrumenter.Edge> edges(Node node) {
            int count =
                    switch (node.getToken()) {
                        case IF, WHILE, DO, HOOK, AND, OR, COALESCE -> 2;
                        case FOR ->
                            Objects.requireNonNull(node.getSecondChild()).isEmpty() ? 0 : 2;
                        case SWITCH ->
                            Objects.requireNonNull(node.getSecondChild()).getChildCount() + (hasDefault(node) ? 0 : 1);
                        default -> 0;
                    };
            var result = new ArrayList<CoverageInstrumenter.Edge>();
            for (int i = 0; i < count; i++) {
                result.add(new CoverageInstrumenter.Edge(node.getToken().name(), node.getLineno(), nextEdge++));
            }
            return result;
        }

        private static boolean hasDefault(Node node) {
            for (var child = Objects.requireNonNull(node.getSecondChild()).getFirstChild();
                    null != child;
                    child = child.getNext()) {
                if (child.isDefaultCase()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Test
    public void realBrowserPreservesValuesStrictModeSideEffectsAndUnexecutedOutcomes() throws Exception {
        String javascript = """
            'use strict';
            const __react4jCoverage = 'authored';
            let count = 0;
            function untouched() {
              return 17;
            }
            function choose(flag) {
              return flag ? 1 : (++count, 2);
            }
            function strict() { 'use strict'; return this; }
            const and = false && ++count;
            const or = 'value' || ++count;
            const coalesce = null ?? (++count, 9);
            const zero = 0 ?? ++count;
            for (let i = 0; i < 2; i++) { if (i === 1) break; }
            switch (2) {
              case 1: count += 20; break;
              case 2: count++;
              case 3: count++; break;
            }
            function explode() { throw new Error('expected'); }
            let afterThrow = 0;
            try {
              const failed = explode() + (++afterThrow, 3);
            } catch (e) {}
            let strictScript = false;
            try { undeclaredCoverageValue = 1; } catch (e) { strictScript = e instanceof ReferenceError; }
            window.snapshot = {and, or, coalesce, zero, count, afterThrow,
              chosen: choose(true), strict: strict() === undefined, strictScript,
              collision: __react4jCoverage};
            window.finished = true;
            """;
        var instrumented = CoverageInstrumenter.instrument(javascript, new Locations());
        assertNotEquals(instrumented.variable(), "__react4jCoverage");
        var result = BrowserCoverage.run(
                Path.of(Objects.requireNonNull(System.getProperty("react4j.browser.metadata"))),
                Map.of(
                        "/index.html",
                        "<!doctype html><script src='/fixture.js'></script>",
                        "/fixture.js",
                        instrumented.javascript()),
                "window.finished",
                "({snapshot:window.snapshot, lines:" + instrumented.variable() + ".lines,edges:"
                        + instrumented.variable() + ".edges})");
        var snapshot = result.getAsJsonObject("snapshot");
        assertFalse(snapshot.get("and").getAsBoolean());
        assertEquals(snapshot.get("or").getAsString(), "value");
        assertEquals(snapshot.get("coalesce").getAsInt(), 9);
        assertEquals(snapshot.get("zero").getAsInt(), 0);
        assertEquals(snapshot.get("count").getAsInt(), 3);
        assertEquals(snapshot.get("chosen").getAsInt(), 1);
        assertEquals(snapshot.get("afterThrow").getAsInt(), 0);
        assertTrue(snapshot.get("strict").getAsBoolean());
        assertTrue(snapshot.get("strictScript").getAsBoolean());
        assertEquals(snapshot.get("collision").getAsString(), "authored");
        var hits = result.getAsJsonArray("edges");
        for (var kind : List.of("HOOK", "AND", "OR", "FOR")) {
            var found = new ArrayList<Boolean>();
            for (int i = 0; i < instrumented.edges().size(); i++) {
                if (kind.equals(instrumented.edges().get(i).source())) {
                    found.add(hits.get(i).getAsBoolean());
                }
            }
            assertTrue(found.contains(true), kind + found);
            assertTrue(found.contains(false), kind + found);
        }
        var switchHits = new ArrayList<Boolean>();
        for (int i = 0; i < instrumented.edges().size(); i++) {
            if ("SWITCH".equals(instrumented.edges().get(i).source())) {
                switchHits.add(hits.get(i).getAsBoolean());
            }
        }
        assertEquals(switchHits, List.of(false, true, true, false));
        var lines = result.getAsJsonArray("lines");
        boolean untouched = false;
        boolean failedCompletion = false;
        for (int i = 0; i < instrumented.lines().size(); i++) {
            var origin = instrumented.lines().get(i);
            if ("entered.js".equals(origin.source()) && 4 == origin.line()) {
                untouched = true;
                assertFalse(lines.get(i).getAsBoolean());
            }
            if ("completed.js".equals(origin.source()) && 24 == origin.line()) {
                failedCompletion = true;
                assertFalse(lines.get(i).getAsBoolean());
            }
        }
        assertTrue(untouched);
        assertTrue(failedCompletion);
    }

    @Test
    public void functionDirectivesReceiversEvalConstructorsAndSuspensionKeepTheirSemantics() throws Exception {
        String javascript = """
            var local = 4;
            function sloppy() { return this === window; }
            function strict() { 'use strict'; return this === undefined; }
            class Base { constructor(value) { this.value = value; } }
            class Child extends Base { constructor() { super(7); } get read() { return this.value; } }
            const receiver = {value: 8, make() { return () => this.value; }};
            const evaluated = eval('local + 1');
            let n = 0;
            while (n < 2) { n++; }
            do { n--; } while (n > 0);
            for (let i = 0; i < 1; i++) { n++; }
            switch (99) { case 1: n += 20; }
            switch (99) { default: n += 2; break; case 3: n += 20; }
            function* values() { yield 1; return 3; }
            const iterator = values();
            const first = iterator.next().value;
            const second = iterator.next().value;
            async function later() { return await Promise.resolve(4); }
            later().then(value => {
              window.snapshot = {sloppy: sloppy(), strict: strict(), evaluated, n,
                constructed: new Child().read, arrow: receiver.make()(), first, second, value};
              window.finished = true;
            });
            """;
        var instrumented = CoverageInstrumenter.instrument(javascript, new Locations());
        var result = BrowserCoverage.run(
                Path.of(Objects.requireNonNull(System.getProperty("react4j.browser.metadata"))),
                Map.of(
                        "/index.html",
                        "<!doctype html><script src='/fixture.js'></script>",
                        "/fixture.js",
                        instrumented.javascript()),
                "window.finished",
                "({snapshot:window.snapshot,edges:" + instrumented.variable() + ".edges})");
        var snapshot = result.getAsJsonObject("snapshot");
        assertTrue(snapshot.get("sloppy").getAsBoolean());
        assertTrue(snapshot.get("strict").getAsBoolean());
        assertEquals(snapshot.get("evaluated").getAsInt(), 5);
        assertEquals(snapshot.get("n").getAsInt(), 3);
        assertEquals(snapshot.get("constructed").getAsInt(), 7);
        assertEquals(snapshot.get("arrow").getAsInt(), 8);
        assertEquals(snapshot.get("first").getAsInt(), 1);
        assertEquals(snapshot.get("second").getAsInt(), 3);
        assertEquals(snapshot.get("value").getAsInt(), 4);
        var hits = result.getAsJsonArray("edges");
        for (int i = 0; i < instrumented.edges().size(); i++) {
            if (List.of("WHILE", "DO", "FOR")
                    .contains(instrumented.edges().get(i).source())) {
                assertTrue(
                        hits.get(i).getAsBoolean(), instrumented.edges().get(i).toString());
            }
        }
        var switchHits = new ArrayList<Boolean>();
        for (int i = 0; i < instrumented.edges().size(); i++) {
            if ("SWITCH".equals(instrumented.edges().get(i).source())) {
                switchHits.add(hits.get(i).getAsBoolean());
            }
        }
        assertEquals(switchHits, List.of(false, true, true, false));
    }

    @Test
    public void malformedJavascriptAndInconsistentInventoriesFail() {
        expectThrows(
                IllegalArgumentException.class, () -> CoverageInstrumenter.instrument("function (", new Locations()));
        expectThrows(
                IllegalArgumentException.class,
                () -> CoverageInstrumenter.instrument(
                        "switch (1) { case 1: break; }", new CoverageInstrumenter.Locations() {
                            @Override
                            public List<CoverageInstrumenter.Line> lines(Node node, boolean completed) {
                                return List.of();
                            }

                            @Override
                            public List<CoverageInstrumenter.Edge> edges(Node node) {
                                return node.isSwitch()
                                        ? List.of(new CoverageInstrumenter.Edge("input.js", 1, 0))
                                        : List.of();
                            }
                        }));
    }
}
