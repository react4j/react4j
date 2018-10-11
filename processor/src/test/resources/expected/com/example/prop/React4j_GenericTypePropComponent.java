package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_GenericTypePropComponent<T> extends GenericTypePropComponent<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypePropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value ) ? props().getAny( PROP_value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value ) );
    }
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0);
  }

  private static final class LiteNativeReactComponent<T> extends NativeAdapterComponent<GenericTypePropComponent<T>> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypePropComponent<T> createComponent() {
      return new React4j_GenericTypePropComponent<T>();
    }
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypePropComponent<T>> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypePropComponent<T> createComponent() {
      return new React4j_GenericTypePropComponent<T>();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      performComponentDidUpdate(arg0);
    }
  }
}
