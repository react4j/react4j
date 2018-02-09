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
class CustomHandlerMultipleArgsComponent_ extends CustomHandlerMultipleArgsComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final CustomHandlerMultipleArgsComponent.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomHandlerMultipleArgsComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomHandlerMultipleArgsComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerMultipleArgsComponent component) {
    return ((CustomHandlerMultipleArgsComponent_) component)._handleFoo;
  }

  @Nonnull
  private CustomHandlerMultipleArgsComponent.CustomHandler create_handleFoo() {
    final CustomHandlerMultipleArgsComponent.CustomHandler handler = (arg0,arg1) -> this.handleFoo(arg0,arg1);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerMultipleArgsComponent.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, CustomHandlerMultipleArgsComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomHandlerMultipleArgsComponent createComponent() {
      return new CustomHandlerMultipleArgsComponent_();
    }
  }
}
