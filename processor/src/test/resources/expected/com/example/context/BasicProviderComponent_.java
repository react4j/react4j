package com.example.context;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.PropType;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class BasicProviderComponent_ extends BasicProviderComponent {
  static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicProviderComponent" );
    }
    final PropType valid = () -> null;
    final JsPropertyMap<Object> childContextTypes = JsPropertyMap.of();
    childContextTypes.set( "newTodo", valid );
    Js.asPropertyMap( componentConstructor ).set( "childContextTypes", childContextTypes );
    return componentConstructor;
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    BasicProviderComponent.Context getChildContext();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, BasicProviderComponent> implements Lifecycle {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected BasicProviderComponent createComponent() {
      return new BasicProviderComponent_();
    }

    @Override
    public BasicProviderComponent.Context getChildContext() {
      return performGetChildContext();
    }
  }
}
