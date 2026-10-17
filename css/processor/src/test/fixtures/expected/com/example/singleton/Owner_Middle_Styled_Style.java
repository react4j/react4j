package com.example.singleton;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Middle_Styled_Style extends Owner.Middle.Style {
  private Owner_Middle_Styled_Style() {
  }

  static Owner.Middle.Style get() {
    return Owner_Middle_Styled_Style.Internal.INSTANCE;
  }

  @Override
  public String style() {
    return "jcss-com-example-singleton-Owner_0024_Middle_0024_Style";
  }

  private static final class Internal {
    private static final Owner.Middle.Style INSTANCE = new Owner_Middle_Styled_Style();

    private Internal() {
    }
  }
}
