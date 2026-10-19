package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssLink;

/** Finalizes linked asset URLs and writes a deterministic application asset inventory. */
public final class AssetBundle {
    private AssetBundle() {}

    public static void main(String[] args) throws IOException {
        run(
                1 == args.length && args[0].startsWith("@")
                        ? Files.readAllLines(Path.of(args[0].substring(1)))
                        : Arrays.asList(args));
    }

    static void run(List<String> args) throws IOException {
        var trees = new ArrayList<Path>();
        var manifests = new ArrayList<Path>();
        var options = new TreeMap<String, String>();
        var required = Set.of(
                "--directory",
                "--manifest",
                "--javascript",
                "--map",
                "--link-config",
                "--liveness",
                "--link",
                "--output-js",
                "--output-map");
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing asset bundle option value");
            }
            String option = args.get(i);
            String value = args.get(i + 1);
            if ("--input".equals(option)) {
                trees.add(Path.of(value));
            } else if ("--asset-catalogue".equals(option)) {
                manifests.add(Path.of(value));
            } else if (!required.contains(option) || null != options.putIfAbsent(option, value)) {
                throw new IllegalArgumentException("Unknown or repeated asset bundle option: " + option);
            }
        }
        if (!options.keySet().equals(required)) {
            throw new IllegalArgumentException(
                    "Expected asset bundle directory, manifest, JavaScript, map and link evidence");
        }
        var link = CssLink.read(Files.readString(Path.of(options.get("--link-config"))));
        var catalogue = CssCatalogue.read(trees);
        if (!link.equals(CssLink.create(
                catalogue,
                Objects.requireNonNull(options.get("--link")),
                link.optimized(),
                link.names().policy()))) {
            throw new IllegalArgumentException("Asset bundle link configuration differs from its catalogue");
        }
        var files = AssetResource.catalogue(manifests);
        AssetResource.validate(catalogue.assets(), files);
        var javascript = Path.of(options.get("--javascript"));
        var live = link.readReport(Files.readString(Path.of(options.get("--liveness"))), CssLink.digest(javascript));
        var directory = Path.of(options.get("--directory"));
        Files.createDirectories(directory);
        try (var contents = Files.list(directory)) {
            if (contents.findAny().isPresent()) {
                throw new IllegalArgumentException("Asset bundle output directory must be empty");
            }
        }
        var retained = new TreeMap<String, Set<String>>();
        for (var asset : link.assets()) {
            if (!link.optimized() || live.contains(asset.id())) {
                retained.computeIfAbsent(asset.binding().path(), ignored -> new TreeSet<>())
                        .add(asset.id());
            }
        }
        var urls = new TreeMap<String, String>();
        var entries = new JsonArray();
        for (var entry : retained.entrySet()) {
            String logical = entry.getKey();
            var source = Path.of(Objects.requireNonNull(files.get(logical)));
            byte[] bytes = Files.readAllBytes(source);
            String digest = CssLink.digest(bytes);
            var media = media(logical);
            String url = "assets/"
                    + Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(HexFormat.of().parseHex(digest)) + "." + media.extension();
            var output = directory.resolve(url);
            Files.createDirectories(Objects.requireNonNull(output.getParent()));
            if (Files.exists(output)) {
                if (!Arrays.equals(bytes, Files.readAllBytes(output))) {
                    throw new IllegalArgumentException("Conflicting content-addressed asset: " + url);
                }
            } else {
                Files.write(output, bytes);
            }
            var item = new JsonObject();
            item.addProperty("logicalPath", logical);
            item.addProperty("source", source.toString().replace('\\', '/'));
            item.addProperty("url", url);
            item.addProperty("mediaType", media.type());
            item.addProperty("sha256", digest);
            item.addProperty("size", bytes.length);
            var getters = new JsonArray();
            var reasons = new JsonArray();
            for (var accessor : entry.getValue()) {
                getters.add(accessor);
                reasons.add("java:" + accessor);
            }
            item.add("accessors", getters);
            item.add("retainedBy", reasons);
            var representations = new JsonObject();
            representations.addProperty("identity", url);
            item.add("representations", representations);
            entries.add(item);
            for (var asset : link.assets()) {
                if (asset.binding().path().equals(logical) && entry.getValue().contains(asset.id())) {
                    urls.put(link.optimized() ? asset.token() : asset.binding().value(), url);
                }
            }
        }
        String code = Files.readString(javascript);
        var mapped = AssetJavaScript.rewrite(
                code, AssetJavaScript.mapping(code, Path.of(options.get("--map")), link, trees), link, urls);
        Files.writeString(Path.of(options.get("--output-js")), mapped.text());
        Files.writeString(Path.of(options.get("--output-map")), mapped.map() + "\n");
        var inventory = new JsonObject();
        inventory.addProperty("version", 1);
        inventory.add("assets", entries);
        var pruned = new JsonArray();
        for (var logical : files.keySet()) {
            if (!retained.containsKey(logical)) {
                pruned.add(logical);
            }
        }
        inventory.add("pruned", pruned);
        Files.writeString(Path.of(options.get("--manifest")), inventory + "\n");
    }

    record Media(String extension, String type) {}

    static Media media(String logical) {
        String extension = logical.substring(logical.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (extension) {
            case "svg" -> new Media("svg", "image/svg+xml");
            case "png" -> new Media("png", "image/png");
            case "jpg", "jpeg" -> new Media("jpg", "image/jpeg");
            case "gif" -> new Media("gif", "image/gif");
            case "webp" -> new Media("webp", "image/webp");
            case "avif" -> new Media("avif", "image/avif");
            case "ico" -> new Media("ico", "image/x-icon");
            case "woff" -> new Media("woff", "font/woff");
            case "woff2" -> new Media("woff2", "font/woff2");
            case "ttf" -> new Media("ttf", "font/ttf");
            case "otf" -> new Media("otf", "font/otf");
            case "css" -> new Media("css", "text/css");
            case "js" -> new Media("js", "text/javascript");
            case "json", "map" -> new Media("json", "application/json");
            case "txt" -> new Media("txt", "text/plain");
            case "xml" -> new Media("xml", "application/xml");
            case "html" -> new Media("html", "text/html");
            case "wav" -> new Media("wav", "audio/wav");
            case "mp3" -> new Media("mp3", "audio/mpeg");
            case "ogg" -> new Media("ogg", "audio/ogg");
            case "wasm" -> new Media("wasm", "application/wasm");
            case "pdf" -> new Media("pdf", "application/pdf");
            default -> new Media("bin", "application/octet-stream");
        };
    }
}
