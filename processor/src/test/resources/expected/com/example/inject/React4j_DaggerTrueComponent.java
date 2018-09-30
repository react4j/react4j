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
class React4j_DaggerTrueComponent extends DaggerTrueComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static Provider<DaggerTrueComponent> c_provider;

  @Inject
  React4j_DaggerTrueComponent() {
  }

  static void setProvider(final Provider<DaggerTrueComponent> provider) {
    c_provider = provider;
  }

  private static Provider<DaggerTrueComponent> getProvider() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'DaggerTrueComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
    }
    return c_provider;
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DaggerTrueComponent" );
    }
    return componentConstructor;
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

  private static final class NativeReactComponent extends NativeAdapterComponent<DaggerTrueComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DaggerTrueComponent createComponent() {
      return getProvider().get();
    }
  }
}
