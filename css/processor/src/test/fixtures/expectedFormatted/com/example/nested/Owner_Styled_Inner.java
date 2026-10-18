package com.example.nested;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Inner extends Owner.Inner {
  private Owner_Styled_Inner() {}

  static Owner.Inner get() {
    return Internal.INSTANCE;
  }

  @Override
  String _ready() {
    return "jcss-com-example-nested-Owner_0024_Inner jcss-com-example-nested-Owner_0024_Inner--_005f_ready";
  }

  @Override
  String style() {
    return "jcss-com-example-nested-Owner_0024_Inner";
  }

  private static final class Internal {
    private static final Owner.Inner INSTANCE = new Owner_Styled_Inner();

    private Internal() {}
  }
}
