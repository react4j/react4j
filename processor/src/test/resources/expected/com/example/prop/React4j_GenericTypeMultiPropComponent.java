package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "GenericTypeMultiPropComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_GenericTypeMultiPropComponent<T> extends GenericTypeMultiPropComponent<T> {
  React4j_GenericTypeMultiPropComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypeMultiPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value ) ? props().getAny( Props.value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value ) );
    }
  }

  @Override
  protected String getValue2() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value2 ) ? props().getAny( Props.value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value2 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue3() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value3 ) ? props().getAny( Props.value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value3 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue4() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value4 ) ? props().getAny( Props.value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value4 ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";

    static final String value2 = React.shouldMinimizePropKeys() ? "b" : "value2";

    static final String value3 = React.shouldMinimizePropKeys() ? "c" : "value3";

    static final String value4 = React.shouldMinimizePropKeys() ? "d" : "value4";
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypeMultiPropComponent<T>> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypeMultiPropComponent<T> createComponent() {
      return new Arez_React4j_GenericTypeMultiPropComponent<T>( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_GenericTypeMultiPropComponent) component() ).render();
    }
  }
}
