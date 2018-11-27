package com.example.on_prop_change;

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
class React4j_CharOnPropChange extends CharOnPropChange {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CharOnPropChange" );
    }
    return componentConstructor;
  }

  @Override
  protected char getMyProp() {
    return props().getAny( Props.myProp ).asChar();
  }

  @Override
  protected void preUpdateOnPropChange(@Nonnull final JsPropertyMap<Object> prevProps,
      @Nonnull final JsPropertyMap<Object> props) {
    final boolean myProp = !Js.isTripleEqual( props.get( Props.myProp ), prevProps.get( Props.myProp ) );
    if ( myProp ) {
      onMyPropChange( props.getAny( Props.myProp ).asChar() );
    }
  }

  @Override
  protected void componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( null != prevProps ) {
      final JsPropertyMap<Object> props = props();
      preUpdateOnPropChange( prevProps, props );
    }
  }

  @Override
  protected void componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    Object getSnapshotBeforeUpdate(@Nonnull JsPropertyMap<Object> prevProps,
        @Nonnull JsPropertyMap<Object> prevState);
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    Object getSnapshotBeforeUpdate(@Nonnull JsPropertyMap<Object> prevProps,
        @Nonnull JsPropertyMap<Object> prevState);

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<CharOnPropChange> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CharOnPropChange createComponent() {
      return new React4j_CharOnPropChange();
    }

    @Override
    public Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      performComponentPreUpdate( prevProps );
      return null;
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CharOnPropChange> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CharOnPropChange createComponent() {
      return new React4j_CharOnPropChange();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      performComponentPreUpdate( prevProps );
      return null;
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }
}
