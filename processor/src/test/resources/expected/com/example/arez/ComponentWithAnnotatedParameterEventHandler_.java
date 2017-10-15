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
import react4j.core.ReactConfig;

@ArezComponent(
    name = "ComponentWithAnnotatedParameterEventHandler",
    singleton = false,
    disposable = true,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
class ComponentWithAnnotatedParameterEventHandler_ extends ComponentWithAnnotatedParameterEventHandler {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithAnnotatedParameterEventHandler> TYPE = getConstructorFunction();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler2 _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithAnnotatedParameterEventHandler> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_ComponentWithAnnotatedParameterEventHandler> componentConstructor = React_ComponentWithAnnotatedParameterEventHandler::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ComponentWithAnnotatedParameterEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static ComponentWithAnnotatedParameterEventHandler.CustomHandler2 _handleFoo(@Nonnull final ComponentWithAnnotatedParameterEventHandler component) {
    return ((ComponentWithAnnotatedParameterEventHandler_) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithAnnotatedParameterEventHandler.CustomHandler _handleFoo2(@Nonnull final ComponentWithAnnotatedParameterEventHandler component) {
    return ((ComponentWithAnnotatedParameterEventHandler_) component)._handleFoo2;
  }

  @Nonnull
  private ComponentWithAnnotatedParameterEventHandler.CustomHandler2 create_handleFoo() {
    final ComponentWithAnnotatedParameterEventHandler.CustomHandler2 handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterEventHandler.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithAnnotatedParameterEventHandler.CustomHandler create_handleFoo2() {
    final ComponentWithAnnotatedParameterEventHandler.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterEventHandler.handleFoo2" ) ) );
    }
    return handler;
  }

  @Nonnull
  @Action(
      reportParameters = false
  )
  String handleFoo() {
    return super.handleFoo();
  }

  @Action(
      reportParameters = false
  )
  void handleFoo2(@Nonnull final String arg0) {
    super.handleFoo2(arg0);
  }
}
