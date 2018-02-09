package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class GenericAllPropComponent_<T> extends GenericAllPropComponent<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericAllPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected <K> K getValue() {
    return props().getAny( "value" ).cast();
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<BaseState, GenericAllPropComponent<T>> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericAllPropComponent<T> createComponent() {
      return new GenericAllPropComponent_<T>();
    }
  }
}
