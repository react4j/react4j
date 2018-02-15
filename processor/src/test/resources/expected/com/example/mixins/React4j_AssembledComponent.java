package com.example.mixins;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.Callback;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_AssembledComponent extends AssembledComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private final Callback.Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "AssembledComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Callback.Procedure _handleFoo(@Nonnull final AssembledComponent component) {
    return ((React4j_AssembledComponent) component)._handleFoo;
  }

  @Nonnull
  private Callback.Procedure create_handleFoo() {
    final Callback.Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "AssembledComponent.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, AssembledComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected AssembledComponent createComponent() {
      return new React4j_AssembledComponent();
    }
  }
}
