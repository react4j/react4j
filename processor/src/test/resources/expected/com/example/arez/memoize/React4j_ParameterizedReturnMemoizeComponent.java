package com.example.arez.memoize;

import arez.annotations.ArezComponent;
import arez.annotations.Memoize;
import arez.annotations.Priority;
import java.util.function.Consumer;
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
    name = "ParameterizedReturnMemoizeComponent"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ParameterizedReturnMemoizeComponent extends ParameterizedReturnMemoizeComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ReactArezConfig.shouldStoreArezDataAsState() ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ParameterizedReturnMemoizeComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Memoize(
      priority = Priority.LOWEST
  )
  <T> Consumer<T> getIcon(final String key) {
    return super.getIcon(key);
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ParameterizedReturnMemoizeComponent> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ParameterizedReturnMemoizeComponent createComponent() {
      return new Arez_React4j_ParameterizedReturnMemoizeComponent();
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

  private static final class NativeReactComponent extends NativeAdapterComponent<ParameterizedReturnMemoizeComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ParameterizedReturnMemoizeComponent createComponent() {
      return new Arez_React4j_ParameterizedReturnMemoizeComponent();
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
