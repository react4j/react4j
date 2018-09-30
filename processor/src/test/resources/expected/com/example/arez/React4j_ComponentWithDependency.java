package com.example.arez;

import arez.Disposable;
import arez.ObservableValue;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import arez.annotations.ObservableValueRef;
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
import react4j.arez.ReactArezConfig;

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
    final ComponentConstructorFunction componentConstructor = ReactArezConfig.shouldStoreArezDataAsState() ? NativeReactComponent::new : LiteNativeReactComponent::new;
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
  @Observable(
      name = "value",
      expectSetter = false,
      readOutsideTransaction = true
  )
  protected String getValue() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value ) ? props().getAny( PROP_value ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value ) );
    }
  }

  @Nonnull
  @ObservableValueRef
  protected abstract ObservableValue getValueObservableValue();

  @Override
  @Observable(
      name = "model",
      expectSetter = false,
      readOutsideTransaction = true
  )
  protected ComponentWithDependency.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_model ) ? props().getAny( PROP_model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_model ) );
    }
  }

  @Nonnull
  @ObservableValueRef
  protected abstract ObservableValue getModelObservableValue();

  @Override
  @Action(
      verifyRequired = false
  )
  protected boolean shouldComponentUpdate(@Nullable final JsPropertyMap<Object> nextProps) {
    boolean modified = false;
    if ( !Js.isTripleEqual( props().get( PROP_value ), null == nextProps ? null : nextProps.get( PROP_value ) ) ) {
      modified = true;
      getValueObservableValue().reportChanged();
    }
    if ( !Js.isTripleEqual( props().get( PROP_model ), null == nextProps ? null : nextProps.get( PROP_model ) ) ) {
      modified = true;
      getModelObservableValue().reportChanged();
    }
    return modified;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);
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

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      return performShouldComponentUpdate(arg0,arg1);
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
