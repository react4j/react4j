package com.example.on_prop_changed;

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

@Generated("react4j.processor.ReactProcessor")
class React4j_DoubleOnPropChanged extends DoubleOnPropChanged {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DoubleOnPropChanged" );
    }
    return componentConstructor;
  }

  @Override
  protected double getMyProp() {
    return props().getAny( PROP_myProp ).asDouble();
  }

  @Override
  protected boolean reportPropChanges(@Nonnull final JsPropertyMap<Object> props,
      @Nonnull final JsPropertyMap<Object> nextProps, final boolean inComponentDidUpdate) {
    boolean modified = false;
    if ( !Js.isTripleEqual( props.get( PROP_myProp ), nextProps.get( PROP_myProp ) ) ) {
      if ( inComponentDidUpdate ) {
        onMyPropChanged( props.getAny( PROP_myProp ).asDouble() );
      }
      modified = true;
    }
    return modified;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<DoubleOnPropChanged> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DoubleOnPropChanged createComponent() {
      return new React4j_DoubleOnPropChanged();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<DoubleOnPropChanged> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DoubleOnPropChanged createComponent() {
      return new React4j_DoubleOnPropChanged();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }
}
