package react4j.css.integration;

import static org.testng.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.AssetResource;

public final class AssetIntegrationTest {
    @Test
    public void j2clPreservesDeclarationsAndBazelDeclaresTheirLogicalPaths() throws Exception {
        var tree = Path.of(Objects.requireNonNull(System.getProperty("assets.tree")));
        var manifest = Path.of(Objects.requireNonNull(System.getProperty("assets.catalogue")));
        var definitions = AssetResource.readTrees(List.of(tree));
        assertEquals(definitions.size(), 2);
        var media = Objects.requireNonNull(definitions.get("react4j.css.integration.AssetFixtures$Media"));
        assertEquals(media.implementation(), "react4j.css.integration.AssetFixtures_Assets_Media");
        var catalogue = AssetResource.catalogue(List.of(manifest));
        assertEquals(
                catalogue.keySet(), Set.of("react4j/css/integration/asset.svg", "react4j/css/integration/unused.svg"));
        AssetResource.validate(definitions, catalogue);
        String javascript =
                Files.readString(tree.resolve("react4j/css/integration/AssetFixtures_Assets_Media.impl.java.js"));
        assertTrue(javascript.contains("asset:react4j/css/integration/asset.svg"), javascript);
        assertFalse(javascript.contains("<svg"));
    }
}
