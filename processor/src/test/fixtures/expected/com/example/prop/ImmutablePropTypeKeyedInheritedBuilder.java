package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeKeyedInheritedBuilder {
  private ImmutablePropTypeKeyedInheritedBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeKeyedInherited.KeyedComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyedInherited.KeyedComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeKeyedInherited.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeKeyedInherited.KeyedComponent myProp) {
      _element.setKey( Keyed.getKey( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeKeyedInherited_62ec04af" : ImmutablePropTypeKeyedInherited.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeKeyedInherited.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
