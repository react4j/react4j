package com.example.shadowed;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@SuppressWarnings("UnnecessarilyFullyQualified")
@NullMarked
final class Styled_Override extends Override {
  private Styled_Override() {
  }

  static Override get() {
    return Internal.INSTANCE;
  }

  @java.lang.Override
  java.lang.String style() {
    return "jcss-com-example-shadowed-Override";
  }

  private static final class Internal {
    private static final Override INSTANCE = new Styled_Override();

    private Internal() {
    }
  }
}
