package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class CustomNameReactComponent_ {
  public static final JsConstructorFn<React_CustomNameReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomNameReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomNameReactComponent> constructorFn = JsConstructorFn.of( React_CustomNameReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for ZANG defined by class com.example.basic.CustomNameReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "ZANG" );
    }
    return constructorFn;
  }
}
