package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
final class CustomPropsAndStateReactComponent_ {
  public static final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, React_CustomPropsAndStateReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, React_CustomPropsAndStateReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, React_CustomPropsAndStateReactComponent> componentConstructor = React_CustomPropsAndStateReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return componentConstructor;
  }
}
