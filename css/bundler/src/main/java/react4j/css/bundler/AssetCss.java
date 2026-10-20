package react4j.css.bundler;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssUrls;
import react4j.css.internal.MappedText;
import react4j.css.internal.SourceMap;

/** Finalizes each independently pruned CSS variant against the application's shared asset inventory. */
record AssetCss(Path input, Path map, Path output, Path outputMap, String name) {
    static AssetCss read(String value) {
        var json = JsonParser.parseString(value).getAsJsonObject();
        String name = json.get("name").getAsString();
        AssetResource.validatePath(name);
        return new AssetCss(
                Path.of(json.get("input").getAsString()),
                Path.of(json.get("map").getAsString()),
                Path.of(json.get("output").getAsString()),
                Path.of(json.get("outputMap").getAsString()),
                name);
    }

    Set<String> references() throws IOException {
        var references = new TreeSet<String>();
        CssUrls.edits(
                Files.readString(input),
                value -> CssUrls.checked(value, (path, suffix) -> {
                    AssetResource.validatePath(path);
                    references.add(path);
                    return value;
                }));
        return Set.copyOf(references);
    }

    void write(Map<String, String> urls) throws IOException {
        String css = Files.readString(input);
        var edits = new ArrayList<>(CssUrls.edits(
                css,
                value -> CssUrls.checked(value, (path, suffix) -> {
                    String url = Objects.requireNonNull(urls.get(path), path);
                    var parent = Path.of(name).getParent();
                    return (null == parent ? Path.of(url) : parent.relativize(Path.of(url)))
                                    .toString()
                                    .replace('\\', '/')
                            + suffix;
                })));
        int reference = css.lastIndexOf("/*# sourceMappingURL=");
        if (reference >= 0 && css.substring(reference).matches("(?s)/\\*# sourceMappingURL=[^*]*\\*/\\s*")) {
            edits.add(new SourceMap.Edit(reference, css.length(), ""));
        }
        var result = new MappedText(
                        css, JsonParser.parseString(Files.readString(map)).getAsJsonObject())
                .replace(edits);
        String filename = Path.of(name).getFileName().toString();
        result.map().addProperty("file", filename);
        Files.writeString(output, result.text() + "/*# sourceMappingURL=" + filename + ".map */\n");
        Files.writeString(outputMap, result.map() + "\n");
    }
}
