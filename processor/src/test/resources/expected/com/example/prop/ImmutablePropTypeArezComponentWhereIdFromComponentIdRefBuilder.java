package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class ImmutablePropTypeArezComponentWhereIdFromComponentIdRefBuilder {
  private ImmutablePropTypeArezComponentWhereIdFromComponentIdRefBuilder() {
  }

  @Nonnull
  static ReactNode myProp(
      final ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.MyComponent myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.MyComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.MyComponent myProp) {
      _element.setKey( ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.class.getName() + Identifiable.<Object>getArezId( myProp ) );
      _element.props().set( React4j_ImmutablePropTypeArezComponentWhereIdFromComponentIdRef.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
