package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
public final class PublicReactComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_PublicReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_PublicReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_PublicReactComponent> componentConstructor = React_PublicReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "PublicReactComponent" );
    }
    return componentConstructor;
  }
}
