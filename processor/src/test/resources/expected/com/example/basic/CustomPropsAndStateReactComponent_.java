package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react4j.core.BaseContext;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomPropsAndStateReactComponent_ extends CustomPropsAndStateReactComponent {
  static final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, BaseContext, CustomPropsAndStateReactComponent> {
    NativeReactComponent(@Nullable final CustomPropsAndStateReactComponent.Props props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected CustomPropsAndStateReactComponent createComponent() {
      return new CustomPropsAndStateReactComponent_();
    }
  }
}
