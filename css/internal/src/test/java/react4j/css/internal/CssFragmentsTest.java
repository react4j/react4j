package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;

public final class CssFragmentsTest {
    private static final String OWNER = "demo.Rules";
    private static final String FINGERPRINT = "1".repeat(64);

    private static CssResource provider() {
        return new CssResource(
                OWNER,
                CssResource.Kind.UTILITY,
                List.of(),
                List.of(),
                "",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                FINGERPRINT);
    }

    private static CssFragments.Fragment fragment(String candidate, String order, String value) {
        String name = "utility-" + candidate;
        String css = "." + name + "{padding:" + value + ";}\n";
        var metadata = new CssResource(
                OWNER,
                CssResource.Kind.UTILITY,
                List.of(),
                List.of(),
                "",
                List.of(),
                List.of(new CssResource.Symbol(CssResource.SymbolKind.CLASS, candidate, name, false)),
                List.of(new CssResource.Occurrence(
                        new CssResource.SymbolId(OWNER, CssResource.SymbolKind.CLASS, candidate),
                        1,
                        1 + name.length(),
                        List.of())),
                List.of(),
                FINGERPRINT);
        var map = new SourceMap.Writer()
                .append(
                        css,
                        new SourceMap.Location(
                                "rules.css",
                                "@utility earlier {padding:1rem;} @utility later {padding:2rem;} @utility same"
                                        + " {padding:1rem;}",
                                0,
                                0))
                .map("utility.css");
        return new CssFragments.Fragment(candidate, order, css, map, metadata);
    }

    private static CssResource consumer(String owner, List<CssFragments.Fragment> fragments) {
        var ids = new ArrayList<CssResource.SymbolId>();
        var values = new ArrayList<String>();
        for (var fragment : fragments) {
            ids.add(new CssResource.SymbolId(OWNER, CssResource.SymbolKind.CLASS, fragment.candidate()));
            values.add("utility-" + fragment.candidate());
        }
        ids.add(new CssResource.SymbolId(owner, CssResource.SymbolKind.CLASS, ""));
        values.add("root-" + owner.substring(owner.lastIndexOf('.') + 1));
        return new CssResource(
                owner,
                CssResource.Kind.COMPONENT,
                List.of(OWNER),
                List.of(),
                "demo.Styled_" + owner.substring(owner.lastIndexOf('.') + 1),
                List.of(new CssResource.Binding(
                        "style", CssResource.SymbolKind.CLASS, String.join(" ", values), List.of(OWNER), ids)),
                List.of(new CssResource.Symbol(CssResource.SymbolKind.CLASS, "", values.get(values.size() - 1), false)),
                List.of(),
                List.of());
    }

    private static void write(Path tree, CssResource metadata, String css) throws Exception {
        var path = tree.resolve(CssResource.path(metadata.owner()));
        Files.createDirectories(path.getParent());
        String name = path.getFileName().toString();
        Files.writeString(path, css + SourceMap.reference(name));
        Files.writeString(
                Path.of(path + ".map"), new SourceMap.Writer().append(css).map(name));
        Files.writeString(Path.of(path + ".json"), metadata.json());
    }

    private static void fragments(Path tree, String consumer, List<CssFragments.Fragment> values) throws Exception {
        Files.writeString(
                tree.resolve(CssResource.path(consumer) + ".fragments.json"),
                new CssFragments(consumer, values).json());
    }

