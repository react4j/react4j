package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CustomPropsAndStateReactComponent_ extends CustomPropsAndStateReactComponent {
  private static final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, BaseContext, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final CustomPropsAndStateReactComponent.Props props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final CustomPropsAndStateReactComponent.Props props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, BaseContext, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, BaseContext, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return componentConstructor;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, BaseContext, CustomPropsAndStateReactComponent> {
    NativeReactComponent(@Nonnull final CustomPropsAndStateReactComponent.Props props, @Nonnull final BaseContext context) {
      super( props, context );
    }

    @Override
    protected CustomPropsAndStateReactComponent createComponent() {
      return new CustomPropsAndStateReactComponent_();
    }
  }
}
