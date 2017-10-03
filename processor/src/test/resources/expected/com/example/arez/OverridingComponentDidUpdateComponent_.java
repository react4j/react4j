package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class OverridingComponentDidUpdateComponent_ {
  public static final JsConstructorFn<React_OverridingComponentDidUpdateComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_OverridingComponentDidUpdateComponent> getConstrutorFunction() {
    final JsConstructorFn<React_OverridingComponentDidUpdateComponent> constructorFn = JsConstructorFn.of( React_OverridingComponentDidUpdateComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for OverridingComponentDidUpdateComponent defined by class com.example.arez.OverridingComponentDidUpdateComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "OverridingComponentDidUpdateComponent" );
    }
    return constructorFn;
  }
}
