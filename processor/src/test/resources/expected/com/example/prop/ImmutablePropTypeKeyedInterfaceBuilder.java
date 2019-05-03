package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypeKeyedInterfaceBuilder {
  private ImmutablePropTypeKeyedInterfaceBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeKeyedInterface.KeyedInterface myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyedInterface.KeyedInterface myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeKeyedInterface.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeKeyedInterface.KeyedInterface myProp) {
      _element.setKey( ImmutablePropTypeKeyedInterface.class.getName() + Keyed.getKey( myProp ) );
      _element.props().set( React4j_ImmutablePropTypeKeyedInterface.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
