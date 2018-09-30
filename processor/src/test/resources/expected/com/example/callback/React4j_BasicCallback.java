package com.example.callback;

import elemental2.core.JsObject;
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
import react4j.annotations.Callback;

@Generated("react4j.processor.ReactProcessor")
class React4j_BasicCallback extends BasicCallback {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final Callback.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicCallback" );
    }
    return componentConstructor;
  }

  @Nonnull
  private Callback.Procedure create_handleFoo() {
    final Callback.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "BasicCallback.handleFoo" ) ) );
    }
    return handler;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BasicCallback> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicCallback createComponent() {
      return new React4j_BasicCallback();
    }
  }
}
