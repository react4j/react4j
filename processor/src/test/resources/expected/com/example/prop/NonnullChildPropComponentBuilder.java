package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NonnullChildPropComponentBuilder {
  private NonnullChildPropComponentBuilder() {
  }

  @Nonnull
  static ReactNode child(@Nonnull final ReactNode child) {
    return new Builder().child( child );
  }

  public interface Step1 {
    @Nonnull
    ReactNode child(@Nonnull ReactNode child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NonnullChildPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode child(@Nonnull final ReactNode child) {
      _element.props().set( React4j_NonnullChildPropComponent.Props.child, JsArray.of( Objects.requireNonNull( child ) ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
