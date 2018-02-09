package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.EventHandler;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomNameComponent_ extends CustomNameComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final EventHandler.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomNameComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static EventHandler.Procedure _handleFoo(@Nonnull final CustomNameComponent component) {
    return ((CustomNameComponent_) component)._handleFoo;
  }

  @Nonnull
  private EventHandler.Procedure create_handleFoo() {
    final EventHandler.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomNameComponent.fox" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, CustomNameComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomNameComponent createComponent() {
      return new CustomNameComponent_();
    }
  }
}
