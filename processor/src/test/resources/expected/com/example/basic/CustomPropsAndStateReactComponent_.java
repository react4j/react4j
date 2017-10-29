package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactElement;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CustomPropsAndStateReactComponent_ extends CustomPropsAndStateReactComponent {
  private static final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  static ReactElement<CustomPropsAndStateReactComponent.Props, NativeReactComponent> _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactElement<CustomPropsAndStateReactComponent.Props, NativeReactComponent> _create(@Nullable final CustomPropsAndStateReactComponent.Props props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactElement<CustomPropsAndStateReactComponent.Props, NativeReactComponent> _create(@Nullable final CustomPropsAndStateReactComponent.Props props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return componentConstructor;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, CustomPropsAndStateReactComponent> {
    NativeReactComponent(@Nonnull final CustomPropsAndStateReactComponent.Props props) {
      super( props );
    }

    @Override
    protected CustomPropsAndStateReactComponent createComponent() {
      return new CustomPropsAndStateReactComponent_();
    }
  }
}
