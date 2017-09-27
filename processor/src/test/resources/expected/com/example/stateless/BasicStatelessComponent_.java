package com.example.stateless;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class BasicStatelessComponent_ {
  public static final JsConstructorFn<React_BasicStatelessComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_BasicStatelessComponent> getConstrutorFunction() {
    final JsConstructorFn<React_BasicStatelessComponent> constructorFn = JsConstructorFn.of( React_BasicStatelessComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicStatelessComponent defined by class com.example.stateless.BasicStatelessComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicStatelessComponent" );
    }
    return constructorFn;
  }
}
