package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_BasicReactComponent extends BasicReactComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BasicReactComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicReactComponent createComponent() {
      return new React4j_BasicReactComponent();
    }
  }
}
