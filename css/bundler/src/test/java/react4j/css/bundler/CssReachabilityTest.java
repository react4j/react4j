package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import org.testng.annotations.Test;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssNames;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssResource.Binding;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssResource.SymbolKind;
import react4j.css.internal.CssTokens;
import react4j.css.internal.SourceMap;

public final class CssReachabilityTest {
    private static Binding binding(String method, SymbolKind kind, String value) {
        return new Binding(
                method, kind, value, List.of(), List.of(new CssResource.SymbolId("demo.Button", kind, "token")));
    }

    private static Resource resource(
            String owner,
            Kind kind,
            String css,
            List<Binding> bindings,
            List<String> includes,
            Map<String, List<String>> providers,
            String... peers) {
        bindings = bindings.stream()
                .map(binding -> {
                    var required = SymbolKind.PROPERTY == binding.kind()
                            ? providers.getOrDefault(binding.value(), List.of())
                            : binding.requires();
                    return new Binding(
                            binding.method(),
                            binding.kind(),
                            binding.value(),
                            required,
                            Arrays.stream(binding.value().split(" "))
                                    .map(token -> new CssResource.SymbolId(
                                            required.isEmpty() ? owner : required.get(required.size() - 1),
                                            binding.kind(),
                                            token))
                                    .toList());
                })
                .toList();
        var symbols = new ArrayList<CssResource.Symbol>();
        bindings.forEach(binding -> Arrays.stream(binding.value().split(" "))
                .forEach(token -> symbols.add(new CssResource.Symbol(binding.kind(), token, token, true))));
        var occurrences = new ArrayList<CssResource.Occurrence>();
        for (var token : CssTokens.read(css)) {
            if (SymbolKind.PROPERTY == token.kind() || SymbolKind.ANIMATION == token.kind()) {
                symbols.add(new CssResource.Symbol(token.kind(), token.value(), token.value(), true));
                occurrences.add(new CssResource.Occurrence(
                        new CssResource.SymbolId(owner, token.kind(), token.value()),
                        token.start(),
                        token.end(),
                        providers.getOrDefault(token.value(), List.of())));
            }
        }
        var dependencies = new ArrayList<>(includes);
        bindings.forEach(binding -> dependencies.addAll(binding.requires()));
        providers.values().forEach(dependencies::addAll);
        dependencies.addAll(Arrays.asList(peers));
        var metadata = new CssResource(
                owner,
                kind,
                dependencies.stream().distinct().toList(),
                includes,
                bindings.isEmpty() ? "" : "Styled_" + owner,
                bindings,
                symbols.stream().distinct().toList(),
                occurrences,
                List.of());
        return new Resource(
                metadata,
                css,
                JsonParser.parseString(new SourceMap.Writer()
                                .append(css, new SourceMap.Location(owner + ".java", css, 0, 0))
                                .map(owner + ".css"))
                        .getAsJsonObject());
    }

    private static Resource global(String owner, String css) {
        return resource(owner, Kind.GLOBAL, css, List.of(), List.of(), Map.of());
    }

