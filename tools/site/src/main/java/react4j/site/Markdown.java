package react4j.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.SourceSpan;
import org.commonmark.parser.IncludeSourceSpans;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class Markdown {
    private static final Pattern DIRECTIVE =
            Pattern.compile("\\{@(link|file_content):\\s*((?:\"[^\"]*\"|'[^']*'|[^}])+)}");
    private static final Pattern PARAMETER = Pattern.compile("\"([^\"]*)\"|'([^']*)'|([^\\s]+)");
    private static final Parser PARSER = Parser.builder()
            .extensions(List.of(TablesExtension.create()))
            .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
            .build();

    private Markdown() {}

    static String render(final String markdown, final Map<String, String> sources) {
        final var code = new ArrayList<SourceSpan>();
        PARSER.parse(markdown).accept(new AbstractVisitor() {
            @Override
            protected void visitChildren(final Node node) {
                if (node instanceof Code || node instanceof FencedCodeBlock || node instanceof IndentedCodeBlock) {
                    code.addAll(node.getSourceSpans());
                } else {
                    super.visitChildren(node);
                }
            }
        });
        final var matcher = DIRECTIVE.matcher(markdown);
        final var expanded = new StringBuilder();
        while (matcher.find()) {
            if (code.stream()
                    .anyMatch(span -> matcher.start() >= span.getInputIndex()
                            && matcher.start() < span.getInputIndex() + span.getLength())) {
                continue;
            }
            final var html =
                    switch (matcher.group(1)) {
                        case "link" -> apiLink(matcher.group(2));
                        default ->
                            "<pre><code class=\"language-java\">" + escape(snippet(matcher.group(2), sources))
                                    + "</code></pre>";
                    };
            matcher.appendReplacement(expanded, Matcher.quoteReplacement(html));
        }
        matcher.appendTail(expanded);
        final var text = expanded.toString();
        return HtmlRenderer.builder()
                .extensions(List.of(TablesExtension.create()))
                .attributeProviderFactory(context -> (node, tag, attributes) -> {
                    if (node instanceof Link link) {
                        final var url = link.getDestination();
                        if (!url.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*") && !url.startsWith("//")) {
                            attributes.put("href", url.replaceFirst("\\.md(?=[?#]|$)", ".html"));
                        }
                    }
                })
                .nodeRendererFactory(context -> new Headings(context, text))
                .build()
                .render(PARSER.parse(text));
    }

    private static String apiLink(final String specification) {
        final var parts = specification.strip().split("\\s+", 2);
        final var target = parts[0].split("#", 2);
        final var names = target[0].split("\\.");
        int classIndex = 0;
        while (classIndex < names.length && !Character.isUpperCase(names[classIndex].charAt(0))) {
            classIndex++;
        }
        if (classIndex == names.length) {
            throw new IllegalArgumentException("Expected a Java class: " + specification);
        }
        final var className = String.join(".", Arrays.copyOfRange(names, classIndex, names.length));
        final var member = target.length == 2 ? target[1] : "";
        final var label = parts.length == 2 ? parts[1] : className + (member.isEmpty() ? "" : "." + member);
        final var url = "/api/" + String.join("/", Arrays.copyOfRange(names, 0, classIndex)) + "/" + className + ".html"
                + (member.isEmpty() ? "" : "#" + member);
        return "<a href=\"" + escape(url) + "\"><code>" + escape(label) + "</code></a>";
    }

    static String snippet(final String parameters, final Map<String, String> sources) {
        final var args = new LinkedHashMap<String, String>();
        final var matcher = PARAMETER.matcher(parameters);
        while (matcher.find()) {
            final var parameter = matcher.group(1) != null
                    ? matcher.group(1)
                    : matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            final var separator = parameter.indexOf('=');
            if (separator < 1) {
                throw new IllegalArgumentException("Expected snippet key=value: " + parameter);
            }
            final var name = parameter.substring(0, separator);
            if (!Set.of(
                            "file",
                            "path",
                            "start_line",
                            "end_line",
                            "include_start_line",
                            "include_end_line",
                            "strip_block")
                    .contains(name)) {
                throw new IllegalArgumentException("Unknown snippet parameter: " + name);
            }
            args.put(name, parameter.substring(separator + 1));
        }
        if (args.containsKey("path") && !args.get("path").equals("generated/processors/main/java")) {
            throw new IllegalArgumentException("Unknown snippet path: " + args.get("path"));
        }
        final var file = args.get("file");
        final var source = sources.get(file);
        if (source == null) {
            throw new IllegalArgumentException("Missing snippet source: " + file);
        }
        final var lines = Arrays.asList(source.split("\\n", -1));
        final var start =
                boundary(lines, 0, args.get("start_line"), 0) + (included(args, "include_start_line") ? 0 : 1);
        final var end = boundary(lines, start, args.get("end_line"), lines.size() - 1)
                + (args.containsKey("end_line") && included(args, "include_end_line") ? 1 : 0);
        final var selected = new ArrayList<String>();
        int indent = Integer.MAX_VALUE;
        int lastIndent = 0;
        boolean eliding = false;
        for (final var line : lines.subList(start, end)) {
            if (!eliding && line.contains("DOC ELIDE START")) {
                eliding = true;
                selected.add(" ".repeat(lastIndent) + "...");
            } else if (eliding && line.contains("DOC ELIDE END")) {
                eliding = false;
            } else if (!eliding) {
                if (!line.isBlank()) {
                    lastIndent = line.length() - line.stripLeading().length();
                    indent = Math.min(indent, lastIndent);
                }
                selected.add(line);
            }
        }
        if (eliding) {
            throw new IllegalArgumentException("Unclosed DOC ELIDE START in " + file);
        }
        final var commonIndent = !included(args, "strip_block") || indent == Integer.MAX_VALUE ? 0 : indent;
        return String.join(
                "\n",
                selected.stream()
                        .map(line -> line.substring(Math.min(commonIndent, line.length())))
                        .toList());
    }

    private static boolean included(final Map<String, String> args, final String key) {
        final var value = args.getOrDefault(key, "true");
        if (!value.equals("true") && !value.equals("false")) {
            throw new IllegalArgumentException("Expected true or false for " + key);
        }
        return value.equals("true");
    }

    private static int boundary(
            final List<String> lines, final int start, final @Nullable String regex, final int defaultLine) {
        if (regex == null) {
            return defaultLine;
        }
        final var pattern = Pattern.compile(regex);
        for (int i = start; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).find()) {
                return i;
            }
        }
        throw new IllegalArgumentException("Snippet boundary not found: " + regex);
    }

    static String slug(final String heading) {
        final var accents = "àáäâèéëêìíïîòóöôùúüûñç";
        final var replacements = "aaaaeeeeiiiioooouuuunc";
        final var normalized = new StringBuilder();
        for (final var character : heading.toLowerCase(Locale.ROOT).toCharArray()) {
            final var index = accents.indexOf(character);
            normalized.append(index < 0 ? character : replacements.charAt(index));
        }
        // Preserve published fragments derived from raw Markdown headings.
        return normalized
                        .toString()
                        .replaceAll("[.(?'/]", "")
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-|-$", "")
                + (Pattern.compile("\\s\\.+").matcher(heading).find() ? "-" : "");
    }

    static String escape(final String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record Headings(HtmlNodeRendererContext context, String source) implements NodeRenderer {
        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(Heading.class);
        }

        @Override
        public void render(final Node node) {
            final var heading = (Heading) node;
            final var span = heading.getSourceSpans().get(0);
            final var raw = source.substring(span.getInputIndex(), span.getInputIndex() + span.getLength())
                    .replaceFirst("^ {0,3}#{1,6}\\s+", "")
                    .replaceFirst("\\s+#+\\s*$", "");
            final var id = slug(raw);
            final var html = context.getWriter();
            final var tag = "h" + heading.getLevel();
            html.line();
            html.tag(tag, Map.of("id", id));
            for (var child = node.getFirstChild(); child != null; child = child.getNext()) {
                context.render(child);
            }
            final var attributes = new LinkedHashMap<String, String>();
            attributes.put("class", "heading-link");
            attributes.put("href", "#" + id);
            attributes.put("aria-label", "Link to this section");
            html.tag("a", attributes);
            html.raw("<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" aria-hidden=\"true\"><path d=\"M6 10l4-4M6"
                    + " 5l2-2a3 3 0 0 1 4 4l-2 2M10 11l-2 2a3 3 0 0 1-4-4l2-2\" fill=\"none\""
                    + " stroke=\"currentColor\" stroke-width=\"1.5\"/></svg>");
            html.tag("/a");
            html.tag("/" + tag);
            html.line();
        }
    }
}
