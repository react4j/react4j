package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class OverridingComponentDidUpdateComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_OverridingComponentDidUpdateComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_OverridingComponentDidUpdateComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_OverridingComponentDidUpdateComponent> componentConstructor = React_OverridingComponentDidUpdateComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "OverridingComponentDidUpdateComponent" );
    }
    return componentConstructor;
  }
}
