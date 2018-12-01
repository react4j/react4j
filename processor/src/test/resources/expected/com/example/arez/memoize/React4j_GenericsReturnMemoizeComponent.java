package com.example.arez.memoize;

import arez.Disposable;
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

@ArezComponent(
    name = "GenericsReturnMemoizeComponent"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_GenericsReturnMemoizeComponent extends GenericsReturnMemoizeComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericsReturnMemoizeComponent" );
    }
    return componentConstructor;
  }

  void $$react4j$$_componentDidMount() {
    storeDebugDataAsState();
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  final void $$react4j$$_componentWillUnmount() {
    Disposable.dispose( this );
  }

  @Override
  @Memoize(
      priority = Priority.LOWEST
  )
  Consumer<String> getIcon(final String key) {
    return super.getIcon(key);
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
    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps);

    void componentWillUnmount();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps);

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);

    void componentWillUnmount();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<GenericsReturnMemoizeComponent> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericsReturnMemoizeComponent createComponent() {
      return new Arez_React4j_GenericsReturnMemoizeComponent();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps) {
      return performShouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<GenericsReturnMemoizeComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericsReturnMemoizeComponent createComponent() {
      return new Arez_React4j_GenericsReturnMemoizeComponent();
    }

    @Override
    public final void componentDidMount() {
      ((Arez_React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps) {
      return performShouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps) {
      ((Arez_React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
