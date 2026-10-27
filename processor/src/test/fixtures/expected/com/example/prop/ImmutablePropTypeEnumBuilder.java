package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeEnumBuilder {
  private ImmutablePropTypeEnumBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode myProp(final ImmutablePropTypeEnum. @Nullable Foo myProp) {
    return newBuilder().myProp( myProp );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode myProp(ImmutablePropTypeEnum. @Nullable Foo myProp);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeEnum.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp(final ImmutablePropTypeEnum. @Nullable Foo myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypeEnum_00ba1b5e" : ImmutablePropTypeEnum.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeEnum.Inputs.myProp, myProp );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
