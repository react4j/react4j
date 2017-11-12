package com.example.render;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class RenderAsArrayComponent_ extends RenderAsArrayComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseState, BaseContext, NativeReactComponent> TYPE = getConstructorFunction();

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
  private static ComponentConstructorFunction<BaseProps, BaseState, BaseContext, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, BaseContext, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "RenderAsArrayComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Nullable
  protected ReactNode render() {
    return ReactNode.of( renderAsArray() );
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, RenderAsArrayComponent> {
    NativeReactComponent(@Nonnull final BaseProps props, @Nonnull final BaseContext context) {
      super( props, context );
    }

    @Override
    protected RenderAsArrayComponent createComponent() {
      return new RenderAsArrayComponent_();
    }
  }
}
