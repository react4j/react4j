package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class BasicReactComponent_ {
  public static final JsConstructorFn<React_BasicReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_BasicReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_BasicReactComponent> constructorFn = JsConstructorFn.of( React_BasicReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicReactComponent defined by class com.example.basic.BasicReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicReactComponent" );
    }
    return constructorFn;
  }
}
