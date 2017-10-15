package com.example.arez;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.Procedure;
import react4j.core.ReactConfig;

@ArezComponent(
    name = "ComponentWithNoAutoActionEventHandler",
    singleton = false,
    disposable = true,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
class ComponentWithNoAutoActionEventHandler_ extends ComponentWithNoAutoActionEventHandler {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithNoAutoActionEventHandler> TYPE = getConstructorFunction();

  @Nonnull
  private final Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithNoAutoActionEventHandler> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithNoAutoActionEventHandler> componentConstructor = React_ComponentWithNoAutoActionEventHandler::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ComponentWithNoAutoActionEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final ComponentWithNoAutoActionEventHandler component) {
    return ((ComponentWithNoAutoActionEventHandler_) component)._handleFoo;
  }

  @Nonnull
  private Procedure create_handleFoo() {
    final Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithNoAutoActionEventHandler.handleFoo" ) ) );
    }
    return handler;
  }
}
