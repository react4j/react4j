package com.example.callback;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomTypeMultipleArgs_ extends CustomTypeMultipleArgs {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final CustomTypeMultipleArgs.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomTypeMultipleArgs" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomTypeMultipleArgs.CustomHandler _handleFoo(@Nonnull final CustomTypeMultipleArgs component) {
    return ((CustomTypeMultipleArgs_) component)._handleFoo;
  }

  @Nonnull
  private CustomTypeMultipleArgs.CustomHandler create_handleFoo() {
    final CustomTypeMultipleArgs.CustomHandler handler = (arg0,arg1) -> this.handleFoo(arg0,arg1);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomTypeMultipleArgs.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, CustomTypeMultipleArgs> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomTypeMultipleArgs createComponent() {
      return new CustomTypeMultipleArgs_();
    }
  }
}
