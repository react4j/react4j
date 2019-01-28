package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypesBuilder {
  private ImmutablePropTypesBuilder() {
  }

  @Nonnull
  static Step2 myProp(final ImmutablePropTypes.MyComponent myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(ImmutablePropTypes.MyComponent myProp);
  }

  public interface Step2 {
    @Nonnull
    Step3 myOtherProp(String myOtherProp);
  }

  public interface Step3 {
    @Nonnull
    Step4 stillAnotherProp(int stillAnotherProp);
  }

  public interface Step4 {
    @Nonnull
    ReactNode BobsProp(ImmutablePropTypes.KeyedComponent BobsProp);
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypes.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(final ImmutablePropTypes.MyComponent myProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myOtherProp(final String myOtherProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.myOtherProp, myOtherProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step4 stillAnotherProp(final int stillAnotherProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.stillAnotherProp, stillAnotherProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode BobsProp(final ImmutablePropTypes.KeyedComponent BobsProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.BobsProp, BobsProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.setKey( String.valueOf( Identifiable.<Object>getArezId( _element.props().get( React4j_ImmutablePropTypes.Props.myProp ) ) ) + "-" + _element.props().get( React4j_ImmutablePropTypes.Props.myOtherProp ) + "-" + String.valueOf( _element.props().get( React4j_ImmutablePropTypes.Props.stillAnotherProp ) ) + "-" + ( (Keyed) _element.props().get( React4j_ImmutablePropTypes.Props.BobsProp ) ).getKey() );
      _element.complete();
      return _element;
    }
  }
}
