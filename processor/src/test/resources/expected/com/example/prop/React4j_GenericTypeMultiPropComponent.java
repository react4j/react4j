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
class React4j_GenericTypeMultiPropComponent<T> extends GenericTypeMultiPropComponent<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

  static final String PROP_value2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "value2";

  static final String PROP_value3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "value3";

  static final String PROP_value4 = ReactConfig.shouldMinimizePropKeys() ? "d" : "value4";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypeMultiPropComponent" );
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

  @Override
  protected String getValue2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value2 ) ? props().getAny( PROP_value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value2 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value3 ) ? props().getAny( PROP_value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value3 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue4() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value4 ) ? props().getAny( PROP_value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value4 ) );
    }
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent<T> extends NativeAdapterComponent<GenericTypeMultiPropComponent<T>> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypeMultiPropComponent<T> createComponent() {
      return new React4j_GenericTypeMultiPropComponent<T>();
    }
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypeMultiPropComponent<T>> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypeMultiPropComponent<T> createComponent() {
      return new React4j_GenericTypeMultiPropComponent<T>();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }
}
