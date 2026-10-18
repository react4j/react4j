package com.example.symbols;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Pulse extends Owner.Pulse {
  private Owner_Styled_Pulse() {
  }

  public static Owner.Pulse get() {
    return Owner_Styled_Pulse.Internal.INSTANCE;
  }

  @Override
  public String effect() {
    return "jcss-com-example-symbols-Owner_0024_Pulse--id-effect";
  }

  @Override
  public String name() {
    return "jcss-com-example-symbols-Owner_0024_Pulse--keyframes";
  }

  private static final class Internal {
    private static final Owner.Pulse INSTANCE = new Owner_Styled_Pulse();

    private Internal() {
    }
  }
}
