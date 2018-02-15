package com.example.arez;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
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
    type = "AutorunArezReactComponent",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_AutorunArezReactComponent extends AutorunArezReactComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "AutorunArezReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
  }

  @Override
  protected final void triggerScheduler() {
    getContext().triggerScheduler();
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

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, AutorunArezReactComponent> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected AutorunArezReactComponent createComponent() {
      return new Arez_React4j_AutorunArezReactComponent();
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
