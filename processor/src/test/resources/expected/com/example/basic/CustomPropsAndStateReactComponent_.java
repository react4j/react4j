package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class CustomPropsAndStateReactComponent_ {
  public static final JsConstructorFn<React_CustomPropsAndStateReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_CustomPropsAndStateReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_CustomPropsAndStateReactComponent> constructorFn = JsConstructorFn.of( React_CustomPropsAndStateReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for CustomPropsAndStateReactComponent defined by class com.example.basic.CustomPropsAndStateReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return constructorFn;
  }
}
