package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class CustomHandlerButParametersIgnoredComponent_ {
  public static final JsConstructorFn<React_CustomHandlerButParametersIgnoredComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomHandlerButParametersIgnoredComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomHandlerButParametersIgnoredComponent> constructorFn = JsConstructorFn.of( React_CustomHandlerButParametersIgnoredComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for CustomHandlerButParametersIgnoredComponent defined by class com.example.event_handler.CustomHandlerButParametersIgnoredComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "CustomHandlerButParametersIgnoredComponent" );
    }
    return constructorFn;
  }

  @Nonnull
  static CustomHandlerButParametersIgnoredComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerButParametersIgnoredComponent component) {
    final CustomHandlerButParametersIgnoredComponent.CustomHandler handler = arg0 -> component.handleFoo();
    JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerButParametersIgnoredComponent.handleFoo" ) ) );
    return handler;
  }
}
