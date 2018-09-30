package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_PropTypeLong extends PropTypeLong {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropTypeLong" );
    }
    return componentConstructor;
  }

  @Override
  protected long getMyProp() {
    return props().getAny( PROP_myProp ).asLong();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PropTypeLong> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropTypeLong createComponent() {
      return new React4j_PropTypeLong();
    }
  }
}
