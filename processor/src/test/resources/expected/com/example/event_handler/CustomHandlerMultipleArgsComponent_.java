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
final class CustomHandlerMultipleArgsComponent_ {
  public static final JsConstructorFn<React_CustomHandlerMultipleArgsComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomHandlerMultipleArgsComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomHandlerMultipleArgsComponent> constructorFn = JsConstructorFn.of( React_CustomHandlerMultipleArgsComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for CustomHandlerMultipleArgsComponent defined by class com.example.event_handler.CustomHandlerMultipleArgsComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "CustomHandlerMultipleArgsComponent" );
    }
    return constructorFn;
  }

  @Nonnull
  static CustomHandlerMultipleArgsComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerMultipleArgsComponent component) {
    final CustomHandlerMultipleArgsComponent.CustomHandler handler = (arg0,arg1) -> component.handleFoo(arg0,arg1);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerMultipleArgsComponent.handleFoo" ) ) );
    }
    return handler;
  }
}
