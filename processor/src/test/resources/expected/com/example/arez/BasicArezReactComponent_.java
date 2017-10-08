package com.example.arez;

import elemental2.core.JsObject;
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
final class BasicArezReactComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> TYPE = getConstructorFunction();

  private static native JsObject componentDidMount_function()/*-{ return function() { return this.@com.example.arez.React_BasicArezReactComponent::componentDidMount(*)(arguments); } }-*/;

  private static native JsObject componentDidUpdate_function()/*-{ return function() { return this.@com.example.arez.React_BasicArezReactComponent::componentDidUpdate(*)(arguments); } }-*/;

  private static native JsObject shouldComponentUpdate_function()/*-{ return function() { return this.@com.example.arez.React_BasicArezReactComponent::shouldComponentUpdate(*)(arguments); } }-*/;

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> getConstructorFunction() {
    final JsPropertyMapOfAny prototype = JsUtil.getPrototypeForClass( React_BasicArezReactComponent.class );
    prototype.set( "componentDidMount", componentDidMount_function() );
    prototype.set( "componentDidUpdate", componentDidUpdate_function() );
    prototype.set( "shouldComponentUpdate", shouldComponentUpdate_function() );
    final ComponentConstructorFunction<BaseProps, BaseState, React_BasicArezReactComponent> componentConstructor = React_BasicArezReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicArezReactComponent" );
    }
    return componentConstructor;
  }
}
