package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class NullablePropAndNonnullChildComponentBuilder {
  private NullablePropAndNonnullChildComponentBuilder() {
  }

  @Nonnull
  static Step2 myProp(@Nonnull final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(@Nonnull String myProp);
  }

  public interface Step2 {
    @Nonnull
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    ReactNode child(ReactNode child);
  }

  public interface Step3 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NullablePropAndNonnullChildComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.myProp2, myProp2 );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
