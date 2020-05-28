package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezComponentWhereIdFromComponentIdBuilder {
  private ImmutablePropTypeArezComponentWhereIdFromComponentIdBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(
      final ImmutablePropTypeArezComponentWhereIdFromComponentId.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeArezComponentWhereIdFromComponentId.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeArezComponentWhereIdFromComponentId.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeArezComponentWhereIdFromComponentId.MyComponent myProp) {
      _element.setKey( Identifiable.<Object>getArezId( myProp ) + ( React.enableComponentNames() ? "_ImmutablePropTypeArezComponentWhereIdFromComponentId_130dc5d5" : ImmutablePropTypeArezComponentWhereIdFromComponentId.class.getName() ) );
      _element.props().set( React4j_ImmutablePropTypeArezComponentWhereIdFromComponentId.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
