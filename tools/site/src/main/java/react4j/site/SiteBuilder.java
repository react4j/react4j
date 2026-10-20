package react4j.site;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SiteBuilder {
    private static final Pattern SLOT = Pattern.compile("\\{\\{([a-z_]+)}}");

    private SiteBuilder() {}

    static void build(
            final Map<String, Path> files,
            final List<Path> docs,
            final List<Path> sourceJars,
            final Map<Path, String> assets)
            throws IOException {
        final var output =
                Objects.requireNonNull(files.get("--output")).toAbsolutePath().normalize();
        Files.createDirectories(output);
        final var sources = readSources(sourceJars);
        final var pages = new TreeMap<String, Page>();
        for (final var doc : docs) {
            final var name = doc.getFileName().toString();
            final var id = name.substring(0, name.length() - 3);
            final var page = readPage(id, Files.readString(doc));
            if (pages.put(id, page) != null) {
                throw new IOException("Duplicate document: " + id);
            }
        }
        final var navigation = readNavigation(Objects.requireNonNull(files.get("--navigation")), pages);
        final var layout = Files.readString(files.get("--layout"));
        final var document = Files.readString(files.get("--document"));
        final var home = template(layout, Map.of("title", "React4j", "content", Files.readString(files.get("--home"))));
        write(output, "index.html", home);
        // Keep the published /en/ homepage URL.
        write(output, "en/index.html", home);
        final var ordered = new ArrayList<>(navigation.keySet());
        for (int i = 0; i < ordered.size(); i++) {
            final var id = ordered.get(i);
            final var page = Objects.requireNonNull(pages.get(id));
            final String body;
            try {
                body = Markdown.render(page.body(), sources);
            } catch (final IllegalArgumentException e) {
                throw new IOException("docs/" + id + ".md: " + e.getMessage(), e);
            }
            final var content = template(
                    document,
                    Map.of(
                            "id",
                            id,
                            "title",
                            Markdown.escape(page.title()),
                            "category",
                            Markdown.escape(Objects.requireNonNull(navigation.get(id))),
                            "sidebar",
                            sidebar(navigation, pages, id),
                            "body",
                            body,
                            "previous",
                            i == 0 ? "" : pageLink(Objects.requireNonNull(pages.get(ordered.get(i - 1))), "Previous"),
                            "next",
                            i + 1 == ordered.size()
                                    ? ""
                                    : pageLink(Objects.requireNonNull(pages.get(ordered.get(i + 1))), "Next")));
            write(
                    output,
                    "docs/" + id + ".html",
                    template(
                            layout, Map.of("title", Markdown.escape(page.title()) + " · React4j", "content", content)));
        }
        extractApi(Objects.requireNonNull(files.get("--api")), output);
        for (final var asset : assets.entrySet()) {
            final var destination = destination(output, asset.getValue());
            copyAsset(asset.getKey(), destination);
        }
        write(
                output,
                "examples/hello_world.html",
                "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"refresh\""
                        + " content=\"0;url=/examples/hello_world/\"><title>Hello World</title></head><body><a"
                        + " href=\"/examples/hello_world/\">Hello World</a></body></html>");
        write(output, ".nojekyll", "");
        SiteLinks.check(output);
    }

    static void copyAsset(Path source, Path destination) throws IOException {
        if (Files.isDirectory(source)) {
            var root = source.toRealPath();
            try (var paths = Files.walk(root)) {
                for (var file : paths.filter(Files::isRegularFile).sorted().toList()) {
                    var target = destination.resolve(root.relativize(file));
                    Files.createDirectories(Objects.requireNonNull(target.getParent()));
                    Files.copy(file, target);
                }
            }
        } else {
            Files.createDirectories(Objects.requireNonNull(destination.getParent()));
            Files.copy(source, destination);
        }
    }

    private static Map<String, String> readSources(final List<Path> jars) throws IOException {
        final var sources = new TreeMap<String, String>();
        for (final var path : jars) {
            try (var jar = new JarFile(path.toFile())) {
                for (final var entry : Collections.list(jar.entries())) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".java")) {
                        final String source;
                        try (var stream = jar.getInputStream(entry)) {
                            source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                        }
                        final var previous = sources.putIfAbsent(entry.getName(), source);
                        if (previous != null && !previous.equals(source)) {
                            throw new IOException("Conflicting example source: " + entry.getName());
                        }
                    }
                }
            }
        }
        return sources;
    }

    static Page readPage(final String id, final String text) {
        if (!text.startsWith("---\n")) {
            throw new IllegalArgumentException("Missing frontmatter in " + id);
        }
        final var end = text.indexOf("\n---\n", 4);
        if (end < 0) {
            throw new IllegalArgumentException("Unclosed frontmatter in " + id);
        }
        final var metadata = new LinkedHashMap<String, String>();
        for (final var line : text.substring(4, end).split("\n")) {
            final var parts = line.split(":", 2);
            if (parts.length != 2 || !List.of("title", "sidebar_label").contains(parts[0])) {
                throw new IllegalArgumentException("Unknown frontmatter in " + id + ": " + line);
            }
            metadata.put(parts[0], parts[1].strip());
        }
        final var title = metadata.get("title");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Missing title in " + id);
        }
        return new Page(id, title, metadata.getOrDefault("sidebar_label", title), text.substring(end + 5));
    }

    private static Map<String, String> readNavigation(final Path path, final Map<String, Page> pages)
            throws IOException {
        final var navigation = new LinkedHashMap<String, String>();
        String category = "";
        for (final var line : Files.readAllLines(path)) {
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                category = line.substring(1, line.length() - 1);
            } else if (category.isBlank()
                    || !pages.containsKey(line)
                    || navigation.putIfAbsent(line, category) != null) {
                throw new IOException("Invalid navigation entry: " + line);
            }
        }
        if (!navigation.keySet().equals(pages.keySet())) {
            throw new IOException("Navigation must list every document exactly once");
        }
        return navigation;
    }

    private static String sidebar(
            final Map<String, String> navigation, final Map<String, Page> pages, final String current) {
        final var html = new StringBuilder();
        String category = "";
        for (final var entry : navigation.entrySet()) {
            if (!category.equals(entry.getValue())) {
                if (!category.isEmpty()) {
                    html.append("</ul></section>\n");
                }
                category = entry.getValue();
                html.append("<section><h2>").append(Markdown.escape(category)).append("</h2><ul>\n");
            }
            html.append("<li><a href=\"/docs/").append(entry.getKey()).append(".html\"");
            if (entry.getKey().equals(current)) {
                html.append(" aria-current=\"page\"");
            }
            html.append(">")
                    .append(Markdown.escape(
                            Objects.requireNonNull(pages.get(entry.getKey())).label()))
                    .append("</a></li>\n");
        }
        return html.append("</ul></section>\n").toString();
    }

    private static String pageLink(final Page page, final String direction) {
        return "<a class=\"" + direction.toLowerCase(Locale.ROOT) + "\" href=\"/docs/" + page.id() + ".html\"><span>"
                + direction + "</span>" + Markdown.escape(page.title()) + "</a>";
    }

    static String template(final String text, final Map<String, String> values) {
        final var matcher = SLOT.matcher(text);
        final var rendered = new StringBuilder();
        while (matcher.find()) {
            final var value = values.get(matcher.group(1));
            if (value == null) {
                throw new IllegalArgumentException("Unknown template slot: " + matcher.group(1));
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }

    private static void extractApi(final Path archive, final Path output) throws IOException {
        try (var jar = new JarFile(archive.toFile())) {
            for (final var entry : Collections.list(jar.entries())) {
                if (!entry.isDirectory() && !entry.getName().startsWith("META-INF/")) {
                    final var target = destination(output, "api/" + entry.getName());
                    Files.createDirectories(target.getParent());
                    try (var stream = jar.getInputStream(entry)) {
                        if (entry.getName().equals("stylesheet.css")) {
                            // The JDK stylesheet imports fonts absent from the API archive.
                            final var css = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                            Files.writeString(target, css.replace("@import url('resources/fonts/dejavu.css');", ""));
                        } else {
                            Files.copy(stream, target);
                        }
                    }
                }
            }
        }
    }

    static Path destination(final Path output, final String name) throws IOException {
        final var target = output.resolve(name).normalize();
        if (!target.startsWith(output)) {
            throw new IOException("Site path escapes output directory: " + name);
        }
        return target;
    }

    private static void write(final Path output, final String name, final String content) throws IOException {
        final var path = destination(output, name);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    record Page(String id, String title, String label, String body) {}
}
