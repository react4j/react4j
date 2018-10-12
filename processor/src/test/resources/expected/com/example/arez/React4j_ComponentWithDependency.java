package com.example.arez;

import arez.Disposable;
import arez.annotations.ArezComponent;
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
    name = "ComponentWithDependency"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ComponentWithDependency extends ComponentWithDependency {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

  static final String PROP_model = ReactConfig.shouldMinimizePropKeys() ? "b" : "model";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithDependency" );
    }
    return componentConstructor;
  }

  @Override
  protected final boolean anyPropsDisposed() {
    final ComponentWithDependency.Model $$react4jv$$_getModel = getModel();
    if ( Disposable.isDisposed( $$react4jv$$_getModel ) ) {
      return true;
    }
    return false;
  }

  @Override
  protected String getValue() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_value ) ? props().getAny( PROP_value ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value ) );
    }
  }

  @Override
  protected ComponentWithDependency.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_model ) ? props().getAny( PROP_model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_model ) );
    }
  }

  @Override
  protected boolean shouldUpdateOnPropChanges(@Nonnull final JsPropertyMap<Object> nextProps) {
    if ( !Js.isTripleEqual( props().get( PROP_value ), nextProps.get( PROP_value ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props().get( PROP_model ), nextProps.get( PROP_model ) ) ) {
      return true;
    }
    return false;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentWillUnmount();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);

    void componentWillUnmount();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ComponentWithDependency> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithDependency createComponent() {
      return new Arez_React4j_ComponentWithDependency();
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentWithDependency> implements Lifecycle {
    @JsConstructor
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
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }
}
