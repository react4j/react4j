package com.example.callback;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;
import react4j.annotations.Callback;

@Generated("react4j.processor.ReactProcessor")
class React4j_TypeParameterOnComponentWithCallback<T> extends TypeParameterOnComponentWithCallback<T> {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final Callback.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "TypeParameterOnComponentWithCallback" );
    }
    return componentConstructor;
  }

  @Nonnull
  private Callback.Procedure create_handleFoo() {
    final Callback.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "TypeParameterOnComponentWithCallback.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent<T> extends NativeAdapterComponent<TypeParameterOnComponentWithCallback<T>> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected TypeParameterOnComponentWithCallback<T> createComponent() {
      return new React4j_TypeParameterOnComponentWithCallback<T>();
    }
  }
}