    private static CssCatalogue catalogue(Resource... resources) {
        var animationOwners = new TreeMap<String, String>();
        for (var resource : resources) {
            var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(resource.css(), new CSSReaderSettings()));
            sheet.getAllKeyframesRules()
                    .forEach(frames -> animationOwners.put(
                            frames.getAnimationName(), resource.metadata().owner()));
        }
        var entries = new TreeMap<String, Resource>();
        for (var resource : resources) {
            var metadata = resource.metadata();
            var bindings = metadata.bindings().stream()
                    .map(binding -> new Binding(
                            binding.method(),
                            binding.kind(),
                            binding.value(),
                            binding.requires(),
                            binding.symbols().stream()
                                    .map(id -> SymbolKind.ANIMATION == id.kind()
                                            ? new CssResource.SymbolId(
                                                    Objects.requireNonNull(animationOwners.get(id.name())),
                                                    id.kind(),
                                                    id.name())
                                            : id)
                                    .toList()))
                    .toList();
            var occurrences = metadata.occurrences().stream()
                    .filter(use -> SymbolKind.ANIMATION != use.symbol().kind()
                            || animationOwners.containsKey(use.symbol().name()))
                    .map(use -> SymbolKind.ANIMATION == use.symbol().kind()
                            ? new CssResource.Occurrence(
                                    new CssResource.SymbolId(
                                            Objects.requireNonNull(animationOwners.get(
                                                    use.symbol().name())),
                                            use.symbol().kind(),
                                            use.symbol().name()),
                                    use.start(),
                                    use.end(),
                                    use.requires())
                            : use)
                    .toList();
            var symbols = metadata.symbols().stream()
                    .filter(symbol -> SymbolKind.ANIMATION != symbol.kind()
                            || metadata.owner().equals(animationOwners.get(symbol.name())))
                    .toList();
            var updated = new CssResource(
                    metadata.owner(),
                    metadata.kind(),
                    metadata.dependencies(),
                    metadata.includes(),
                    metadata.implementation(),
                    bindings,
                    symbols,
                    occurrences,
                    List.of());
            entries.put(metadata.owner(), new Resource(updated, resource.css(), resource.map()));
        }
        return new CssCatalogue(entries, "1".repeat(64), Map.of());
    }

    private static String bundle(CssCatalogue catalogue, Set<String> live, String... globals) throws Exception {
        return Bundle.assemble(
                        catalogue,
                        List.of(),
                        List.of(globals),
                        "app.css",
                        CssLink.create(catalogue, "//:app", true, new CssNames.Policy(false, "")),
                        live)
                .css();
    }

    @Test
    public void equalStableIdsActivateOnlyTheLiveBindingProvidersAndAlreadyActiveOwners() throws Exception {
        var catalogue = catalogue(
                resource(
                        "Consumer",
                        Kind.GLOBAL,
                        "#shared{font-weight:700;}",
                        List.of(
                                new Binding(
                                        "aDead",
                                        SymbolKind.ID,
                                        "shared",
                                        List.of("Second"),
                                        List.of(new CssResource.SymbolId("demo.Button", SymbolKind.ID, "token"))),
                                new Binding(
                                        "zLive",
                                        SymbolKind.ID,
                                        "shared",
                                        List.of("Alias", "First"),
                                        List.of(new CssResource.SymbolId("demo.Button", SymbolKind.ID, "token")))),
                        List.of("Active"),
                        Map.of()),
                resource(
                        "Alias",
                        Kind.GLOBAL,
                        "#shared{margin-left:19px;}",
                        List.of(),
                        List.of("Required"),
                        Map.of(),
                        "First"),
                resource(
                        "First",
                        Kind.GLOBAL,
                        "#shared{padding-left:13px;}",
                        List.of(binding("id", SymbolKind.ID, "shared")),
                        List.of(),
                        Map.of()),
                resource(
                        "Second",
                        Kind.GLOBAL,
                        "#shared{padding-right:37px;}",
                        List.of(binding("id", SymbolKind.ID, "shared")),
                        List.of(),
                        Map.of()),
                resource(
                        "Active",
                        Kind.GLOBAL,
                        "#shared{margin-right:29px;}",
                        List.of(binding("id", SymbolKind.ID, "shared")),
                        List.of(),
                        Map.of()),
                resource(
                        "Dormant",
                        Kind.GLOBAL,
                        "#shared{padding-top:43px;}",
                        List.of(binding("id", SymbolKind.ID, "shared")),
                        List.of(),
                        Map.of()),
                global("Required", "body{outline-width:23px;}"));
        String first = bundle(catalogue, Set.of("Consumer#zLive"));
        for (var retained : List.of(
                "font-weight:700",
                "margin-left:19px",
                "padding-left:13px",
                "margin-right:29px",
                "outline-width:23px")) {
            assertTrue(first.contains(retained), first);
        }
        assertFalse(first.contains("padding-right:37px"), first);
        assertFalse(first.contains("padding-top:43px"), first);
        String second = bundle(catalogue, Set.of("Consumer#aDead"));
        assertTrue(second.contains("padding-right:37px"), second);
        assertTrue(second.contains("margin-right:29px"), second);
        for (var omitted : List.of("padding-left:13px", "margin-left:19px", "outline-width:23px", "padding-top:43px")) {
            assertFalse(second.contains(omitted), second);
        }
    }

    @Test
    public void equalPropertySpellingsRetainOnlyTheProviderOfTheSurvivingAlias() throws Exception {
        String css = ".root.first{color:var(--shared);}.root.second{background:var(--shared);}";
        var consumer = resource(
                "Consumer",
                Kind.COMPONENT,
                css,
                List.of(
                        binding("style", SymbolKind.CLASS, "root"),
                        binding("first", SymbolKind.CLASS, "root first"),
                        binding("second", SymbolKind.CLASS, "root second")),
                List.of(),
                Map.of("--shared", List.of("First", "Second")));
        var uses = new ArrayList<CssResource.Occurrence>();
        for (var token : CssTokens.read(css)) {
            if (SymbolKind.PROPERTY == token.kind()) {
                String provider = token.start() < css.indexOf(".root.second") ? "First" : "Second";
                uses.add(new CssResource.Occurrence(
                        new CssResource.SymbolId(provider, SymbolKind.PROPERTY, "--shared"),
                        token.start(),
                        token.end(),
                        List.of(provider)));
            }
        }
        consumer = new Resource(consumer.metadata().withOccurrences(uses), consumer.css(), consumer.map());
        var catalogue = catalogue(
                consumer,
                global("First", "body{--shared:red;margin-left:13px;}"),
                global("Second", "body{--shared:blue;margin-right:37px;}"));
        String first = bundle(catalogue, Set.of("Consumer#first"));
        assertTrue(first.contains("margin-left:13px"), first);
        assertFalse(first.contains("margin-right:37px"), first);
        String second = bundle(catalogue, Set.of("Consumer#second"));
        assertTrue(second.contains("margin-right:37px"), second);
        assertFalse(second.contains("margin-left:13px"), second);
    }

    @Test
    public void liveIdsDoNotActivateEqualNamedAnimationsOrTheirProvider() throws Exception {
        var catalogue = catalogue(
                resource(
                        "IDs",
                        Kind.GLOBAL,
                        "#shared{color:red;}",
                        List.of(
                                binding("id", SymbolKind.ID, "shared"),
                                binding("animation", SymbolKind.ANIMATION, "shared")),
                        List.of(),
                        Map.of("shared", List.of("Pulse"))),
                resource(
                        "Pulse",
                        Kind.KEYFRAMES,
                        "@keyframes shared{to{opacity:0.12345;}}",
                        List.of(),
                        List.of(),
                        Map.of()));
        String id = bundle(catalogue, Set.of("IDs#id"));
        assertTrue(id.contains("#shared{color:red;}"), id);
        assertFalse(id.contains("@keyframes"), id);
        String animation = bundle(catalogue, Set.of("IDs#animation"));
        assertTrue(animation.contains("@keyframes shared"), animation);
        assertFalse(animation.contains("#shared"), animation);
    }

    @Test
    public void removedRulesReleaseLocalSharedAnimationsAndPropertyProviders() throws Exception {
        var catalogue = catalogue(
                resource(
                        "Button",
                        Kind.COMPONENT,
                        "@keyframes localDead{to{color:var(--unused);}}"
                                + ".root.dead{animation:dead 1s;color:var(--unused);}"
                                + ".root{animation:live 1s;color:var(--accent);}",
                        List.of(
                                binding("style", SymbolKind.CLASS, "root"),
                                binding("dead", SymbolKind.CLASS, "root dead")),
                        List.of(),
                        Map.of(
                                "dead",
                                List.of("DeadPulse"),
                                "live",
                                List.of("Pulse"),
                                "--unused",
                                List.of("Unused"),
                                "--accent",
                                List.of("Theme"))),
                resource(
                        "DeadPulse",
                        Kind.KEYFRAMES,
                        "@keyframes dead{to{color:var(--unused);}}",
                        List.of(),
                        List.of(),
                        Map.of("--unused", List.of("Unused"))),
                resource(
                        "Pulse",
                        Kind.KEYFRAMES,
                        "@keyframes live{to{color:var(--accent);}}",
                        List.of(),
                        List.of(),
                        Map.of("--accent", List.of("Theme"))),
                resource(
                        "Theme",
                        Kind.GLOBAL,
                        "body{--accent:blue;}",
                        List.of(),
                        List.of(),
                        Map.of("--accent", List.of("Defaults"))),
                global("Defaults", "body{--accent:red;}"),
                global("Unused", "body{--unused:pink;}"));
        String css = bundle(catalogue, Set.of("Button#style"));
        assertFalse(css.contains("localDead"), css);
        assertFalse(css.contains("@keyframes dead"), css);
        assertFalse(css.contains(".root.dead"), css);
        assertFalse(css.contains("pink"), css);
        assertTrue(css.contains("@keyframes live"), css);
        assertTrue(css.indexOf("--accent:red") < css.indexOf("--accent:blue"), css);
        assertTrue(css.contains(".root{animation:live"), css);
        assertFalse(bundle(catalogue, Set.of()).contains("@keyframes"));
    }

    @Test
    public void everyCheckedAccessorActivatesAuthoredIncludesWithoutCreatingClassPresence() throws Exception {
        for (var kind : List.of(SymbolKind.ID, SymbolKind.ANIMATION, SymbolKind.PROPERTY)) {
            String symbol = SymbolKind.ID == kind ? "node" : SymbolKind.ANIMATION == kind ? "spin" : "--accent";
            var catalogue = catalogue(
                    resource(
                            "Consumer",
                            Kind.COMPONENT,
                            ".consumer{color:red;}.consumer#node{margin:91px;}",
                            List.of(binding("style", SymbolKind.CLASS, "consumer"), binding("symbol", kind, symbol)),
                            List.of("Included"),
                            Map.of("spin", List.of("Pulse"), "--accent", List.of("Theme"))),
                    global("Included", "body{margin:3px;}"),
                    global("Theme", "body{--accent:blue;}"),
                    resource(
                            "Pulse",
                            Kind.KEYFRAMES,
                            "@keyframes spin{to{opacity:1;}}",
                            List.of(),
                            List.of(),
                            Map.of()));
            String css = bundle(catalogue, Set.of("Consumer#symbol"));
            assertTrue(css.contains("margin:3px"), css);
            assertFalse(css.contains(".consumer"), css);
            assertEquals(css.contains("@keyframes spin"), SymbolKind.ANIMATION == kind, css);
            assertEquals(css.contains("--accent:blue"), SymbolKind.PROPERTY == kind, css);
        }
    }

    @Test
    public void peerSelectorsAndFragmentUrlsDoNotEstablishDomPresence() throws Exception {
        var catalogue = catalogue(
                resource(
                        "Button",
                        Kind.COMPONENT,
                        ".root{filter:url(#node);}.root#node{padding:91px;}.root+.peer{color:var(--peer);}",
                        List.of(binding("style", SymbolKind.CLASS, "root"), binding("id", SymbolKind.ID, "node")),
                        List.of(),
                        Map.of("--peer", List.of("PeerTheme")),
                        "Peer"),
                resource(
                        "Peer",
                        Kind.COMPONENT,
                        ".peer{color:blue;}",
                        List.of(binding("style", SymbolKind.CLASS, "peer")),
                        List.of("PeerTheme"),
                        Map.of()),
                global("PeerTheme", "body{--peer:pink;}"));
        String css = bundle(catalogue, Set.of("Button#style"));
        assertTrue(css.contains("url(#node)"), css);
        assertFalse(css.contains("91px"), css);
        assertFalse(css.contains(".peer"), css);
        assertFalse(css.contains("pink"), css);
        assertTrue(bundle(catalogue, Set.of("Button#style", "Peer#style")).contains("pink"));
        assertTrue(bundle(catalogue, Set.of("Button#style", "Button#id")).contains("91px"));
    }

    @Test
    public void variantsKeepIndependentRootsAndProvidersSharingACanonicalName() throws Exception {
        var catalogue = catalogue(
                resource(
                        "Button",
                        Kind.COMPONENT,
                        ".root{color:var(--accent);}",
                        List.of(binding("style", SymbolKind.CLASS, "root")),
                        List.of(),
                        Map.of("--accent", List.of("Defaults", "Provider"))),
                resource(
                        "Provider",
                        Kind.GLOBAL,
                        "body{--accent:blue;}",
                        List.of(),
                        List.of(),
                        Map.of("--accent", List.of("Defaults"))),
                global("Defaults", "body{--accent:red;}"),
                global("ThemeA", "body{margin:31px;}"),
                global("ThemeB", "body{margin:32px;}"));
        var live = Set.of("Button#style");
        String first = bundle(catalogue, live, "ThemeA");
        String second = bundle(catalogue, live, "ThemeB");
        assertTrue(first.contains("31px"));
        assertFalse(first.contains("32px"));
        assertTrue(second.contains("32px"));
        assertFalse(second.contains("31px"));
        for (var css : List.of(first, second)) {
            assertTrue(css.indexOf("--accent:red") < css.indexOf("--accent:blue"), css);
        }
    }

    @Test
    public void retainedSupportConditionsAndKeyframesFollowPropertiesButEmptyGroupsDoNot() throws Exception {
        var catalogue = catalogue(
                resource(
                        "Button",
                        Kind.COMPONENT,
                        "@supports (color:var(--support)){.root{color:red;}}"
                                + "@supports (color:var(--dead)){.dead{color:blue;}}",
                        List.of(binding("style", SymbolKind.CLASS, "root"), binding("dead", SymbolKind.CLASS, "dead")),
                        List.of(),
                        Map.of("--support", List.of("Support"), "--dead", List.of("Dead"))),
                global("Support", "body{--support:green;}"),
                global("Dead", "body{--dead:pink;}"));
        String css = bundle(catalogue, Set.of("Button#style"));
        assertTrue(css.contains("--support:green"), css);
        assertFalse(css.contains("--dead"), css);
        assertFalse(css.contains("pink"), css);
    }

    @Test
    public void invalidGraphsAndRootsFailRatherThanInventRetention() {
        var missing = catalogue(resource(
                "A",
                Kind.COMPONENT,
                ".root{color:red;}",
                List.of(binding("style", SymbolKind.CLASS, "root")),
                List.of("Missing"),
                Map.of()));
        expectThrows(IllegalArgumentException.class, () -> bundle(missing, Set.of("A#style")));
        for (var root : List.of("Missing", "A")) {
            expectThrows(IllegalArgumentException.class, () -> bundle(missing, Set.of(), root));
        }
        var cycle = catalogue(
                resource("A", Kind.GLOBAL, "body{color:red;}", List.of(), List.of("B"), Map.of()),
                resource("B", Kind.GLOBAL, "body{color:blue;}", List.of(), List.of("A"), Map.of()));
        expectThrows(IllegalArgumentException.class, () -> bundle(cycle, Set.of(), "A"));
        var duplicate = catalogue(
                resource("A", Kind.KEYFRAMES, "@keyframes spin{to{opacity:1;}}", List.of(), List.of(), Map.of()),
                resource("B", Kind.KEYFRAMES, "@keyframes spin{to{opacity:0;}}", List.of(), List.of(), Map.of()));
        expectThrows(IllegalArgumentException.class, () -> bundle(duplicate, Set.of()));
    }
}
