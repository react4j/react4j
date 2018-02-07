package com.example.arez;

import arez.annotations.ArezComponent;
import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.EventHandler;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@ArezComponent(
    type = "ComponentWithNoAutoActionEventHandler",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class ComponentWithNoAutoActionEventHandler_ extends ComponentWithNoAutoActionEventHandler {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private final EventHandler.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithNoAutoActionEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static EventHandler.Procedure _handleFoo(@Nonnull final ComponentWithNoAutoActionEventHandler component) {
    return ((ComponentWithNoAutoActionEventHandler_) component)._handleFoo;
  }

  @Override
  protected final void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
  }

  @Nonnull
  private EventHandler.Procedure create_handleFoo() {
    final EventHandler.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithNoAutoActionEventHandler.handleFoo" ) ) );
    }
    return handler;
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1,
        @Nonnull BaseContext arg2);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, ComponentWithNoAutoActionEventHandler> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected ComponentWithNoAutoActionEventHandler createComponent() {
      return new Arez_ComponentWithNoAutoActionEventHandler_();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final BaseState arg1) {
      performComponentDidUpdate(arg0,arg1);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final BaseState arg1, @Nonnull final BaseContext arg2) {
      return performShouldComponentUpdate(arg0,arg1,arg2);
    }
  }
}
