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
import react4j.annotations.Callback;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@ArezComponent(
    type = "ComponentWithCallback",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class ComponentWithCallback_ extends ComponentWithCallback {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final Callback.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithCallback.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithCallback" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Callback.Procedure _handleFoo(@Nonnull final ComponentWithCallback component) {
    return ((ComponentWithCallback_) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithCallback.CustomHandler _handleFoo2(@Nonnull final ComponentWithCallback component) {
    return ((ComponentWithCallback_) component)._handleFoo2;
  }

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
  }

  @Nonnull
  private Callback.Procedure create_handleFoo() {
    final Callback.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithCallback.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithCallback.CustomHandler create_handleFoo2() {
    final ComponentWithCallback.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithCallback.handleFoo2" ) ) );
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

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0, @Nonnull BaseState arg1);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, ComponentWithCallback> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithCallback createComponent() {
      return new Arez_ComponentWithCallback_();
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
