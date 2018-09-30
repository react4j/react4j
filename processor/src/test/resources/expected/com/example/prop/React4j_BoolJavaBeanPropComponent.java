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
class React4j_BoolJavaBeanPropComponent extends BoolJavaBeanPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_foo = ReactConfig.shouldMinimizePropKeys() ? "a" : "foo";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BoolJavaBeanPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean isFoo() {
    return props().getAny( PROP_foo ).asBoolean();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BoolJavaBeanPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BoolJavaBeanPropComponent createComponent() {
      return new React4j_BoolJavaBeanPropComponent();
    }
  }
}
