package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class GenericAllPropComponent_<T> extends GenericAllPropComponent<T> {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericAllPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected <K> K getValue() {
    return props().getAny( "value" ).cast();
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<BaseState, BaseContext, GenericAllPropComponent<T>> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected GenericAllPropComponent<T> createComponent() {
      return new GenericAllPropComponent_<T>();
    }
  }
}
