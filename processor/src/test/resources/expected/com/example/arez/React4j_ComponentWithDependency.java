package com.example.arez;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Dependency;
import arez.annotations.Observable;
import arez.annotations.ObservableRef;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@ArezComponent(
    name = "ComponentWithDependency",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ComponentWithDependency extends ComponentWithDependency {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithDependency" );
    }
    return componentConstructor;
  }

  @Override
  @Observable(
      name = "value",
      expectSetter = false
  )
  protected String getValue() {
    return null != props().getAny( "value" ) ? props().getAny( "value" ).asString() : null;
  }

  @Nonnull
  @ObservableRef
  protected abstract arez.Observable getValueObservable();

  @Override
  @Observable(
      name = "model",
      expectSetter = false
  )
  @Dependency
  protected ComponentWithDependency.Model getModel() {
    return null != props().getAny( "model" ) ? props().getAny( "model" ).cast() : null;
  }

  @Nonnull
  @ObservableRef
  protected abstract arez.Observable getModelObservable();

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
    if ( !Js.isTripleEqual( props().get( "value" ), null == nextProps ? null : nextProps.get( "value" ) ) ) {
      getValueObservable().reportChanged();
    }
    if ( !Js.isTripleEqual( props().get( "model" ), null == nextProps ? null : nextProps.get( "model" ) ) ) {
      getModelObservable().reportChanged();
    }
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentWithDependency> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithDependency createComponent() {
      return new Arez_React4j_ComponentWithDependency();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      performComponentDidUpdate(arg0,arg1);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      return performShouldComponentUpdate(arg0,arg1);
    }
  }
}
