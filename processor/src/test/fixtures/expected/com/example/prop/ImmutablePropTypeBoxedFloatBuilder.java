package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeBoxedFloatBuilder {
  private ImmutablePropTypeBoxedFloatBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final Float myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(Float myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeBoxedFloat.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final Float myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypeBoxedFloat_0dca3d37" : ImmutablePropTypeBoxedFloat.class.getName() ) );
      _element.props().set( React4j_ImmutablePropTypeBoxedFloat.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
