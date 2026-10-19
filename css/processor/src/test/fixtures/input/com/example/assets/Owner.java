package com.example.assets;

import org.jspecify.annotations.NullMarked;
import react4j.css.Asset;
import react4j.css.Assets;

@NullMarked
public final class Owner {
    @Assets
    public abstract static class Media implements AssetContract {
        public static Media get() {
            return Owner_Assets_Media.get();
        }

        @Asset("card/portrait.png")
        abstract String portrait();

        String decorated() {
            return "icon: " + icon();
        }
    }
}

@NullMarked
interface AssetContract {
    @Asset("card/edit.svg")
    String icon();
}
