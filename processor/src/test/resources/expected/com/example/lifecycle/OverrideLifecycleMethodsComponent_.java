package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class OverrideLifecycleMethodsComponent_ extends OverrideLifecycleMethodsComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> componentConstructor = React_OverrideLifecycleMethodsComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }
}
