package react4j.css.bundler;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssResource;

/** Keeps owner activation separate from DOM presence while following surviving CSS dependencies. */
final class CssReachability {
    private final Map<String, Resource> resources;
    private final Map<String, CssPruner> pruners = new TreeMap<>();
    private final Map<String, String> animationOwners = new HashMap<>();
    private final Set<String> animations = new HashSet<>();
    private final Set<String> active = new HashSet<>();
    private final Set<String> queued = new HashSet<>();
    private final ArrayDeque<String> pending = new ArrayDeque<>();
    private final Map<String, Resource> retained = new TreeMap<>();

    private CssReachability(CssCatalogue catalogue, Set<String> live) {
        resources = catalogue.resources();
        var known = new HashSet<String>();
        var present = new HashSet<String>();
        for (var resource : resources.values()) {
            for (var binding : resource.metadata().bindings()) {
                if (CssResource.SymbolKind.CLASS == binding.kind() || CssResource.SymbolKind.ID == binding.kind()) {
                    String prefix = CssResource.SymbolKind.CLASS == binding.kind() ? "." : "#";
                    for (var value : binding.value().split(" ")) {
                        known.add(prefix + value);
                        if (live.contains(resource.metadata().owner() + "#" + binding.method())) {
                            present.add(prefix + value);
                        }
                    }
                }
            }
        }
        for (var resource : resources.values()) {
            String owner = resource.metadata().owner();
            var pruner = new CssPruner(resource, known, present);
            pruners.put(owner, pruner);
            for (var name : pruner.animations()) {
                if (null != animationOwners.putIfAbsent(name, owner)) {
                    throw new IllegalArgumentException("Duplicate generated animation: " + name);
                }
            }
        }
    }

    static Map<String, Resource> prune(CssCatalogue catalogue, CssLink link, Set<String> live, List<String> globals) {
        var graph = new CssReachability(catalogue, live);
        for (var owner : globals) {
            graph.activate(owner);
        }
        for (var value : link.values()) {
            if (live.contains(value.id())) {
                graph.activate(value.owner());
                graph.reference(value.owner(), value.binding().value());
            }
        }
        while (!graph.pending.isEmpty()) {
            String owner = graph.pending.remove();
            graph.queued.remove(owner);
            var resource = Objects.requireNonNull(graph.resources.get(owner));
            resource.metadata().includes().forEach(graph::activate);
            var selection = Objects.requireNonNull(graph.pruners.get(owner)).prune(graph.animations);
            graph.retained.put(owner, selection.resource());
            selection.references().forEach(symbol -> graph.reference(owner, symbol));
        }
        return graph.retained;
    }

    private void activate(String owner) {
        if (!resources.containsKey(owner)) {
            throw new IllegalArgumentException("Missing CSS dependency: " + owner);
        }
        if (active.add(owner)) {
            enqueue(owner);
        }
    }

    private void enqueue(String owner) {
        if (queued.add(owner)) {
            pending.add(owner);
        }
    }

    private void reference(String owner, String symbol) {
        var providers = Objects.requireNonNull(resources.get(owner))
                .metadata()
                .providers()
                .get(symbol);
        if (null != providers) {
            providers.forEach(this::activate);
        }
        var animationOwner = animationOwners.get(symbol);
        if (null != animationOwner && animations.add(symbol)) {
            activate(animationOwner);
            enqueue(animationOwner);
        }
    }
}
