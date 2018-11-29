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
      return null != props().getAny( Props.value ) ? props().getAny( Props.value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value ) );
    }
  }

  @Override
  protected String getValue2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value2 ) ? props().getAny( Props.value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value2 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value3 ) ? props().getAny( Props.value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value3 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue4() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value4 ) ? props().getAny( Props.value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value4 ) );
    }
  }

  void $$react4j$$_componentDidMount() {
    storeDebugDataAsState();
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

    static final String value2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "value2";

    static final String value3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "value3";

    static final String value4 = ReactConfig.shouldMinimizePropKeys() ? "d" : "value4";
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
      ((React4j_GenericTypeMultiPropComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_GenericTypeMultiPropComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }
  }
}
