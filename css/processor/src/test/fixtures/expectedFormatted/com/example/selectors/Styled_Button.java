package com.example.selectors;

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
  String cancel() {
    return "jcss-com-example-selectors-Button jcss-com-example-selectors-Button--cancel";
  }

  @Override
  String style() {
    return "jcss-com-example-selectors-Button";
  }

  @Override
  String success() {
    return "jcss-com-example-selectors-Button jcss-com-example-selectors-Button--success";
  }

  private static final class Internal {
    private static final Button INSTANCE = new Styled_Button();

    private Internal() {}
  }
}
