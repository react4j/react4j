package com.example.interface_symbols;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Button extends Owner.Button {
  private Owner_Styled_Button() {}

  static Owner.Button get() {
    return Internal.INSTANCE;
  }

  @Override
  public String getAccent() {
    return "--jcss-com-example-interface_005f_symbols-Owner_0024_Button--accent";
  }

  @Override
  public String getControl() {
    return "jcss-com-example-interface_005f_symbols-Owner_0024_Button--id-widget";
  }

  @Override
  public String getPulse() {
    return "jcss-com-example-interface_005f_symbols-Owner_0024_Pulse--keyframes";
  }

  @Override
  String style() {
    return "jcss-com-example-interface_005f_symbols-Owner_0024_Button";
  }

  private static final class Internal {
    private static final Owner.Button INSTANCE = new Owner_Styled_Button();

    private Internal() {}
  }
}
