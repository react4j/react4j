package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomHandlerMultipleArgsComponent_ extends CustomHandlerMultipleArgsComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerMultipleArgsComponent> TYPE = getConstructorFunction();

  @Nonnull
  private final CustomHandlerMultipleArgsComponent.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerMultipleArgsComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerMultipleArgsComponent> componentConstructor = React_CustomHandlerMultipleArgsComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomHandlerMultipleArgsComponent" );
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
}
