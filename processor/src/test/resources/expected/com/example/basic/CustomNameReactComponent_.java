package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomNameReactComponent_ extends CustomNameReactComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> componentConstructor = React_CustomNameReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ZANG" );
    }
    return componentConstructor;
  }
}
