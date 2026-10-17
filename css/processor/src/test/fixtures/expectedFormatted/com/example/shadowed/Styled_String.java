package com.example.shadowed;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@SuppressWarnings("UnnecessarilyFullyQualified")
@NullMarked
final class Styled_String extends String {
  private Styled_String() {}

  static String get() {
    return Styled_String.Internal.INSTANCE;
  }

  @java.lang.Override
  java.lang.String style() {
    return "jcss-com-example-shadowed-String";
  }

  private static final class Internal {
    private static final String INSTANCE = new Styled_String();

    private Internal() {}
  }
}
