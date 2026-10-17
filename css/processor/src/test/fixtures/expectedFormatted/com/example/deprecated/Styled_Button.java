package com.example.deprecated;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@SuppressWarnings({"deprecation", "removal"})
@NullMarked
final class Styled_Button extends Button {
  private Styled_Button() {}

  static Button get() {
    return Styled_Button.Internal.INSTANCE;
  }

  @Override
  String active() {
    return "jcss-com-example-deprecated-Button jcss-com-example-deprecated-Button--active";
  }

  @Override
  String style() {
    return "jcss-com-example-deprecated-Button";
  }

  private static final class Internal {
    private static final Button INSTANCE = new Styled_Button();

    private Internal() {}
  }
}
