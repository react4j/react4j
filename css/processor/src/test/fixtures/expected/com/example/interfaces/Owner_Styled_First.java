package com.example.interfaces;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_First {
  private Owner_Styled_First() {
  }

  public static Owner.First get() {
    return Internal_.INSTANCE;
  }

  private static final class Internal_ {
    private static final Owner.First INSTANCE = new Implementation();

    private Internal_() {
    }
  }

  static final class Implementation extends Owner.First {
    private Implementation() {
    }

    @Override
    public String style() {
      return "jcss-com-example-interfaces-Owner_0024_First";
    }

    @Override
    public String success() {
      return "jcss-com-example-interfaces-Owner_0024_First jcss-com-example-interfaces-Owner_0024_First--success";
    }
  }
}
