package react4j.site;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SiteLinks {
    private static final Pattern CSS_URL = Pattern.compile(
            "(?i)url\\(\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s)]*))\\s*\\)|@import\\s+[\"']([^\"']+)[\"']");

    private SiteLinks() {}

    static void check(final Path directory) throws IOException {
        final var root = directory.toRealPath();
        final var anchors = new LinkedHashMap<Path, Set<String>>();
        final var references = new LinkedHashMap<Path, List<Link>>();
        try (var files = Files.walk(root)) {
            for (final var file : files.filter(Files::isRegularFile).sorted().toList()) {
                if (file.toString().endsWith(".html")) {
                    final var page = new Page();
                    try (var reader = Files.newBufferedReader(file)) {
                        new ParserDelegator().parse(reader, page, true);
                    }
                    anchors.put(file, page.anchors);
                    references.put(file, page.links);
                } else if (file.toString().endsWith(".css")) {
                    final var links = new ArrayList<Link>();
                    final var matcher = CSS_URL.matcher(Files.readString(file));
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            final var url = matcher.group(i);
                            if (url != null && !url.isEmpty() && !url.startsWith("data:")) {
                                links.add(new Link(url, true));
                            }
                        }
                    }
                    references.put(file, links);
                }
            }
        }
        if (anchors.isEmpty()) {
            throw new IOException("No HTML pages in " + root);
        }
        final var broken = new ArrayList<String>();
        int count = 0;
        for (final var entry : references.entrySet()) {
            for (final var link : entry.getValue()) {
                final var uri = URI.create(link.url());
                if (uri.isAbsolute() || uri.getRawAuthority() != null) {
                    if (link.asset()) {
                        broken.add(root.relativize(entry.getKey()) + ": external asset " + link.url());
                    }
                    continue;
                }
                final var path = uri.getPath();
                var target = path.isEmpty()
                        ? entry.getKey()
                        : path.startsWith("/")
                                ? root.resolve(path.substring(1)).normalize()
                                : Objects.requireNonNull(entry.getKey().getParent())
                                        .resolve(path)
                                        .normalize();
                if (!link.asset()
                        && target.startsWith(root)
                        && SiteDeploy.EXTERNAL_PATHS.contains(
                                root.relativize(target).getName(0).toString())) {
                    continue;
                }
                if (Files.isDirectory(target)) {
                    target = target.resolve("index.html");
                } else if (!Files.exists(target)
                        && !target.getFileName().toString().contains(".")) {
                    target = target.resolveSibling(target.getFileName() + ".html");
                }
                count++;
                final var fragment = uri.getFragment();
                if (!target.startsWith(root)
                        || !Files.isRegularFile(target)
                        || (fragment != null
                                && !fragment.isEmpty()
                                && anchors.containsKey(target)
                                && !anchors.get(target).contains(fragment))) {
                    broken.add(root.relativize(entry.getKey()) + ": " + link.url());
                }
            }
        }
        if (!broken.isEmpty()) {
            throw new IOException("Broken site links:\n" + String.join("\n", broken));
        }
        System.out.println("Checked " + count + " local file and fragment links; 0 broken");
    }

    private record Link(String url, boolean asset) {}

    private static final class Page extends HTMLEditorKit.ParserCallback {
        private final Set<String> anchors = new HashSet<>();
        private final List<Link> links = new ArrayList<>();

        @Override
        public void handleStartTag(final HTML.Tag tag, final MutableAttributeSet attributes, final int position) {
            read(tag, attributes);
        }

        @Override
        public void handleSimpleTag(final HTML.Tag tag, final MutableAttributeSet attributes, final int position) {
            read(tag, attributes);
        }

        private void read(final HTML.Tag tag, final MutableAttributeSet attributes) {
            final var id = attributes.getAttribute(HTML.Attribute.ID);
            if (id != null) {
                anchors.add(id.toString());
            }
            if (tag == HTML.Tag.A) {
                final var name = attributes.getAttribute(HTML.Attribute.NAME);
                if (name != null) {
                    anchors.add(name.toString());
                }
            }
            for (final var attribute : new HTML.Attribute[] {HTML.Attribute.HREF, HTML.Attribute.SRC}) {
                final var value = attributes.getAttribute(attribute);
                if (value != null && !value.toString().isEmpty()) {
                    links.add(new Link(value.toString(), attribute == HTML.Attribute.SRC || tag == HTML.Tag.LINK));
                }
            }
        }
    }
}
