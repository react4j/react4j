package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
public final class PublicReactComponent_ {
  public static final JsConstructorFn<React_PublicReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_PublicReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_PublicReactComponent> constructorFn = JsConstructorFn.of( React_PublicReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for PublicReactComponent defined by class com.example.basic.PublicReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "PublicReactComponent" );
    }
    return constructorFn;
  }
}
