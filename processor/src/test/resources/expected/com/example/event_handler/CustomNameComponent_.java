package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.common.Procedure;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class CustomNameComponent_ {
  public static final JsConstructorFn<React_CustomNameComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomNameComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomNameComponent> constructorFn = JsConstructorFn.of( React_CustomNameComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for CustomNameComponent defined by class com.example.event_handler.CustomNameComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "CustomNameComponent" );
    }
    return constructorFn;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final CustomNameComponent component) {
    final Procedure handler = () -> component.handleFoo();
    JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomNameComponent.fox" ) ) );
    return handler;
  }
}
