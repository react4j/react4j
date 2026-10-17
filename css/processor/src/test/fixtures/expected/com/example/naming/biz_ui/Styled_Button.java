package com.example.naming.biz_ui;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_Button extends Button {
  private Styled_Button() {
  }

  static Button get() {
    return Styled_Button.Internal.INSTANCE;
  }

  @Override
  String style() {
    return "jcss-com-example-naming-biz_005f_ui-Button";
  }

  private static final class Internal {
    private static final Button INSTANCE = new Styled_Button();

    private Internal() {
    }
  }
}
