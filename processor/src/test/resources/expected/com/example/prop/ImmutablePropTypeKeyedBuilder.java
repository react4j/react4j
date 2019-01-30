package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypeKeyedBuilder {
  private ImmutablePropTypeKeyedBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeKeyed.KeyedComponent myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyed.KeyedComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeKeyed.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeKeyed.KeyedComponent myProp) {
      _element.setKey( myProp.getKey() );
      _element.props().set( React4j_ImmutablePropTypeKeyed.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
