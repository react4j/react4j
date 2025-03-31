package com.example.prop;

import akasha.lang.JsArray;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NonnullChildPropComponentBuilder {
  private NonnullChildPropComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode child(@Nonnull final ReactNode child) {
    return newBuilder().child( child );
  }

  public interface Step1 {
    @Nonnull
    ReactNode child(@Nonnull ReactNode child);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonnullChildPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode child(@Nonnull final ReactNode child) {
      _element.input( React4j_NonnullChildPropComponent.Inputs.child, JsArray.of( Objects.requireNonNull( child ) ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
