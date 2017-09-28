package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class NestedNestedReactComponent$DeepNesting$BasicReactComponent_ {
  public static final JsConstructorFn<NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> getConstrutorFunction() {
    final JsConstructorFn<NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> constructorFn = JsConstructorFn.of( NestedNestedReactComponent$DeepNesting$React_BasicReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for BasicReactComponent defined by class com.example.nested.NestedNestedReactComponent.DeepNesting.BasicReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "BasicReactComponent" );
    }
    return constructorFn;
  }
}
