package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_GenericTypeMultiPropComponent<T> extends GenericTypeMultiPropComponent<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypeMultiPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    return null != props().getAny( "value" ) ? props().getAny( "value" ).cast() : null;
  }

  @Override
  protected String getValue2() {
    return null != props().getAny( "value2" ) ? props().getAny( "value2" ).asString() : null;
  }

  @Nullable
  @Override
  protected String getValue3() {
    return null != props().getAny( "value3" ) ? props().getAny( "value3" ).asString() : null;
  }

  @Nullable
  @Override
  protected String getValue4() {
    return null != props().getAny( "value4" ) ? props().getAny( "value4" ).asString() : null;
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<GenericTypeMultiPropComponent<T>> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericTypeMultiPropComponent<T> createComponent() {
      return new React4j_GenericTypeMultiPropComponent<T>();
    }
  }
}
