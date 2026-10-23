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
import react4j.css.internal.CssFontValues;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssResource;

/** Keeps owner activation separate from DOM presence while following surviving CSS dependencies. */
final class CssReachability {
    private final Map<String, Resource> resources;
    private final Map<String, CssPruner> pruners = new TreeMap<>();
    private final Map<String, String> animationOwners = new HashMap<>();
    private final Set<String> animations = new HashSet<>();
    private final Set<String> fonts = new HashSet<>();
    private final Set<String> properties = new HashSet<>();
    private final Set<String> active = new HashSet<>();
    private final Set<String> queued = new HashSet<>();
    private final ArrayDeque<String> pending = new ArrayDeque<>();
    private final Map<String, Resource> retained = new TreeMap<>();

    private CssReachability(CssCatalogue catalogue, Set<String> live) {
        resources = catalogue.resources();
        var known = new HashSet<String>();
        var present = new HashSet<String>();
        for (var resource : resources.values()) {
            for (var symbol : resource.metadata().symbols()) {
                if (CssResource.SymbolKind.CLASS == symbol.kind() || CssResource.SymbolKind.ID == symbol.kind()) {
                    known.add((CssResource.SymbolKind.CLASS == symbol.kind() ? "." : "#") + symbol.value());
                }
            }
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
                value.binding().requires().forEach(graph::activate);
                if (CssResource.SymbolKind.ANIMATION == value.binding().kind()) {
                    value.binding().symbols().forEach(graph::animation);
                } else if (CssResource.SymbolKind.PROPERTY == value.binding().kind()) {
                    value.binding().symbols().forEach(graph::property);
                }
            }
        }
        while (!graph.pending.isEmpty()) {
            String owner = graph.pending.remove();
            graph.queued.remove(owner);
            var resource = Objects.requireNonNull(graph.resources.get(owner));
            resource.metadata().includes().forEach(graph::activate);
            var selected = Objects.requireNonNull(graph.pruners.get(owner))
                    .prune(graph.animations, graph.fonts, graph.properties);
            graph.retained.put(owner, selected);
            for (var occurrence : selected.metadata().occurrences()) {
                occurrence.requires().forEach(graph::activate);
                if (CssResource.SymbolKind.ANIMATION == occurrence.symbol().kind()) {
                    graph.animation(occurrence.symbol());
                } else if (CssResource.SymbolKind.PROPERTY
                        == occurrence.symbol().kind()) {
                    graph.property(occurrence.symbol());
                } else if (CssResource.SymbolKind.FONT == occurrence.symbol().kind()) {
                    graph.font(occurrence.symbol());
                }
            }
            for (var use : selected.metadata().fontUses()) {
                use.families().forEach(graph::font);
            }
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

    private void property(CssResource.SymbolId id) {
        var resource = Objects.requireNonNull(resources.get(id.owner()), id.owner());
        var symbol = resource.metadata().symbols().stream()
                .filter(candidate ->
                        candidate.kind() == id.kind() && candidate.name().equals(id.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing canonical CSS property: " + id.id()));
        activate(id.owner());
        if (properties.add(symbol.value())) {
            active.forEach(this::enqueue);
        }
    }

    private void font(CssResource.SymbolId id) {
        var resource = Objects.requireNonNull(resources.get(id.owner()), id.owner());
        var symbol = resource.metadata().symbols().stream()
                .filter(candidate ->
                        candidate.kind() == id.kind() && candidate.name().equals(id.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing canonical CSS font: " + id.id()));
        if (fonts.add(CssFontValues.canonical(symbol.value()))) {
            activate(id.owner());
            enqueue(id.owner());
        }
    }

    private void animation(CssResource.SymbolId id) {
        var resource = resources.get(id.owner());
        if (null == resource) {
            throw new IllegalArgumentException("Missing CSS animation owner: " + id.owner());
        }
        var symbol = resource.metadata().symbols().stream()
                .filter(candidate ->
                        candidate.kind() == id.kind() && candidate.name().equals(id.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing canonical CSS animation: " + id.id()));
        if (!id.owner().equals(animationOwners.get(symbol.value()))) {
            throw new IllegalArgumentException("CSS animation differs from its canonical owner: " + id.id());
        }
        if (animations.add(symbol.value())) {
            activate(id.owner());
            enqueue(id.owner());
        }
    }
}
