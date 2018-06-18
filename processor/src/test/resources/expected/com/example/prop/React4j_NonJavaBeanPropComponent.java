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
class React4j_NonJavaBeanPropComponent extends NonJavaBeanPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

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
    return null != props().getAny( "window" ) ? props().getAny( "window" ).asString() : null;
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
