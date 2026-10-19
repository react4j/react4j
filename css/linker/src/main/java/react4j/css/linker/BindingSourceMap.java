package react4j.css.linker;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.SourceMapInput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Loads a binding's actual companion map and fills missing Java content from its declared tree. */
record BindingSourceMap(SourceMapConsumerV3 mapping, SourceMapInput input) {
    static BindingSourceMap read(Path source, Path tree) {
        // Preserve the input path form so Closure and Bazel apply the same source-location mappings.
        var path = source.resolveSibling(source.getFileName().toString().replace(".impl.java.js", ".js.map"));
        try {
            var json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            var sources = json.getAsJsonArray("sources");
            var supplied =
                    json.has("sourcesContent") && !json.get("sourcesContent").isJsonNull()
                            ? json.getAsJsonArray("sourcesContent")
                            : null;
            if (null != supplied && supplied.size() != sources.size()) {
                throw new IllegalArgumentException("Generated source-map content must match its source entries");
            }
            String root = json.has("sourceRoot") ? json.get("sourceRoot").getAsString() : "";
            var contents = new JsonArray();
            for (int i = 0; i < sources.size(); i++) {
                var name = sources.get(i);
                if (!name.isJsonPrimitive() || !name.getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException("Generated source-map sources must be strings");
                }
                var content = null == supplied ? null : supplied.get(i);
                if (null != content && !content.isJsonNull()) {
                    if (!content.isJsonPrimitive()
                            || !content.getAsJsonPrimitive().isString()) {
                        throw new IllegalArgumentException("Generated source-map content must be a string or null");
                    }
                    contents.add(content);
                } else {
                    var java = Objects.requireNonNull(path.toAbsolutePath().getParent())
                            .resolve(root)
                            .resolve(name.getAsString())
                            .normalize();
                    if (!java.startsWith(tree.toAbsolutePath().normalize())
                            || !java.toString().endsWith(".java")) {
                        throw new IllegalArgumentException(
                                "Generated Java source is outside its J2CL tree: " + name.getAsString());
                    }
                    contents.add(Files.readString(java));
                }
            }
            json.add("sourcesContent", contents);
            String enriched = json.toString();
            var mapping = new SourceMapConsumerV3();
            mapping.parse(enriched);
            return new BindingSourceMap(mapping, new SourceMapInput(SourceFile.fromCode(path.toString(), enriched)));
        } catch (IOException | SourceMapParseException | RuntimeException e) {
            throw new IllegalArgumentException("Invalid generated CSS binding source map: " + source, e);
        }
    }
}
