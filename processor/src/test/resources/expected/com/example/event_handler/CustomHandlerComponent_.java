package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class CustomHandlerComponent_ {
  public static final JsConstructorFn<React_CustomHandlerComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomHandlerComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomHandlerComponent> constructorFn = JsConstructorFn.of( React_CustomHandlerComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for CustomHandlerComponent defined by class com.example.event_handler.CustomHandlerComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "CustomHandlerComponent" );
    }
    return constructorFn;
  }

  @Nonnull
  static CustomHandlerComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerComponent component) {
    final CustomHandlerComponent.CustomHandler handler = arg0 -> component.handleFoo(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerComponent.handleFoo" ) ) );
    }
    return handler;
  }
}
