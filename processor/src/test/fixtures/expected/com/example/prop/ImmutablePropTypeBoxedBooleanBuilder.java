package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeBoxedBooleanBuilder {
  private ImmutablePropTypeBoxedBooleanBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final Boolean myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(Boolean myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeBoxedBoolean.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final Boolean myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypeBoxedBoolean_c8609c6b" : ImmutablePropTypeBoxedBoolean.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeBoxedBoolean.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
