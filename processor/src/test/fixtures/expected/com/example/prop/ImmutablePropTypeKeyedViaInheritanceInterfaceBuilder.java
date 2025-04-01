package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeKeyedViaInheritanceInterfaceBuilder {
  private ImmutablePropTypeKeyedViaInheritanceInterfaceBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(
      final ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeKeyedViaInheritanceInterface.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeKeyedViaInheritanceInterface.KeyedInterface myProp) {
      _element.setKey( Keyed.getKey( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeKeyedViaInheritanceInterface_4cd25d5b" : ImmutablePropTypeKeyedViaInheritanceInterface.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeKeyedViaInheritanceInterface.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
