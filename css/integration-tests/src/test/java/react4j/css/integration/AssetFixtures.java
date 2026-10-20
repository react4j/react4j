package react4j.css.integration;

import jsinterop.annotations.JsMethod;
import react4j.css.Asset;
import react4j.css.Assets;

public final class AssetFixtures {
    private AssetFixtures() {}

    @JsMethod
    public static String icon() {
        return throughInterface(Media.get());
    }

    private static String throughInterface(Images images) {
        return images.icon();
    }

    @JsMethod
    public static String shadowed() {
        return Shadowed.get().icon();
    }

    @Assets
    abstract static class Shadowed {
        static class AssetFixtures_Assets_Shadowed {}

        static Shadowed get() {
            return react4j.css.integration.AssetFixtures_Assets_Shadowed.get();
        }

        @Asset("asset.svg")
        abstract String icon();
    }

    interface Images {
        @Asset("asset.svg")
        String icon();
    }

    @Assets
    abstract static class Media implements Images {
        static Media get() {
            return AssetFixtures_Assets_Media.get();
        }

        @Asset("unused.svg")
        abstract String unused();
    }
}
