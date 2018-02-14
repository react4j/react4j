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
    type = "ExplicitlyEnabledCallback",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class ExplicitlyEnabledCallback_ extends ExplicitlyEnabledCallback {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final Callback.Procedure _handleAction = create_handleAction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitlyEnabledCallback" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Callback.Procedure _handleAction(@Nonnull final ExplicitlyEnabledCallback component) {
    return ((ExplicitlyEnabledCallback_) component)._handleAction;
  }

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
  }

  @Nonnull
  private Callback.Procedure create_handleAction() {
    final Callback.Procedure handler = () -> this.handleAction();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ExplicitlyEnabledCallback.handleAction" ) ) );
    }
    return handler;
  }

  @Action(
      reportParameters = false
  )
  void handleAction() {
    super.handleAction();
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

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, ExplicitlyEnabledCallback> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitlyEnabledCallback createComponent() {
      return new Arez_ExplicitlyEnabledCallback_();
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
