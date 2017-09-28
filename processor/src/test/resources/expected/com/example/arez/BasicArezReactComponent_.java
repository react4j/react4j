package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class BasicArezReactComponent_ {
  public static final JsConstructorFn<React_BasicArezReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_BasicArezReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_BasicArezReactComponent> constructorFn = JsConstructorFn.of( React_BasicArezReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicArezReactComponent defined by class com.example.arez.BasicArezReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicArezReactComponent" );
    }
    return constructorFn;
  }
}
