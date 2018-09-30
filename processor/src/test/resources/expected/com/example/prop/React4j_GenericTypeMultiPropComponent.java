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

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypeMultiPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    return Js.<Props<T>>uncheckedCast( props() ).value;
  }

  @Override
  protected String getValue2() {
    return Js.<Props<T>>uncheckedCast( props() ).value2;
  }

  @Nullable
  @Override
  protected String getValue3() {
    return Js.<Props<T>>uncheckedCast( props() ).value3;
  }

  @Nullable
  @Override
  protected String getValue4() {
    return Js.<Props<T>>uncheckedCast( props() ).value4;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props<T> {
    @Nullable
    Object key;

    T value;

    String value2;

    @Nullable
    String value3;

    @Nullable
    String value4;
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypeMultiPropComponent<T>> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypeMultiPropComponent<T> createComponent() {
      return new React4j_GenericTypeMultiPropComponent<T>();
    }
  }
}
