package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssLink;

/** Independently prepared bytes and representations, with stable Bazel output paths. */
record AssetContent(
        String logicalPath,
        Path source,
        String sha256,
        long size,
        @Nullable Path compressed,
        long compressedSize) {
    AssetContent {
        AssetResource.validatePath(logicalPath);
        if (source.toString().isEmpty()
                || !sha256.matches("[0-9a-f]{64}")
                || size < 0
                || (null == compressed
                        ? 0 != compressedSize
                        : compressed.toString().isEmpty() || compressedSize <= 0 || compressedSize >= size)) {
            throw new IllegalArgumentException("Invalid prepared asset: " + logicalPath);
        }
    }

    public static void main(String[] args) throws IOException {
        if (4 != args.length) {
            throw new IllegalArgumentException(
                    "Expected logical path, source, Brotli representation and metadata output");
        }
        Files.writeString(
                Path.of(args[3]),
                prepare(args[0], Path.of(args[1]), Path.of(args[2])).json() + "\n");
    }

    static AssetContent prepare(String logicalPath, Path source, @Nullable Path compressed) throws IOException {
        byte[] bytes = Files.readAllBytes(source);
        long length = null == compressed ? 0 : Files.size(compressed);
        boolean useful = length > 0 && length < bytes.length;
        return new AssetContent(
                logicalPath,
                source,
                CssLink.digest(bytes),
                bytes.length,
                useful ? compressed : null,
                useful ? length : 0);
    }

    JsonObject json() {
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("logicalPath", logicalPath);
        json.addProperty("source", source.toString().replace('\\', '/'));
        json.addProperty("sha256", sha256);
        json.addProperty("size", size);
        if (null != compressed) {
            json.addProperty("brotli", compressed.toString().replace('\\', '/'));
            json.addProperty("brotliSize", compressedSize);
        }
        return json;
    }

    static AssetContent read(Path file) throws IOException {
        try {
            return read(JsonParser.parseString(Files.readString(file)).getAsJsonObject());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid prepared asset metadata: " + file, e);
        }
    }

    static AssetContent read(JsonObject json) {
        try {
            if (!"1".equals(json.get("version").toString())) {
                throw new IllegalArgumentException("Unsupported prepared asset version");
            }
            return new AssetContent(
                    string(json, "logicalPath"),
                    Path.of(string(json, "source")),
                    string(json, "sha256"),
                    size(json, "size"),
                    json.has("brotli") ? Path.of(string(json, "brotli")) : null,
                    json.has("brotliSize") ? size(json, "brotliSize") : 0);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid prepared asset metadata", e);
        }
    }

    static AssetContent managed(JsonObject entry) {
        var representations = entry.getAsJsonObject("representations");
        boolean compressed = representations.has("br");
        if (!representations.keySet().equals(compressed ? Set.of("identity", "br") : Set.of("identity"))) {
            throw new IllegalArgumentException("Unsupported asset representation");
        }
        String url = string(entry, "url");
        var json = entry.deepCopy();
        json.addProperty("version", 1);
        if (compressed) {
            if (!(url + ".br").equals(string(representations, "br"))) {
                throw new IllegalArgumentException("Brotli representation must accompany its canonical asset URL");
            }
            json.add("brotli", representations.get("br"));
        }
        var content = read(json);
        if (!url.equals(string(representations, "identity"))
                || !url.equals(content.url())
                || !AssetMedia.forPath(content.logicalPath()).type().equals(string(entry, "mediaType"))) {
            throw new IllegalArgumentException("Linked asset URL or media type differs from its content identity");
        }
        return content;
    }

    private static String string(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Prepared asset requires string " + key);
        }
        return value.getAsString();
    }

    private static long size(JsonObject json, String key) {
        var value = json.get(key);
        if (null == value || !value.toString().matches("0|[1-9][0-9]*")) {
            throw new IllegalArgumentException("Prepared asset requires nonnegative integer " + key);
        }
        return Long.parseLong(value.getAsString());
    }

    String url() {
        return "assets/"
                + Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(HexFormat.of().parseHex(sha256)) + "."
                + AssetMedia.forPath(logicalPath).extension();
    }

    JsonObject entry(String url, List<String> accessors, List<String> reasons) {
        var entry = json();
        entry.remove("version");
        entry.remove("brotli");
        entry.addProperty("url", url);
        entry.addProperty("mediaType", AssetMedia.forPath(logicalPath).type());
        var getters = new JsonArray();
        accessors.forEach(getters::add);
        entry.add("accessors", getters);
        var retained = new JsonArray();
        reasons.forEach(retained::add);
        entry.add("retainedBy", retained);
        var representations = new JsonObject();
        representations.addProperty("identity", url);
        if (null != compressed) {
            representations.addProperty("br", url + ".br");
        }
        entry.add("representations", representations);
        return entry;
    }

    void copy(Path directory, String url) throws IOException {
        AssetResource.validatePath(url);
        if (Files.size(source) != size || !CssLink.digest(source).equals(sha256)) {
            throw new IllegalArgumentException("Prepared asset differs from its source bytes: " + logicalPath);
        }
        copy(source, directory.resolve(url));
        if (null != compressed) {
            if (Files.size(compressed) != compressedSize) {
                throw new IllegalArgumentException(
                        "Prepared Brotli representation differs from its metadata: " + logicalPath);
            }
            copy(compressed, directory.resolve(url + ".br"));
        }
    }

    private static void copy(Path source, Path destination) throws IOException {
        Files.createDirectories(Objects.requireNonNull(destination.getParent()));
        if (Files.exists(destination)) {
            if (-1 != Files.mismatch(source, destination)) {
                throw new IllegalArgumentException("Conflicting packaged asset: " + destination);
            }
        } else {
            Files.copy(source, destination);
        }
    }
}
