package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeActAsComponentAndKeyedBuilder {
  private ImmutablePropTypeActAsComponentAndKeyedBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode myProp(
      @Nullable final ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myProp(@Nullable ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeActAsComponentAndKeyed.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myProp(
        @Nullable final ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp) {
      _element.setKey( Keyed.getKey( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeActAsComponentAndKeyed_fae2f037" : ImmutablePropTypeActAsComponentAndKeyed.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeActAsComponentAndKeyed.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
