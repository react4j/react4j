package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class CustomHandlerComponent_ extends CustomHandlerComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private final CustomHandlerComponent.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "CustomHandlerComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomHandlerComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerComponent component) {
    return ((CustomHandlerComponent_) component)._handleFoo;
  }

  @Nonnull
  private CustomHandlerComponent.CustomHandler create_handleFoo() {
    final CustomHandlerComponent.CustomHandler handler = arg0 -> this.handleFoo(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerComponent.handleFoo" ) ) );
    }
    return handler;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, CustomHandlerComponent> {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected CustomHandlerComponent createComponent() {
      return new CustomHandlerComponent_();
    }
  }
}
