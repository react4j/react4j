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

@Generated("react4j.processor.ReactProcessor")
class React4j_CustomNameProp extends CustomNameProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_foo = ReactConfig.shouldMinimizePropKeys() ? "a" : "foo";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomNameProp" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_foo ) ? props().getAny( PROP_foo ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_foo ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomNameProp> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomNameProp createComponent() {
      return new React4j_CustomNameProp();
    }
  }
}
