package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezComponentAndKeyedBuilder {
  private ImmutablePropTypeArezComponentAndKeyedBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeArezComponentAndKeyed.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp) {
      _element.setKey( Keyed.getKey( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeArezComponentAndKeyed_ad8da370" : ImmutablePropTypeArezComponentAndKeyed.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeArezComponentAndKeyed.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
