package com.example.arez.memoize;

import arez.annotations.ArezComponent;
import arez.annotations.Memoize;
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
    name = "BasicMemoizeComponent"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_BasicMemoizeComponent extends BasicMemoizeComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicMemoizeComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Memoize(
      priority = Priority.LOWEST
  )
  String getIcon(final String key) {
    return super.getIcon(key);
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<BasicMemoizeComponent> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicMemoizeComponent createComponent() {
      return new Arez_React4j_BasicMemoizeComponent();
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

  private static final class NativeReactComponent extends NativeAdapterComponent<BasicMemoizeComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicMemoizeComponent createComponent() {
      return new Arez_React4j_BasicMemoizeComponent();
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
