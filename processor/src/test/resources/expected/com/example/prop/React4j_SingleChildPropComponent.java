package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_SingleChildPropComponent extends SingleChildPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_child = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "SingleChildPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected ReactNode getChild() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_child ) ? props().getAny( PROP_child ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_child ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<SingleChildPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected SingleChildPropComponent createComponent() {
      return new React4j_SingleChildPropComponent();
    }
  }
}
