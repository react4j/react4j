package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_PublicReactComponent extends PublicReactComponent {
  @Inject
  React4j_PublicReactComponent() {
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PublicReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected void componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class InjectSupport {
    private static Provider<PublicReactComponent> c_provider;

    static void setProvider(final Provider<PublicReactComponent> provider) {
      c_provider = provider;
    }

    private static Provider<PublicReactComponent> getProvider() {
      if ( ReactConfig.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'PublicReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_provider;
    }
  }

  static final class Props {
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<PublicReactComponent> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PublicReactComponent createComponent() {
      return InjectSupport.getProvider().get();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PublicReactComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PublicReactComponent createComponent() {
      return InjectSupport.getProvider().get();
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