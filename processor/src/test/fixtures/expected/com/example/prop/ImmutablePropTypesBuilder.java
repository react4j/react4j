package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypesBuilder {
  private ImmutablePropTypesBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myProp(final ImmutablePropTypes.MyComponent myProp) {
    return newBuilder().myProp( myProp );
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
    Step5 BobsProp(ImmutablePropTypes.KeyedComponent BobsProp);
  }

  public interface Step5 {
    @Nonnull
    ReactNode someProp(ImmutablePropTypes.Foo someProp);
  }

  private static class Builder implements Step1, Step2, Step3, Step4, Step5 {
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
    public final Step5 BobsProp(final ImmutablePropTypes.KeyedComponent BobsProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.BobsProp, BobsProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode someProp(final ImmutablePropTypes.Foo someProp) {
      _element.props().set( React4j_ImmutablePropTypes.Props.someProp, someProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      final JsPropertyMap<Object> props = _element.props();
      _element.setKey( ( React.enableComponentNames() ? "_ImmutablePropTypes_108bb2eb" : ImmutablePropTypes.class.getName() ) + "-" + String.valueOf( Identifiable.<Object>getArezId( (ImmutablePropTypes.MyComponent) props.get( React4j_ImmutablePropTypes.Props.myProp ) ) ) + "-" + ( (String) props.get( React4j_ImmutablePropTypes.Props.myOtherProp ) ) + "-" + String.valueOf( (int) props.get( React4j_ImmutablePropTypes.Props.stillAnotherProp ) ) + "-" + Keyed.getKey( (ImmutablePropTypes.KeyedComponent) props.get( React4j_ImmutablePropTypes.Props.BobsProp ) ) + "-" + ( (ImmutablePropTypes.Foo) props.get( React4j_ImmutablePropTypes.Props.someProp ) ) );
      _element.complete();
      return _element;
    }
  }
}
