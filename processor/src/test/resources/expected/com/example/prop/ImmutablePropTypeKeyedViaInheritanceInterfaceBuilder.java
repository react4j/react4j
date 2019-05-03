package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypeKeyedViaInheritanceInterfaceBuilder {
  private ImmutablePropTypeKeyedViaInheritanceInterfaceBuilder() {
  }

  @Nonnull
  static ReactNode myProp(
      final ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeKeyedViaInheritanceInterface.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp) {
      _element.setKey( ImmutablePropTypeKeyedViaInheritanceInterface.class.getName() + Keyed.getKey( myProp ) );
      _element.props().set( React4j_ImmutablePropTypeKeyedViaInheritanceInterface.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
