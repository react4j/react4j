package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.Procedure;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class BasicHandlerComponent_ {
  public static final JsConstructorFn<React_BasicHandlerComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_BasicHandlerComponent> getConstrutorFunction() {
    final JsConstructorFn<React_BasicHandlerComponent> constructorFn = JsConstructorFn.of( React_BasicHandlerComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicHandlerComponent defined by class com.example.event_handler.BasicHandlerComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicHandlerComponent" );
    }
    return constructorFn;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final BasicHandlerComponent component) {
    final Procedure handler = () -> component.handleFoo();
    JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "BasicHandlerComponent.handleFoo" ) ) );
    return handler;
  }
}
