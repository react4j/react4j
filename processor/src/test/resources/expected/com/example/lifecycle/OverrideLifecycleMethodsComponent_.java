package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class OverrideLifecycleMethodsComponent_ {
  public static final JsConstructorFn<React_OverrideLifecycleMethodsComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_OverrideLifecycleMethodsComponent> getConstrutorFunction() {
    final JsConstructorFn<React_OverrideLifecycleMethodsComponent> constructorFn = JsConstructorFn.of( React_OverrideLifecycleMethodsComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for OverrideLifecycleMethodsComponent defined by class com.example.lifecycle.OverrideLifecycleMethodsComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return constructorFn;
  }
}
