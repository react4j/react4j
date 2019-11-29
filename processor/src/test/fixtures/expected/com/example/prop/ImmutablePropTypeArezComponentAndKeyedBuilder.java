package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezComponentAndKeyedBuilder {
  private ImmutablePropTypeArezComponentAndKeyedBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeArezComponentAndKeyed.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp) {
      _element.setKey( ImmutablePropTypeArezComponentAndKeyed.class.getName() + Keyed.getKey( myProp ) );
      _element.props().set( React4j_ImmutablePropTypeArezComponentAndKeyed.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
