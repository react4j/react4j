package com.example.inject;

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
class React4j_InjectFalseComponent extends InjectFalseComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "InjectFalseComponent" );
    }
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, InjectFalseComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected InjectFalseComponent createComponent() {
      return new React4j_InjectFalseComponent();
    }
  }
}
