package com.example.symbols;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Owner_Styled_Page extends Owner.Page {
  private Owner_Styled_Page() {
  }

  public static Owner.Page get() {
    return Internal.INSTANCE;
  }

  @Override
  public String accent() {
    return "--jcss-com-example-symbols-Owner_0024_Page--accent";
  }

  @Override
  public String page() {
    return "jcss-com-example-symbols-Owner_0024_Page--id-page";
  }

  private static final class Internal {
    private static final Owner.Page INSTANCE = new Owner_Styled_Page();

    private Internal() {
    }
  }
}
