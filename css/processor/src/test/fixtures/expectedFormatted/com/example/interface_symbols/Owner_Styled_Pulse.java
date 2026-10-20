package com.example.interface_symbols;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Pulse extends Owner.Pulse {
  private Owner_Styled_Pulse() {}

  public static Owner.Pulse get() {
    return Internal.INSTANCE;
  }

  @Override
  public String getAccent() {
    return "--jcss-com-example-interface_005f_symbols-Owner_0024_Pulse--accent";
  }

  @Override
  public String getControl() {
    return "jcss-com-example-interface_005f_symbols-Owner_0024_Pulse--id-control";
  }

  @Override
  public String getPulse() {
    return "jcss-com-example-interface_005f_symbols-Owner_0024_Pulse--keyframes";
  }

  private static final class Internal {
    private static final Owner.Pulse INSTANCE = new Owner_Styled_Pulse();

    private Internal() {}
  }
}
