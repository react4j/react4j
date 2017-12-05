package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class MethodInjectReactComponent_ extends MethodInjectReactComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  @Inject
  MethodInjectReactComponent_() {
  }

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "MethodInjectReactComponent" );
    }
    return componentConstructor;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, MethodInjectReactComponent> {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected MethodInjectReactComponent createComponent() {
      return new MethodInjectReactComponent_();
    }
  }

  @Module
  public static final class DaggerModule {
    @Provides
    static Component provideComponent(final MethodInjectReactComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  public interface DaggerComponent {
    Provider<Component> createProvider();

    default MethodInjectReactComponent get() {
      return (MethodInjectReactComponent) createProvider().get();
    }
  }
}
