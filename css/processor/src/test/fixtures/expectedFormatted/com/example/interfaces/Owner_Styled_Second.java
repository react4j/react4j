package com.example.interfaces;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Second extends Owner.Second {
  private Owner_Styled_Second() {}

  static Owner.Second get() {
    return Owner_Styled_Second.Internal.INSTANCE;
  }

  @Override
  public String style() {
    return "jcss-com-example-interfaces-Owner_0024_Second";
  }

  @Override
  public String success() {
    return "jcss-com-example-interfaces-Owner_0024_Second jcss-com-example-interfaces-Owner_0024_Second--success";
  }

  private static final class Internal {
    private static final Owner.Second INSTANCE = new Owner_Styled_Second();

    private Internal() {}
  }
}
