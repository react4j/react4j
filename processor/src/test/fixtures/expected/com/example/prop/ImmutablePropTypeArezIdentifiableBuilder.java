package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezIdentifiableBuilder {
  private ImmutablePropTypeArezIdentifiableBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeArezIdentifiable.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeArezIdentifiable.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeArezIdentifiable.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeArezIdentifiable.MyComponent myProp) {
      _element.setKey( Identifiable.<Object>getArezId( myProp ) + ( React.enableViewNames() ? "_ImmutablePropTypeArezIdentifiable_30200f60" : ImmutablePropTypeArezIdentifiable.class.getName() ) );
      _element.props().set( React4j_ImmutablePropTypeArezIdentifiable.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
