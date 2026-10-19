package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import react4j.css.internal.AssetResource;

/** Derives offline policy from retained URLs before the generated lists are independently compressed. */
public final class ApplicationPlan {
    private ApplicationPlan() {}

    public static void main(String[] args) throws IOException {
        run(
                1 == args.length && args[0].startsWith("@")
                        ? Files.readAllLines(Path.of(args[0].substring(1)))
                        : Arrays.asList(args));
    }

    static void run(List<String> args) throws IOException {
        var options = new TreeMap<String, Path>();
        var roots = new ArrayList<AssetContent>();
        var precache = new TreeSet<String>();
        var required = Set.of("--manifest", "--output", "--precache-output", "--on-demand-output");
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing application plan option value");
            }
            String option = args.get(i);
            String value = args.get(i + 1);
            if ("--prepared".equals(option)) {
                roots.add(AssetContent.read(Path.of(value)));
            } else if ("--precache".equals(option)) {
                AssetResource.validatePath(value);
                if (!precache.add(value)) {
                    throw new IllegalArgumentException("Repeated precache asset: " + value);
                }
            } else if (!required.contains(option) || null != options.putIfAbsent(option, Path.of(value))) {
                throw new IllegalArgumentException("Unknown or repeated application plan option: " + option);
            }
        }
        if (!options.keySet().equals(required)) {
            throw new IllegalArgumentException("Expected linked inventory, application plan and offline list outputs");
        }
        var inventory = JsonParser.parseString(Files.readString(options.get("--manifest")))
                .getAsJsonObject();
        var plan = create(inventory, roots, precache);
        Files.writeString(options.get("--precache-output"), plan.get("precache") + "\n");
        Files.writeString(options.get("--on-demand-output"), plan.get("onDemand") + "\n");
        Files.writeString(options.get("--output"), plan + "\n");
    }

    private static JsonObject create(JsonObject inventory, List<AssetContent> roots, Set<String> precache) {
        if (!"1".equals(inventory.get("version").toString())) {
            throw new IllegalArgumentException("Unsupported linked asset inventory");
        }
        var candidates = new TreeSet<String>();
        for (var item : inventory.getAsJsonArray("pruned")) {
            String path = item.getAsString();
            AssetResource.validatePath(path);
            if (!candidates.add(path)) {
                throw new IllegalArgumentException("Repeated pruned asset: " + path);
            }
        }
        var entries = new ArrayList<JsonObject>();
        var occupied = new TreeSet<String>();
        for (var item : inventory.getAsJsonArray("assets")) {
            var entry = item.getAsJsonObject();
            var content = AssetContent.managed(entry);
            if (!candidates.add(content.logicalPath())) {
                throw new IllegalArgumentException("Repeated linked asset: " + content.logicalPath());
            }
            occupied.add(content.url());
            if (null != content.compressed()) {
                occupied.add(content.url() + ".br");
            }
            entries.add(entry);
        }
        var rootMetadata = new JsonArray();
        for (var content : roots) {
            String destination = content.logicalPath();
            if (Set.of("precache.json", "on-demand.json").contains(destination)
                    || !occupied.add(destination)
                    || null != content.compressed() && !occupied.add(destination + ".br")) {
                throw new IllegalArgumentException("Reserved or duplicate application destination: " + destination);
            }
            candidates.add(destination);
            entries.add(content.entry(destination, List.of(), List.of("application")));
            rootMetadata.add(content.json());
        }
        if (!candidates.containsAll(precache)) {
            var unknown = new TreeSet<>(precache);
            unknown.removeAll(candidates);
            throw new IllegalArgumentException("Unknown precache asset: " + unknown);
        }
        var cached = new TreeSet<String>();
        var onDemand = new TreeSet<String>();
        for (var entry : entries) {
            String url = entry.get("url").getAsString();
            (precache.contains(entry.get("logicalPath").getAsString()) ? cached : onDemand).add(url);
        }
        onDemand.removeAll(cached);
        var plan = inventory.deepCopy();
        plan.add("roots", rootMetadata);
        plan.add("precache", array(cached));
        plan.add("onDemand", array(onDemand));
        return plan;
    }

    private static JsonArray array(Set<String> values) {
        var array = new JsonArray();
        values.forEach(array::add);
        return array;
    }
}
