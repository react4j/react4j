package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class NestedNestedReactComponent$DeepNesting$BasicReactComponent_ extends NestedNestedReactComponent.DeepNesting.BasicReactComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NestedNestedReactComponent$DeepNesting$React_BasicReactComponent> componentConstructor = NestedNestedReactComponent$DeepNesting$React_BasicReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }
}
