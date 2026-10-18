package com.example.singleton;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_PackageStyle extends PackageStyle {
  private Styled_PackageStyle() {}

  public static PackageStyle get() {
    return Internal.INSTANCE;
  }

  @Override
  String selected() {
    return "jcss-com-example-singleton-PackageStyle jcss-com-example-singleton-PackageStyle--selected";
  }

  @Override
  String style() {
    return "jcss-com-example-singleton-PackageStyle";
  }

  private static final class Internal {
    private static final PackageStyle INSTANCE = new Styled_PackageStyle();

    private Internal() {}
  }
}
