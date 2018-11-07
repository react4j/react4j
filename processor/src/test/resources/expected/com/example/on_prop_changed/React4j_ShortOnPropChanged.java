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
class React4j_ShortOnPropChanged extends ShortOnPropChanged {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ShortOnPropChanged" );
    }
    return componentConstructor;
  }

  @Override
  protected short getMyProp() {
    return props().getAny( Props.myProp ).asShort();
  }

  @Override
  protected boolean reportPropChanges(@Nonnull final JsPropertyMap<Object> props,
      @Nonnull final JsPropertyMap<Object> nextProps, final boolean inComponentDidUpdate) {
    boolean modified = false;
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      if ( inComponentDidUpdate ) {
        onMyPropChanged( props.getAny( Props.myProp ).asShort() );
      }
      modified = true;
    }
    return modified;
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ShortOnPropChanged> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ShortOnPropChanged createComponent() {
      return new React4j_ShortOnPropChanged();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ShortOnPropChanged> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ShortOnPropChanged createComponent() {
      return new React4j_ShortOnPropChanged();
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
