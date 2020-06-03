package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeActAsComponentInterfaceBuilder {
  private ImmutablePropTypeActAsComponentInterfaceBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeActAsComponentInterface.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeActAsComponentInterface.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeActAsComponentInterface.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeActAsComponentInterface.MyComponent myProp) {
      _element.setKey( Identifiable.<Object>getArezId( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeActAsComponentInterface_56c176ed" : ImmutablePropTypeActAsComponentInterface.class.getName() ) );
      _element.props().set( React4j_ImmutablePropTypeActAsComponentInterface.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