    @Test
    public void actualTreeCollectionDeduplicatesAndOrdersCanonicalCandidateContributions() throws Exception {
        var directory = Files.createTempDirectory("utility-fragments-");
        try {
            var p = Files.createDirectory(directory.resolve("provider"));
            var a = Files.createDirectory(directory.resolve("first"));
            var b = Files.createDirectory(directory.resolve("second"));
            var earlier = fragment("earlier", "0001", "1rem");
            var later = fragment("later", "0002", "2rem");
            write(p, provider(), "");
            write(a, consumer("demo.First", List.of(later, earlier)), "");
            write(b, consumer("demo.Second", List.of(later)), "");
            fragments(a, "demo.First", List.of(later, earlier));
            fragments(b, "demo.Second", List.of(later));
            var first = CssCatalogue.read(List.of(p, a, b));
            var second = CssCatalogue.read(List.of(b, p, a));
            assertEquals(first, second);
            var combined = first.resources().get(OWNER);
            assertNotNull(combined);
            assert null != combined;
            assertEquals(combined.css(), earlier.css() + later.css());
            assertEquals(combined.metadata().fingerprint(), FINGERPRINT);
            assertEquals(combined.metadata().occurrences().size(), 2);
            assertEquals(
                    combined.metadata().occurrences().get(1).start(),
                    earlier.css().length() + 1);
            var names = CssNames.create(first, new CssNames.Policy(true, ""));
            var name = names.entries().stream()
                    .filter(entry -> "later".equals(entry.symbol().name()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(Set.copyOf(name.getters()), Set.of("demo.First#style", "demo.Second#style"));
            assertTrue(
                    combined.map().getAsJsonArray("sourcesContent").toString().contains("@utility earlier"));
            assertEquals(
                    CssFragments.read(new CssFragments("demo.First", List.of(later)).json(), "demo.First")
                            .fragments(),
                    List.of(later));
        } finally {
            delete(directory);
        }
    }

    @Test
    public void mismatchedFingerprintsConflictingBytesAndMissingFragmentsFailWithOrigins() throws Exception {
        var directory = Files.createTempDirectory("utility-fragment-errors-");
        try {
            var p = Files.createDirectory(directory.resolve("provider"));
            var a = Files.createDirectory(directory.resolve("first"));
            var b = Files.createDirectory(directory.resolve("second"));
            var first = fragment("same", "0001", "1rem");
            write(p, provider(), "");
            write(a, consumer("demo.First", List.of(first)), "");
            write(b, consumer("demo.Second", List.of(first)), "");
            fragments(a, "demo.First", List.of(first));
            var changed = new CssFragments.Fragment(
                    first.candidate(),
                    first.orderKey(),
                    first.css(),
                    first.sourceMap(),
                    first.metadata().withFingerprint("2".repeat(64)));
            fragments(b, "demo.Second", List.of(changed));
            var fingerprint = expectThrows(IllegalArgumentException.class, () -> CssCatalogue.read(List.of(p, a, b)));
            assertTrue(String.valueOf(fingerprint.getMessage()).contains("fingerprint mismatch"));
            assertTrue(String.valueOf(fingerprint.getMessage()).contains("demo/Rules.css.json"));
            assertTrue(String.valueOf(fingerprint.getMessage()).contains("Second.css.fragments.json"));
            fragments(b, "demo.Second", List.of(fragment("same", "0001", "3rem")));
            var conflict = expectThrows(IllegalArgumentException.class, () -> CssCatalogue.read(List.of(p, a, b)));
            assertTrue(String.valueOf(conflict.getMessage()).contains("First.css.fragments.json"));
            assertTrue(String.valueOf(conflict.getMessage()).contains("Second.css.fragments.json"));
            Files.delete(a.resolve("demo/First.css.fragments.json"));
            Files.delete(b.resolve("demo/Second.css.fragments.json"));
            var missing = expectThrows(IllegalArgumentException.class, () -> CssCatalogue.read(List.of(p, a, b)));
            assertTrue(String.valueOf(missing.getMessage()).contains("Missing utility expansion fragment"));
            fragments(a, "demo.First", List.of(first));
            expectThrows(IllegalArgumentException.class, () -> CssCatalogue.read(List.of(a)));
        } finally {
            delete(directory);
        }
    }

    @Test
    public void fragmentsRejectMalformedSchemasAndNonCanonicalCandidates() {
        var fragment = fragment("same", "0001", "1rem");
        String json = new CssFragments("demo.First", List.of(fragment)).json();
        for (var invalid : List.of(
                "{}",
                "null",
                "[]",
                json.replace("\"version\":1,", "\"version\":0,"),
                json.replace("demo.First", "demo.Foreign"))) {
            expectThrows(IllegalArgumentException.class, () -> CssFragments.read(invalid, "demo.First"));
        }
        expectThrows(IllegalArgumentException.class, () -> new CssFragments("demo.First", List.of(fragment, fragment)));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssFragments.Fragment(
                        "other", "0001", fragment.css(), fragment.sourceMap(), fragment.metadata()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssFragments.Fragment("same", "", fragment.css(), fragment.sourceMap(), fragment.metadata()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssFragments.Fragment(
                        "same",
                        "0001",
                        fragment.css(),
                        fragment.sourceMap(),
                        fragment.metadata().withFingerprint("")));
        var map = JsonParser.parseString(fragment.sourceMap()).getAsJsonObject();
        map.addProperty("version", 4);
        expectThrows(
                IllegalArgumentException.class,
                () -> new CssFragments.Fragment("same", "0001", fragment.css(), map.toString(), fragment.metadata()));
        expectThrows(
                IllegalArgumentException.class,
                () -> CssResource.read(provider().json().replace("\"version\":11", "\"version\":10"), OWNER));
    }

    private static void delete(Path directory) throws Exception {
        try (var files = Files.walk(directory)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
