package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class NestedReactComponent$BasicReactComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NestedReactComponent$React_BasicReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NestedReactComponent$React_BasicReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NestedReactComponent$React_BasicReactComponent> componentConstructor = NestedReactComponent$React_BasicReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }
}
