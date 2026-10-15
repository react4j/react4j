package react4j.generation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jspecify.annotations.NullMarked;

/** Generates the DOM factory source archive consumed by the DOM library. */
@NullMarked
public final class DomGenerator {
    private record Tag(String name, String props, boolean children) {}

    private static final List<Tag> TAGS = List.of(
            new Tag("a", "AnchorProps", true),
            new Tag("article", "HtmlProps", true),
            new Tag("audio", "AudioProps", true),
            new Tag("br", "HtmlProps", false),
            new Tag("button", "BtnProps", true),
            new Tag("canvas", "HtmlProps", true),
            new Tag("caption", "HtmlProps", true),
            new Tag("col", "ColProps", false),
            new Tag("colgroup", "HtmlProps", true),
            new Tag("div", "HtmlProps", true),
            new Tag("footer", "HtmlProps", true),
            new Tag("form", "FormProps", true),
            new Tag("header", "HtmlProps", true),
            new Tag("h1", "HtmlProps", true),
            new Tag("h2", "HtmlProps", true),
            new Tag("h3", "HtmlProps", true),
            new Tag("h4", "HtmlProps", true),
            new Tag("h5", "HtmlProps", true),
            new Tag("h6", "HtmlProps", true),
            new Tag("i", "HtmlProps", true),
            new Tag("iframe", "IFrameProps", true),
            new Tag("img", "ImgProps", false),
            new Tag("input", "InputProps", false),
            new Tag("label", "LabelProps", true),
            new Tag("li", "HtmlProps", true),
            new Tag("ol", "HtmlProps", true),
            new Tag("option", "OptionProps", true),
            new Tag("optgroup", "OptGroupProps", true),
            new Tag("p", "HtmlProps", true),
            new Tag("span", "HtmlProps", true),
            new Tag("select", "SelectProps", true),
            new Tag("section", "HtmlProps", true),
            new Tag("strong", "HtmlProps", true),
            new Tag("source", "SourceProps", false),
            new Tag("table", "HtmlProps", true),
            new Tag("textarea", "TextAreaProps", false),
            new Tag("tbody", "HtmlProps", true),
            new Tag("td", "TdProps", true),
            new Tag("th", "ThProps", true),
            new Tag("thead", "HtmlProps", true),
            new Tag("tr", "HtmlProps", true),
            new Tag("ul", "HtmlProps", true));

    private DomGenerator() {}

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected output source archive");
        }
        var source = new StringBuilder(template("header"));
        var propTypes = new TreeSet<String>();
        for (var tag : TAGS) {
            propTypes.add(tag.props());
        }
        for (var props : propTypes) {
            source.append("import react4j.dom.proptypes.html.").append(props).append(";\n");
        }
        source.append(template("common"));
        var element = template("element");
        var children = template("children");
        for (var tag : TAGS) {
            source.append(expand(element, tag));
            if (tag.children()) {
                source.append(expand(children, tag));
            }
        }
        source.append(template("footer"));
        try (var zip = new ZipOutputStream(Files.newOutputStream(Path.of(args[0])))) {
            var entry = new ZipEntry("react4j/dom/DOM.java");
            entry.setTimeLocal(LocalDateTime.of(2010, 1, 1, 0, 0));
            zip.putNextEntry(entry);
            zip.write(source.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    private static String expand(String template, Tag tag) {
        return template.replace("{tag}", tag.name()).replace("{props}", tag.props());
    }

    private static String template(String name) throws IOException {
        try (var input = DomGenerator.class.getResourceAsStream("dom-" + name + ".txt")) {
            if (input == null) {
                throw new IOException("Missing DOM template: " + name);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
