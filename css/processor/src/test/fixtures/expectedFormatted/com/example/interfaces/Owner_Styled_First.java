package com.example.interfaces;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_First extends Owner.First {
  private Owner_Styled_First() {}

  public static Owner.First get() {
    return Owner_Styled_First.Internal.INSTANCE;
  }

  @Override
  public String style() {
    return "jcss-com-example-interfaces-Owner_0024_First";
  }

  @Override
  public String success() {
    return "jcss-com-example-interfaces-Owner_0024_First jcss-com-example-interfaces-Owner_0024_First--success";
  }

  private static final class Internal {
    private static final Owner.First INSTANCE = new Owner_Styled_First();

    private Internal() {}
  }
}
