package react4j.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class SiteTest {
    @Test
    public void rendersDocumentationLinksTablesAndEscapedSources() {
        final var source =
                "package example;\n\n@Injectable\nclass Example {\n  String value = \"<h1>Hello</h1>\";\n}\n";
        final var html = Markdown.render("""
            ## @Typed on @Injectable types

            See {@link: react4j.annotations.View#name() @View.name} and [Fragments](fragments.md#cycles).
            Keep [GitHub](https://github.com/example/doc.md) unchanged.

            | Key | Value |
            | --- | --- |
            | a | b |

            <figure><img src="/img/example.svg" alt="Example"><figcaption>Caption</figcaption></figure>

            {@file_content: file=example/Example.java start_line=@Injectable}
            """, Map.of("example/Example.java", source));
        Assert.assertTrue(html.contains("id=\"typed-on-injectable-types\""));
        Assert.assertTrue(html.contains("href=\"/api/react4j/annotations/View.html#name()\""));
        Assert.assertTrue(html.contains("href=\"fragments.html#cycles\""));
        Assert.assertTrue(html.contains("href=\"https://github.com/example/doc.md\""));
        Assert.assertTrue(html.contains("<table>"));
        Assert.assertTrue(html.contains("<figcaption>Caption</figcaption>"));
        Assert.assertTrue(html.contains("&lt;h1&gt;Hello&lt;/h1&gt;"));
        Assert.assertFalse(html.contains("package example"));
    }

    @Test
    public void leavesDirectiveExamplesInsideCodeUntouched() {
        final var html = Markdown.render("""
            Use `{@link: ...}` or `{@file_content: ...}`.

            ```text
            {@link: ...}
            ```

                {@file_content: ...}
            """, Map.of());
        Assert.assertTrue(html.contains("<code>{@link: ...}</code>"));
        Assert.assertTrue(html.contains("{@file_content: ...}"));
    }

    @Test
    public void extractsQuotedBoundariesElidesAndRemovesCommonIndent() {
        final var source = """
            class Example {
                public static void main(String[] args) {
                    before();
                    // DOC ELIDE START
                    hidden();
                    // DOC ELIDE END
                    after();
                }
            }
            """;
        Assert.assertEquals(Markdown.snippet("""
            file=example/Example.java "start_line=public static void main" "end_line=^    }"
            include_start_line=false include_end_line=false
            """, Map.of("example/Example.java", source)), "before();\n...\nafter();");
        Assert.assertEquals(
                Markdown.snippet(
                        "path=generated/processors/main/java file=example/Sting_Example.java"
                                + " start_line=Sting_Example\\( end_line=\\)",
                        Map.of(
                                "example/Sting_Example.java",
                                "package example;\n  Sting_Example(\n      Object value)\n  {}\n")),
                "Sting_Example(\n    Object value)");
    }

    @Test
    public void failsWhenAnExampleDisappearsOrItsBoundaryChanges() {
        Assert.assertTrue(Assert.expectThrows(
                        IllegalArgumentException.class, () -> Markdown.snippet("file=missing.java", Map.of()))
                .toString()
                .contains("missing.java"));
        Assert.assertTrue(Assert.expectThrows(
                        IllegalArgumentException.class,
                        () -> Markdown.snippet(
                                "file=Example.java start_line=@Injectable",
                                Map.of("Example.java", "class Example {}\n")))
                .toString()
                .contains("@Injectable"));
    }

    @DataProvider
    public Object[][] headings() {
        return new Object[][] {
            {"@Typed on @Injectable types", "typed-on-injectable-types"},
            {"foo.bar(baz)?", "foobarbaz"},
            {"Why can't React's components render?", "why-cant-reacts-components-render"},
            {"Version ...", "version-"},
            {"Café & Niño", "cafe-nino"},
            {"change events/notifications", "change-eventsnotifications"}
        };
    }

    @Test(dataProvider = "headings")
    public void preservesPublicHeadingFragments(final String heading, final String fragment) {
        Assert.assertEquals(Markdown.slug(heading), fragment);
    }

    @Test
    public void fillsTemplatesOnceAndRejectsUnknownSlots() {
        Assert.assertEquals(
                SiteBuilder.template("<main>{{body}}</main>", Map.of("body", "{{title}} $1 \\")),
                "<main>{{title}} $1 \\</main>");
        Assert.expectThrows(IllegalArgumentException.class, () -> SiteBuilder.template("{{missing}}", Map.of()));
    }

    @Test
    public void readsTheExistingFrontmatterFormat() {
        final var page =
                SiteBuilder.readPage("inputs", "---\ntitle: Inputs\nsidebar_label: Injector Inputs\n---\nBody\n");
        Assert.assertEquals(page.title(), "Inputs");
        Assert.assertEquals(page.label(), "Injector Inputs");
        Assert.assertEquals(page.body(), "Body\n");
        Assert.expectThrows(
                IllegalArgumentException.class, () -> SiteBuilder.readPage("inputs", "---\nunknown: x\n---\n"));
    }

    @Test
    public void checksDecodedFragmentsDirectoryIndexesAndLocalAssets() throws IOException {
        final var root = site(Map.of(
                "index.html", "<a href=\"docs/#includes%28%29\">API</a><script src=\"local.js\"></script>",
                "docs/index.html", "<section id=\"includes()\"><a href=\"#includes()\">Method</a></section>",
                "local.js", "",
                "style.css", "body { background: url('icons/x.svg'); }",
                "icons/x.svg", "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>"));
        try {
            SiteLinks.check(root);
            Files.writeString(root.resolve("index.html"), "<a href=\"docs/#absent\">Broken</a>");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .toString()
                    .contains("#absent"));
            Files.writeString(root.resolve("index.html"), "<script src=\"https://example.com/code.js\"></script>");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .toString()
                    .contains("external asset"));
            for (final var route : List.of(
                    "drumloop",
                    "webspeechdemo",
                    "webspeechdemo_dev",
                    "heart-rate-monitor",
                    "heart-rate-monitor_dev",
                    "todomvc",
                    "todomvc/raw/todomvc",
                    "todomvc/raw/todomvc_dev",
                    "todomvc/arez/todomvc",
                    "todomvc/arez/todomvc_dev",
                    "todomvc/sting/todomvc",
                    "todomvc/sting/todomvc_dev")) {
                Files.writeString(root.resolve("index.html"), "<a href=\"/" + route + "/\">Missing owned example</a>");
                Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            }
            Files.writeString(root.resolve("index.html"), "<a href=\"/todomvc/../../outside.html\">Escapes</a>");
            Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            Files.writeString(root.resolve("index.html"), "<a href=\"../outside.html\">Escapes</a>");
            Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            Files.writeString(root.resolve("index.html"), "<a href=\"https://example.com/\">External page</a>");
            Files.writeString(root.resolve("style.css"), "@import url('https://fonts.example.com/font.css');");
            Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            Files.writeString(root.resolve("style.css"), "@import 'missing.css';");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .toString()
                    .contains("missing.css"));
            Files.writeString(
                    root.resolve("style.css"), "body { background: url('data:image/svg+xml; utf8, <svg></svg>'); }");
            SiteLinks.check(root);
        } finally {
            delete(root);
        }
    }

    @Test
    public void rejectsArchiveEntriesOutsideTheSite() {
        Assert.expectThrows(IOException.class, () -> SiteBuilder.destination(Path.of("site"), "../outside"));
    }

    @Test
    public void replacesPublishedAppsAndPreservesGitMetadata() throws IOException {
        final var source = site(Map.of(
                "index.html",
                "<main>New site</main>",
                "docs/overview.html",
                "<h1>Overview</h1>",
                "drumloop/index.html",
                "Owned drumloop",
                "drumloop/drumloop/index.html",
                "Production drumloop",
                "drumloop/drumloop_dev/index.html",
                "Development drumloop",
                "webspeechdemo/index.html",
                "Owned speech",
                "webspeechdemo_dev/index.html",
                "Development speech",
                "heart-rate-monitor/index.html",
                "Owned monitor",
                "heart-rate-monitor_dev/index.html",
                "Development monitor",
                "todomvc/raw/todomvc/index.html",
                "Owned TodoMVC"));
        final var destination = site(Map.of(
                "index.html",
                "Old site",
                "stale/page.html",
                "stale",
                ".git/config",
                "git metadata",
                "todomvc/raw/todomvc/index.html",
                "external app",
                "todomvc/dagger/todomvc/index.html",
                "legacy dagger",
                "drumloop/index.html",
                "external drumloop",
                "webspeechdemo/index.html",
                "external speech",
                "heart-rate-monitor/index.html",
                "external monitor"));
        try {
            SiteDeploy.prepare(source, destination);
            Assert.assertEquals(Files.readString(destination.resolve("index.html")), "<main>New site</main>");
            Assert.assertFalse(Files.exists(destination.resolve("stale")));
            Assert.assertEquals(Files.readString(destination.resolve(".git/config")), "git metadata");
            Assert.assertEquals(
                    Files.readString(destination.resolve("todomvc/raw/todomvc/index.html")), "Owned TodoMVC");
            Assert.assertEquals(Files.readString(destination.resolve("drumloop/index.html")), "Owned drumloop");
            Assert.assertEquals(Files.readString(destination.resolve("webspeechdemo/index.html")), "Owned speech");
            Assert.assertEquals(
                    Files.readString(destination.resolve("heart-rate-monitor/index.html")), "Owned monitor");
            Assert.assertEquals(
                    Files.readString(destination.resolve("heart-rate-monitor_dev/index.html")), "Development monitor");
            Assert.assertEquals(
                    Files.readString(destination.resolve("drumloop/drumloop/index.html")), "Production drumloop");
            Assert.assertEquals(
                    Files.readString(destination.resolve("drumloop/drumloop_dev/index.html")), "Development drumloop");
            Assert.assertEquals(
                    Files.readString(destination.resolve("webspeechdemo_dev/index.html")), "Development speech");
            Assert.assertFalse(Files.exists(destination.resolve("todomvc/dagger")));
            Files.createDirectories(source.resolve(".git"));
            Files.writeString(source.resolve(".git/config"), "collision");
            Assert.expectThrows(IOException.class, () -> SiteDeploy.prepare(source, destination));
            Assert.assertEquals(
                    Files.readString(destination.resolve("todomvc/raw/todomvc/index.html")), "Owned TodoMVC");
            Assert.expectThrows(IOException.class, () -> SiteDeploy.prepare(source, source));
        } finally {
            delete(source);
            delete(destination);
        }
    }

    @Test
    public void copiesPackagedApplicationTreesIncludingRepresentationsAndSourceMaps() throws Exception {
        var source = Path.of(Objects.requireNonNull(System.getProperty("application.directory")))
                .toRealPath();
        var output = Files.createTempDirectory("site-tree");
        try {
            var destination = output.resolve("examples/component");
            SiteBuilder.copyAsset(source, destination);
            try (var paths = Files.walk(source)) {
                for (var file : paths.filter(Files::isRegularFile).toList()) {
                    Assert.assertEquals(Files.mismatch(file, destination.resolve(source.relativize(file))), -1L);
                }
            }
            SiteBuilder.copyAsset(source.resolve("app.js"), output.resolve("plain.js"));
            Assert.assertEquals(Files.mismatch(source.resolve("app.js"), output.resolve("plain.js")), -1L);
            SiteLinks.check(output);
            Assert.expectThrows(IOException.class, () -> SiteBuilder.copyAsset(source, destination));
        } finally {
            delete(output);
        }
    }

    private static Path site(final Map<String, String> files) throws IOException {
        final var root = Files.createTempDirectory("react4j-site-test");
        for (final var entry : files.entrySet()) {
            final var file = root.resolve(entry.getKey());
            Files.createDirectories(file.getParent());
            Files.writeString(file, entry.getValue());
        }
        return root;
    }

    private static void delete(final Path root) throws IOException {
        try (var files = Files.walk(root)) {
            for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
