package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeStringBuilder {
  private ImmutablePropTypeStringBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(String myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeString.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypeString_e57e44c1" : ImmutablePropTypeString.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeString.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
