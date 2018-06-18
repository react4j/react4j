package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_GenericTypePropComponent<T> extends GenericTypePropComponent<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypePropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    return null != props().getAny( "value" ) ? props().getAny( "value" ).cast() : null;
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypePropComponent<T>> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypePropComponent<T> createComponent() {
      return new React4j_GenericTypePropComponent<T>();
    }
  }
}
