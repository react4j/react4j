package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NullablePropAndNonnullChildComponentBuilder {
  private NullablePropAndNonnullChildComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myProp(@Nonnull final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(@Nonnull String myProp);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp2(@Nullable String myProp2);

    @Nonnull
    ReactNode child(ReactNode child);
  }

  public interface Step3 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NullablePropAndNonnullChildComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp2(@Nullable final String myProp2) {
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
