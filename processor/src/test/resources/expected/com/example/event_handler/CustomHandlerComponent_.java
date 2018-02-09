package com.example.event_handler;

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
class CustomHandlerComponent_ extends CustomHandlerComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final CustomHandlerComponent.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomHandlerComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomHandlerComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerComponent component) {
    return ((CustomHandlerComponent_) component)._handleFoo;
  }

  @Nonnull
  private CustomHandlerComponent.CustomHandler create_handleFoo() {
    final CustomHandlerComponent.CustomHandler handler = arg0 -> this.handleFoo(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerComponent.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, CustomHandlerComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomHandlerComponent createComponent() {
      return new CustomHandlerComponent_();
    }
  }
}
