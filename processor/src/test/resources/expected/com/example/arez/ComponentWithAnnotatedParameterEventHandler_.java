package com.example.arez;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@ArezComponent(
    type = "ComponentWithAnnotatedParameterEventHandler",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class ComponentWithAnnotatedParameterEventHandler_ extends ComponentWithAnnotatedParameterEventHandler {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler2 _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithAnnotatedParameterEventHandler" );
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

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
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

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, ComponentWithAnnotatedParameterEventHandler> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithAnnotatedParameterEventHandler createComponent() {
      return new Arez_ComponentWithAnnotatedParameterEventHandler_();
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
        @Nonnull final BaseState arg1) {
      return performShouldComponentUpdate(arg0,arg1);
    }
  }
}
