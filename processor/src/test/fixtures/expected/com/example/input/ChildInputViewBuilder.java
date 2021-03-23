package com.example.input;

import akasha.lang.JsArray;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ChildInputViewBuilder {
  private ChildInputViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode child(final ReactNode child) {
    return newBuilder().child( child );
  }

  public interface Step1 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ChildInputView.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.input( React4j_ChildInputView.Inputs.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
