package react4j.css.internal;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** The deterministic collection shared by one JavaScript link and its CSS variants. */
public record CssCatalogue(Map<String, Resource> resources, String digest, Map<String, AssetResource> assets) {
    public record Resource(CssResource metadata, String css, JsonObject map) {}

    public CssCatalogue {
        resources = Collections.unmodifiableMap(new TreeMap<>(resources));
        assets = Collections.unmodifiableMap(new TreeMap<>(assets));
        for (var entry : assets.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().owner()) || resources.containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Conflicting CSS and asset definition: " + entry.getKey());
            }
        }
    }

    public static CssCatalogue read(List<Path> trees) throws IOException {
        var files = new TreeMap<String, Path>();
        for (var tree : trees.stream().distinct().toList()) {
            try (var paths = Files.walk(tree)) {
                for (var path : paths.filter(Files::isRegularFile).sorted().toList()) {
                    String name = tree.relativize(path).toString().replace('\\', '/');
                    if (name.endsWith(".css")
                            || name.endsWith(".css.map")
                            || name.endsWith(".css.json")
                            || name.endsWith(".assets.json")
                            || name.endsWith(".css.fragments.json")) {
                        if (null != files.putIfAbsent(name, path)) {
                            throw new IllegalArgumentException("Duplicate CSS resource: " + name);
                        }
                    }
                }
            }
        }
        var definitions = new TreeMap<String, Resource>();
        var assets = new TreeMap<String, AssetResource>();
        var fragments = new ArrayList<CssFragments.Located>();
        var hash = sha256();
        for (var entry : files.entrySet()) {
            String name = entry.getKey();
            String content = Files.readString(entry.getValue());
            hash.update(
                    (name.length() + ":" + name + content.length() + ":" + content).getBytes(StandardCharsets.UTF_8));
            if (name.endsWith(".css.fragments.json")) {
                String requester = name.substring(0, name.length() - ".css.fragments.json".length())
                        .replace('/', '.');
                if (!files.containsKey(CssResource.path(requester))) {
                    throw new IllegalArgumentException("Utility fragments without requesting CSS metadata: " + name);
                }
                for (var fragment : CssFragments.read(content, requester).fragments()) {
                    fragments.add(
                            new CssFragments.Located(fragment, entry.getValue().toString()));
                }
                continue;
            }
            if (name.endsWith(".assets.json")) {
                String owner = name.substring(0, name.length() - ".assets.json".length())
                        .replace('/', '.');
                assets.put(owner, AssetResource.read(content, owner));
                continue;
            }
            if (!name.endsWith(".css")) {
                if (!files.containsKey(name.substring(0, name.lastIndexOf('.')))) {
                    throw new IllegalArgumentException("CSS metadata or source map without CSS: " + name);
                }
                continue;
            }
            Path mapPath = files.get(name + ".map");
            Path metadataPath = files.get(name + ".json");
            if (null == mapPath || null == metadataPath) {
                throw new IllegalArgumentException("Missing CSS source map or metadata: " + name);
            }
            String owner = name.substring(0, name.length() - 4).replace('/', '.');
            String file = entry.getValue().getFileName().toString();
            String reference = SourceMap.reference(file);
            if (!content.endsWith(reference)) {
                throw new IllegalArgumentException("Missing component sourceMappingURL: " + name);
            }
            definitions.put(
                    owner,
                    new Resource(
                            CssResource.read(Files.readString(metadataPath), owner),
                            content.substring(0, content.length() - reference.length()),
                            readMap(mapPath, file)));
        }
        return new CssCatalogue(
                CssFragments.merge(definitions, fragments), HexFormat.of().formatHex(hash.digest()), assets);
    }

    static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private static JsonObject readMap(Path path, String cssName) throws IOException {
        try {
            var map = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (!map.has("version")
                    || !"3".equals(map.get("version").toString())
                    || !map.has("file")
                    || !cssName.equals(map.get("file").getAsString())
                    || !map.has("mappings")
                    || !map.has("sources")
                    || !map.has("sourcesContent")) {
                throw new IllegalArgumentException("Invalid component source map: " + path);
            }
            return map;
        } catch (JsonParseException | IllegalStateException e) {
            throw new IllegalArgumentException("Invalid component source map: " + path, e);
        }
    }
}
