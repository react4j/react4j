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
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssLink;

/** Independently prepared content identity, with stable Bazel metadata paths. */
record AssetContent(String logicalPath, Path source, String sha256, long size) {
    AssetContent {
        AssetResource.validatePath(logicalPath);
        if (source.toString().isEmpty() || !sha256.matches("[0-9a-f]{64}") || size < 0) {
            throw new IllegalArgumentException("Invalid prepared asset: " + logicalPath);
        }
    }

    public static void main(String[] args) throws IOException {
        if (3 != args.length) {
            throw new IllegalArgumentException("Expected logical path, source and metadata output");
        }
        Files.writeString(Path.of(args[2]), prepare(args[0], Path.of(args[1])).json() + "\n");
    }

    static AssetContent prepare(String logicalPath, Path source) throws IOException {
        byte[] bytes = Files.readAllBytes(source);
        return new AssetContent(logicalPath, source, CssLink.digest(bytes), bytes.length);
    }

    JsonObject json() {
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("logicalPath", logicalPath);
        json.addProperty("source", source.toString().replace('\\', '/'));
        json.addProperty("sha256", sha256);
        json.addProperty("size", size);
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
                    size(json, "size"));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid prepared asset metadata", e);
        }
    }

    static AssetContent managed(JsonObject entry) {
        String url = string(entry, "url");
        var json = entry.deepCopy();
        json.addProperty("version", 1);
        var content = read(json);
        if (!url.equals(content.url())
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
        entry.addProperty("url", url);
        entry.addProperty("mediaType", AssetMedia.forPath(logicalPath).type());
        var getters = new JsonArray();
        accessors.forEach(getters::add);
        entry.add("accessors", getters);
        var retained = new JsonArray();
        reasons.forEach(retained::add);
        entry.add("retainedBy", retained);
        return entry;
    }

    void copy(Path directory, String url) throws IOException {
        AssetResource.validatePath(url);
        if (Files.size(source) != size || !CssLink.digest(source).equals(sha256)) {
            throw new IllegalArgumentException("Prepared asset differs from its source bytes: " + logicalPath);
        }
        copy(source, directory.resolve(url));
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
