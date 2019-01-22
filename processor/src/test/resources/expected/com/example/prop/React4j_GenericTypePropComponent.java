package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_GenericTypePropComponent<T> extends GenericTypePropComponent<T> {
  React4j_GenericTypePropComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypePropComponent" );
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

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypePropComponent<T>> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypePropComponent<T> createComponent() {
      return new React4j_GenericTypePropComponent<T>( this );
    }
  }
}
