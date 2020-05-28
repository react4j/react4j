package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeActAsComponentAndKeyedBuilder {
  private ImmutablePropTypeActAsComponentAndKeyedBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeActAsComponentAndKeyed.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeActAsComponentAndKeyed.MyComponent myProp) {
      _element.setKey( Keyed.getKey( myProp ) + ( React.enableComponentNames() ? "_ImmutablePropTypeActAsComponentAndKeyed_fae2f037" : ImmutablePropTypeActAsComponentAndKeyed.class.getName() ) );
      _element.props().set( React4j_ImmutablePropTypeActAsComponentAndKeyed.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
