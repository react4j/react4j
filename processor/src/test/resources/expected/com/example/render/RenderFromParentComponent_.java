package com.example.render;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.RenderElement;

@Generated("react4j.processor.ReactProcessor")
class RenderFromParentComponent_ extends RenderFromParentComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "RenderFromParentComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Nullable
  protected RenderElement render() {
    return RenderElement.of( renderAsString() );
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, RenderFromParentComponent> {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected RenderFromParentComponent createComponent() {
      return new RenderFromParentComponent_();
    }
  }
}
