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
import react4j.core.Procedure;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class BasicHandlerComponent_ extends BasicHandlerComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private final Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicHandlerComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final BasicHandlerComponent component) {
    return ((BasicHandlerComponent_) component)._handleFoo;
  }

  @Nonnull
  private Procedure create_handleFoo() {
    final Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "BasicHandlerComponent.handleFoo" ) ) );
    }
    return handler;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicHandlerComponent> {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected BasicHandlerComponent createComponent() {
      return new BasicHandlerComponent_();
    }
  }
}
