package com.example.symbols;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Button extends Owner.Button {
  private Owner_Styled_Button() {
  }

  static Owner.Button get() {
    return Internal.INSTANCE;
  }

  @Override
  String filterId() {
    return "jcss-com-example-symbols-Owner_0024_Button--id-mask";
  }

  @Override
  String getControl() {
    return "jcss-com-example-symbols-Owner_0024_Button--id-control";
  }

  @Override
  String ink() {
    return "--jcss-com-example-symbols-Owner_0024_Page--accent";
  }

  @Override
  String local() {
    return "jcss-com-example-symbols-Owner_0024_Button--keyframes-local";
  }

  @Override
  String sharedAnimation() {
    return "jcss-com-example-symbols-Owner_0024_Pulse--keyframes";
  }

  @Override
  String style() {
    return "jcss-com-example-symbols-Owner_0024_Button";
  }

  private static final class Internal {
    private static final Owner.Button INSTANCE = new Owner_Styled_Button();

    private Internal() {
    }
  }
}
