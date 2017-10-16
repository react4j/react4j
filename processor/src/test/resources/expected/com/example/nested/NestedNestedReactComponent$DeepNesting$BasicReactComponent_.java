package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class NestedNestedReactComponent$DeepNesting$BasicReactComponent_ extends NestedNestedReactComponent.DeepNesting.BasicReactComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, NestedNestedReactComponent.DeepNesting.BasicReactComponent> {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected NestedNestedReactComponent.DeepNesting.BasicReactComponent createComponent() {
      return new NestedNestedReactComponent$DeepNesting$BasicReactComponent_();
    }
  }
}
