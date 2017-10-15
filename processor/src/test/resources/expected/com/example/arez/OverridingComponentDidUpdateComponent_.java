package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@ArezComponent(
    name = "OverridingComponentDidUpdateComponent",
    singleton = false,
    disposable = true,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
class OverridingComponentDidUpdateComponent_ extends OverridingComponentDidUpdateComponent {
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
