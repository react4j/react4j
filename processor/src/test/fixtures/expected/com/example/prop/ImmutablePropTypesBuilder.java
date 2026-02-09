package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
  static Step2 stillAnotherProp(final int stillAnotherProp) {
    return newBuilder().stillAnotherProp( stillAnotherProp );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 stillAnotherProp(int stillAnotherProp);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp(@Nullable ImmutablePropTypes.MyComponent myProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myOtherProp(@Nullable String myOtherProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step2 BobsProp(@Nullable ImmutablePropTypes.KeyedComponent BobsProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step2 someProp(@Nullable ImmutablePropTypes.Foo someProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step2 object(@Nullable Object object);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypes.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 stillAnotherProp(final int stillAnotherProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.stillAnotherProp, stillAnotherProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp(@Nullable final ImmutablePropTypes.MyComponent myProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myOtherProp(@Nullable final String myOtherProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.myOtherProp, myOtherProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 BobsProp(@Nullable final ImmutablePropTypes.KeyedComponent BobsProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.BobsProp, BobsProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 someProp(@Nullable final ImmutablePropTypes.Foo someProp) {
      _element.input( React4j_ImmutablePropTypes.Inputs.someProp, someProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 object(@Nullable final Object object) {
      _element.input( React4j_ImmutablePropTypes.Inputs.object, object );
      return this;
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<Object> inputs = _element.inputs();
      final Object $object$ = inputs.get( React4j_ImmutablePropTypes.Inputs.object );
      _element.setKey( String.valueOf( (int) (double) inputs.get( React4j_ImmutablePropTypes.Inputs.stillAnotherProp ) ) + "-" + String.valueOf( Identifiable.<Object>getArezId( (ImmutablePropTypes.MyComponent) inputs.get( React4j_ImmutablePropTypes.Inputs.myProp ) ) ) + "-" + ( (String) inputs.get( React4j_ImmutablePropTypes.Inputs.myOtherProp ) ) + "-" + Keyed.getKey( (ImmutablePropTypes.KeyedComponent) inputs.get( React4j_ImmutablePropTypes.Inputs.BobsProp ) ) + "-" + ( (ImmutablePropTypes.Foo) inputs.get( React4j_ImmutablePropTypes.Inputs.someProp ) ) + "-" + ( $object$ instanceof Keyed ? Keyed.getKey( $object$ ) : $object$ instanceof Identifiable ? Identifiable.<Object>getArezId( $object$ ) : String.valueOf( $object$ ) ) + ( React.enableViewNames() ? "_ImmutablePropTypes_108bb2eb" : ImmutablePropTypes.class.getName() ) );
      return _element;
    }
  }
}
