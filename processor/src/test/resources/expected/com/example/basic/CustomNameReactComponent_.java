package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;
import react4j.core.util.JsUtil;

@Generated("react4j.processor.ReactProcessor")
final class CustomNameReactComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> getConstructorFunction() {
    final JsPropertyMapOfAny prototype = JsUtil.getPrototypeForClass( React_CustomNameReactComponent.class );
    final ComponentConstructorFunction<BaseProps, BaseState, React_CustomNameReactComponent> componentConstructor = React_CustomNameReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ZANG" );
    }
    return componentConstructor;
  }
}
