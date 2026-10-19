package com.example.assets;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Assets_Media extends Owner.Media {
  private Owner_Assets_Media() {}

  public static Owner.Media get() {
    return Internal.INSTANCE;
  }

  @Override
  String font() {
    return "asset:com/example/assets/card/heading.ttf";
  }

  @Override
  public String icon() {
    return "asset:com/example/assets/card/edit.svg";
  }

  @Override
  String portrait() {
    return "asset:com/example/assets/card/portrait.png";
  }

  private static final class Internal {
    private static final Owner.Media INSTANCE = new Owner_Assets_Media();

    private Internal() {}
  }
}
