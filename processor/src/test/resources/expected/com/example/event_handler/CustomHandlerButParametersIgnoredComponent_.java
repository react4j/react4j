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
final class CustomHandlerButParametersIgnoredComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerButParametersIgnoredComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerButParametersIgnoredComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_CustomHandlerButParametersIgnoredComponent> componentConstructor = React_CustomHandlerButParametersIgnoredComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomHandlerButParametersIgnoredComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomHandlerButParametersIgnoredComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerButParametersIgnoredComponent component) {
    final CustomHandlerButParametersIgnoredComponent.CustomHandler handler = arg0 -> component.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerButParametersIgnoredComponent.handleFoo" ) ) );
    }
    return handler;
  }
}
