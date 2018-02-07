package com.example.context;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.PropType;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class BasicContextComponent_ extends BasicContextComponent {
  static final ComponentConstructorFunction<BasicContextComponent.Context> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BasicContextComponent.Context> getConstructorFunction() {
    final ComponentConstructorFunction<BasicContextComponent.Context> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicContextComponent" );
    }
    final PropType valid = () -> null;
    final JsPropertyMap<Object> contextTypes = JsPropertyMap.of();
    contextTypes.set( "newTodo", valid );
    Js.asPropertyMap( componentConstructor ).set( "contextTypes", contextTypes );
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BasicContextComponent.Context, BasicContextComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BasicContextComponent.Context context) {
      super( props, context );
    }

    @Override
    protected BasicContextComponent createComponent() {
      return new BasicContextComponent_();
    }
  }
}
