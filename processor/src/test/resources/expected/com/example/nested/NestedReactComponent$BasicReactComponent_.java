package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class NestedReactComponent$BasicReactComponent_ {
  public static final JsConstructorFn<NestedReactComponent$React_BasicReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<NestedReactComponent$React_BasicReactComponent> getConstrutorFunction() {
    final JsConstructorFn<NestedReactComponent$React_BasicReactComponent> constructorFn = JsConstructorFn.of( NestedReactComponent$React_BasicReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicReactComponent defined by class com.example.nested.NestedReactComponent.BasicReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicReactComponent" );
    }
    return constructorFn;
  }
}
