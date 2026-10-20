package com.example.properties;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Pulse extends Owner.Pulse {
  private Owner_Styled_Pulse() {
  }

  static Owner.Pulse get() {
    return Internal.INSTANCE;
  }

  @Override
  String color() {
    return "--jcss-com-example-properties-Owner_0024_Defaults--accent";
  }

  @Override
  String opacity() {
    return "--jcss-com-example-properties-Owner_0024_Pulse--opacity";
  }

  private static final class Internal {
    private static final Owner.Pulse INSTANCE = new Owner_Styled_Pulse();

    private Internal() {
    }
  }
}
