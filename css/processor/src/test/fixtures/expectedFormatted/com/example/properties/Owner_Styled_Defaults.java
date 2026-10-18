package com.example.properties;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Defaults extends Owner.Defaults {
  private Owner_Styled_Defaults() {}

  static Owner.Defaults get() {
    return Owner_Styled_Defaults.Internal.INSTANCE;
  }

  @Override
  String getAccent() {
    return "--jcss-com-example-properties-Owner_0024_Defaults--accent";
  }

  private static final class Internal {
    private static final Owner.Defaults INSTANCE = new Owner_Styled_Defaults();

    private Internal() {}
  }
}
