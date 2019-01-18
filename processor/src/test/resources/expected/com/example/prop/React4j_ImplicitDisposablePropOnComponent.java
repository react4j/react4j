package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_ImplicitDisposablePropOnComponent extends ImplicitDisposablePropOnComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImplicitDisposablePropOnComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected ImplicitDisposablePropOnComponent.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.model ) ? props().getAny( Props.model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.model ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String model = ReactConfig.shouldMinimizePropKeys() ? "a" : "model";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ImplicitDisposablePropOnComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ImplicitDisposablePropOnComponent createComponent() {
      return new React4j_ImplicitDisposablePropOnComponent();
    }
  }
}
