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
    name = "BasicArezReactComponent",
    singleton = false,
    disposable = true,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
class BasicArezReactComponent_ extends BasicArezReactComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> componentConstructor = React_BasicArezReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicArezReactComponent" );
    }
    return componentConstructor;
  }
}
