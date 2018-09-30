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
class React4j_NonJavaBeanPropComponent extends NonJavaBeanPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_window = ReactConfig.shouldMinimizePropKeys() ? "a" : "window";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NonJavaBeanPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected String window() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_window ) ? props().getAny( PROP_window ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_window ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NonJavaBeanPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NonJavaBeanPropComponent createComponent() {
      return new React4j_NonJavaBeanPropComponent();
    }
  }
}
