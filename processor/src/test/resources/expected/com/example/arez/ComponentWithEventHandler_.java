package com.example.arez;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.Procedure;
import react4j.core.ReactConfig;

@ArezComponent(
    name = "ComponentWithEventHandler",
    singleton = false,
    disposable = true,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
class ComponentWithEventHandler_ extends ComponentWithEventHandler {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithEventHandler> TYPE = getConstructorFunction();

  @Nonnull
  private final Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithEventHandler.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithEventHandler> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithEventHandler> componentConstructor = React_ComponentWithEventHandler::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ComponentWithEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final ComponentWithEventHandler component) {
    return ((ComponentWithEventHandler_) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithEventHandler.CustomHandler _handleFoo2(@Nonnull final ComponentWithEventHandler component) {
    return ((ComponentWithEventHandler_) component)._handleFoo2;
  }

  @Nonnull
  private Procedure create_handleFoo() {
    final Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithEventHandler.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithEventHandler.CustomHandler create_handleFoo2() {
    final ComponentWithEventHandler.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithEventHandler.handleFoo2" ) ) );
    }
    return handler;
  }

  @Action(
      reportParameters = false
  )
  void handleFoo() {
    super.handleFoo();
  }

  @Action(
      reportParameters = false
  )
  int handleFoo2(final int arg0) {
    return super.handleFoo2(arg0);
  }
}
