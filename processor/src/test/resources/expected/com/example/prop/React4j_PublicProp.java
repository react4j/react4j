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
class React4j_PublicProp extends PublicProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PublicProp" );
    }
    return componentConstructor;
  }

  @Override
  public String getMyProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp ) ? props().getAny( PROP_myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PublicProp> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PublicProp createComponent() {
      return new React4j_PublicProp();
    }
  }
}
