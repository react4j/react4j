package com.example.basic;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_Button extends Button {
  private Styled_Button() {}

  static Button get() {
    return Styled_Button.Internal.INSTANCE;
  }

  @Override
  protected String cancel() {
    return "jcss-com-example-basic-Button jcss-com-example-basic-Button--cancel";
  }

  @Override
  String style() {
    return "jcss-com-example-basic-Button";
  }

  @Override
  String success() {
    return "jcss-com-example-basic-Button jcss-com-example-basic-Button--success";
  }

  private static final class Internal {
    private static final Button INSTANCE = new Styled_Button();

    private Internal() {}
  }
}
