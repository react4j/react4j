package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Computed;
import arez.annotations.Priority;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@ArezComponent(
    name = "NotObservableAsNotUpdateOnChangeProp"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_NotObservableAsNotUpdateOnChangeProp extends NotObservableAsNotUpdateOnChangeProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NotObservableAsNotUpdateOnChangeProp" );
    }
    return componentConstructor;
  }

  @Override
  protected Object getValue() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value ) ? props().getAny( PROP_value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value ) );
    }
  }

  @Override
  @Computed(
      priority = Priority.LOWEST
  )
  int someValue() {
    return super.someValue();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0);
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<NotObservableAsNotUpdateOnChangeProp> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NotObservableAsNotUpdateOnChangeProp createComponent() {
      return new Arez_React4j_NotObservableAsNotUpdateOnChangeProp();
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      return performShouldComponentUpdate(arg0);
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NotObservableAsNotUpdateOnChangeProp> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NotObservableAsNotUpdateOnChangeProp createComponent() {
      return new Arez_React4j_NotObservableAsNotUpdateOnChangeProp();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      performComponentDidUpdate(arg0);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      return performShouldComponentUpdate(arg0);
    }
  }
}
