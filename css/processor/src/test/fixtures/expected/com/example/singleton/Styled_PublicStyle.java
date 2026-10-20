package com.example.singleton;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_PublicStyle extends PublicStyle {
  private Styled_PublicStyle() {
  }

  public static PublicStyle get() {
    return Internal.INSTANCE;
  }

  @Override
  public String selected() {
    return "jcss-com-example-singleton-PublicStyle jcss-com-example-singleton-PublicStyle--selected";
  }

  @Override
  public String style() {
    return "jcss-com-example-singleton-PublicStyle";
  }

  private static final class Internal {
    private static final PublicStyle INSTANCE = new Styled_PublicStyle();

    private Internal() {
    }
  }
}
