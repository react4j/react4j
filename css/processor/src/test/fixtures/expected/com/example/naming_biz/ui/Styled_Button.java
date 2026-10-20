package com.example.naming_biz.ui;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_Button extends Button {
  private Styled_Button() {
  }

  static Button get() {
    return Internal.INSTANCE;
  }

  @Override
  String style() {
    return "jcss-com-example-naming_005f_biz-ui-Button";
  }

  private static final class Internal {
    private static final Button INSTANCE = new Styled_Button();

    private Internal() {
    }
  }
}
