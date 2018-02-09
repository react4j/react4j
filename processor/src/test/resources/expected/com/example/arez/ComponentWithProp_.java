package com.example.arez;

import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import arez.annotations.ObservableRef;
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
    type = "ComponentWithProp",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class ComponentWithProp_ extends ComponentWithProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithProp" );
    }
    return componentConstructor;
  }

  @Override
  @Observable(
      name = "value",
      expectSetter = false
  )
  protected String getValue() {
    return props().getAny( "value" ).asString();
  }

  @Nonnull
  @ObservableRef
  protected abstract arez.Observable getValueObservable();

  @Override
  protected final void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
    if ( !Js.isTripleEqual( props().get( "value" ), nextProps.get( "value" ) ) ) {
      getValueObservable().reportChanged();
    }
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

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, ComponentWithProp> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithProp createComponent() {
      return new Arez_ComponentWithProp_();
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
