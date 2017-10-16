package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomPropsAndStateReactComponent_ extends CustomPropsAndStateReactComponent {
  public static final ComponentConstructorFunction<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, NativeReactComponent> TYPE = getConstructorFunction();

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
