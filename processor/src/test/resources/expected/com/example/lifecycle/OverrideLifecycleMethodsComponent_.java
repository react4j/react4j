package com.example.lifecycle;

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
final class OverrideLifecycleMethodsComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> TYPE = getConstructorFunction();

  private static native JsObject componentDidUpdate_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::componentDidUpdate(*)(arguments); } }-*/;

  private static native JsObject componentWillMount_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::componentWillMount(*)(arguments); } }-*/;

  private static native JsObject componentWillReceiveProps_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::componentWillReceiveProps(*)(arguments); } }-*/;

  private static native JsObject componentWillUnmount_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::componentWillUnmount(*)(arguments); } }-*/;

  private static native JsObject componentWillUpdate_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::componentWillUpdate(*)(arguments); } }-*/;

  private static native JsObject shouldComponentUpdate_function()/*-{ return function() { return this.@com.example.lifecycle.React_OverrideLifecycleMethodsComponent::shouldComponentUpdate(*)(arguments); } }-*/;

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> getConstructorFunction() {
    final JsPropertyMapOfAny prototype = JsUtil.getPrototypeForClass( React_OverrideLifecycleMethodsComponent.class );
    prototype.set( "componentDidUpdate", componentDidUpdate_function() );
    prototype.set( "componentWillMount", componentWillMount_function() );
    prototype.set( "componentWillReceiveProps", componentWillReceiveProps_function() );
    prototype.set( "componentWillUnmount", componentWillUnmount_function() );
    prototype.set( "componentWillUpdate", componentWillUpdate_function() );
    prototype.set( "shouldComponentUpdate", shouldComponentUpdate_function() );
    final ComponentConstructorFunction<BaseProps, BaseState, React_OverrideLifecycleMethodsComponent> componentConstructor = React_OverrideLifecycleMethodsComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }
}
