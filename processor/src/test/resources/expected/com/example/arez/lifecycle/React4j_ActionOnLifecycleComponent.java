package com.example.arez.lifecycle;

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
    name = "ActionOnLifecycleComponent"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ActionOnLifecycleComponent extends ActionOnLifecycleComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ActionOnLifecycleComponent" );
    }
    return componentConstructor;
  }

  void $$react4j$$_componentDidMount() {
    postMount();
    storeDebugDataAsState();
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentDidMount();

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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ActionOnLifecycleComponent> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ActionOnLifecycleComponent createComponent() {
      return new Arez_React4j_ActionOnLifecycleComponent();
    }

    @Override
    public void componentDidMount() {
      ((Arez_React4j_ActionOnLifecycleComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ActionOnLifecycleComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ActionOnLifecycleComponent createComponent() {
      return new Arez_React4j_ActionOnLifecycleComponent();
    }

    @Override
    public void componentDidMount() {
      ((Arez_React4j_ActionOnLifecycleComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((Arez_React4j_ActionOnLifecycleComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }
}
