package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class DaggerFalseComponent_ extends DaggerFalseComponent {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  private static Provider<DaggerFalseComponent> c_provider;

  @Inject
  DaggerFalseComponent_() {
  }

  static void setProvider(final Provider<DaggerFalseComponent> provider) {
    c_provider = provider;
  }

  private static Provider<DaggerFalseComponent> getProvider() {
    Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'DaggerFalseComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
    return c_provider;
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DaggerFalseComponent" );
    }
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, DaggerFalseComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected DaggerFalseComponent createComponent() {
      return getProvider().get();
    }
  }
}
