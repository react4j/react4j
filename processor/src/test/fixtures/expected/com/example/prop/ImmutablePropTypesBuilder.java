package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
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
  @Contract(
      pure = true
  )
  static Step2 myProp(final ImmutablePropTypes.MyComponent myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp(ImmutablePropTypes.MyComponent myProp);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3 myOtherProp(String myOtherProp);
  }

  public interface Step3 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step4 stillAnotherProp(int stillAnotherProp);
  }

  public interface Step4 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step5 BobsProp(ImmutablePropTypes.KeyedComponent BobsProp);
  }

  public interface Step5 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step6 someProp(ImmutablePropTypes.Foo someProp);
  }

  public interface Step6 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode object(Object object);
  }

  private static class Builder implements Step1, Step2, Step3, Step4, Step5, Step6 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypes.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp(final ImmutablePropTypes.MyComponent myProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3 myOtherProp(final String myOtherProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.myOtherProp, myOtherProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step4 stillAnotherProp(final int stillAnotherProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.stillAnotherProp, stillAnotherProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step5 BobsProp(final ImmutablePropTypes.KeyedComponent BobsProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.BobsProp, BobsProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step6 someProp(final ImmutablePropTypes.Foo someProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.someProp, someProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode object(final Object object) {
      _element.input( React4j_ImmutablePropTypes.Inputs.object, object );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<Object> inputs = _element.inputs();
      final Object $object$ = inputs.get( React4j_ImmutablePropTypes.Inputs.object );
      _element.setKey( String.valueOf( Identifiable.<Object>getArezId( (ImmutablePropTypes.MyComponent) inputs.get( React4j_ImmutablePropTypes.Inputs.myProp ) ) ) + "-" + ( (String) inputs.get( React4j_ImmutablePropTypes.Inputs.myOtherProp ) ) + "-" + String.valueOf( (int) (double) inputs.get( React4j_ImmutablePropTypes.Inputs.stillAnotherProp ) ) + "-" + Keyed.getKey( (ImmutablePropTypes.KeyedComponent) inputs.get( React4j_ImmutablePropTypes.Inputs.BobsProp ) ) + "-" + ( (ImmutablePropTypes.Foo) inputs.get( React4j_ImmutablePropTypes.Inputs.someProp ) ) + "-" + ( $object$ instanceof Keyed ? Keyed.getKey( $object$ ) : $object$ instanceof Identifiable ? Identifiable.<Object>getArezId( $object$ ) : String.valueOf( $object$ ) ) + ( React.enableViewNames() ? "_ImmutablePropTypes_108bb2eb" : ImmutablePropTypes.class.getName() ) );
      return _element;
    }
  }
}
