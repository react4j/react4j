package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssLink;

/** Finalizes linked asset URLs and writes a deterministic application asset inventory. */
final class AssetBundle {
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
        var styles = new ArrayList<AssetCss>();
        var prepared = new TreeMap<String, AssetContent>();
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
            } else if ("--prepared".equals(option)) {
                var content = AssetContent.read(Path.of(value));
                var previous = prepared.putIfAbsent(content.logicalPath(), content);
                if (null != previous
                        && (!previous.source().equals(content.source())
                                || !previous.sha256().equals(content.sha256())
                                || previous.size() != content.size())) {
                    throw new IllegalArgumentException("Conflicting prepared asset: " + content.logicalPath());
                }
            } else if ("--style".equals(option)) {
                styles.add(AssetCss.read(value));
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
        CssLink.validateCssAssets(catalogue, files);
        if (!prepared.keySet().equals(files.keySet())) {
            throw new IllegalArgumentException("Prepared assets differ from the declared asset catalogue");
        }
        for (var content : prepared.values()) {
            if (!content.source().equals(Path.of(Objects.requireNonNull(files.get(content.logicalPath()))))) {
                throw new IllegalArgumentException(
                        "Prepared asset source differs from its catalogue: " + content.logicalPath());
            }
        }
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
                        .add("java:" + asset.id());
            }
        }
        for (var style : styles) {
            for (var path : style.references()) {
                if (!files.containsKey(path)) {
                    throw new IllegalArgumentException("Missing declared CSS asset: " + path);
                }
                retained.computeIfAbsent(path, ignored -> new TreeSet<>()).add("css:" + style.name());
            }
        }
        var fileUrls = new TreeMap<String, String>();
        var urls = new TreeMap<String, String>();
        var entries = new JsonArray();
        for (var entry : retained.entrySet()) {
            String logical = entry.getKey();
            var content = Objects.requireNonNull(prepared.get(logical));
            String url = content.url();
            fileUrls.put(logical, url);
            content.copy(directory, url);
            entries.add(content.entry(
                    url,
                    link.assets().stream()
                            .filter(asset -> asset.binding().path().equals(logical))
                            .map(CssLink.Asset::id)
                            .sorted()
                            .toList(),
                    List.copyOf(entry.getValue())));
            for (var asset : link.assets()) {
                if (asset.binding().path().equals(logical) && entry.getValue().contains("java:" + asset.id())) {
                    urls.put(link.optimized() ? asset.token() : asset.binding().value(), url);
                }
            }
        }
        for (var style : styles) {
            style.write(fileUrls);
        }
        String code = Files.readString(javascript);
        var mapped = AssetJavaScript.finalizeBundle(code, Path.of(options.get("--map")), link, trees, urls);
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
}
