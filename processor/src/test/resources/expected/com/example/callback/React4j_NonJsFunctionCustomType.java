package com.example.callback;

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
class React4j_NonJsFunctionCustomType extends NonJsFunctionCustomType {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final NonJsFunctionCustomType.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NonJsFunctionCustomType" );
    }
    return componentConstructor;
  }

  @Nonnull
  private NonJsFunctionCustomType.CustomHandler create_handleFoo() {
    final NonJsFunctionCustomType.CustomHandler handler = arg0 -> this.handleFoo(arg0);
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NonJsFunctionCustomType> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NonJsFunctionCustomType createComponent() {
      return new React4j_NonJsFunctionCustomType();
    }
  }
}
