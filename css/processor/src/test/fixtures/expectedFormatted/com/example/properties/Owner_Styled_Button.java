package com.example.properties;

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
  String accent() {
    return "--jcss-com-example-properties-Owner_0024_Defaults--accent";
  }

  @Override
  String style() {
    return "jcss-com-example-properties-Owner_0024_Button";
  }

  @Override
  String width() {
    return "--jcss-com-example-properties-Owner_0024_Button--runtime_002d_width";
  }

  private static final class Internal {
    private static final Owner.Button INSTANCE = new Owner_Styled_Button();

    private Internal() {}
  }
}
