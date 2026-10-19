package react4j.css.integration;

import react4j.css.Asset;
import react4j.css.Assets;

final class AssetFixtures {
    private AssetFixtures() {}

    @Assets
    abstract static class Media {
        static Media get() {
            return AssetFixtures_Assets_Media.get();
        }

        @Asset("asset.svg")
        abstract String icon();

        @Asset("unused.svg")
        abstract String unused();
    }
}
