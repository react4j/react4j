package com.example.optional_props;

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
class React4j_ExplicitOptional extends ExplicitOptional {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myRequiredProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

  static final String PROP_myOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "b" : "myOptionalProp";

  static final String PROP_myOtherOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "c" : "myOtherOptionalProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp ) ? props().getAny( PROP_myRequiredProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp ) );
    }
  }

  @Override
  protected String getMyOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myOptionalProp ) ? props().getAny( PROP_myOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myOptionalProp ) );
    }
  }

  @Override
  protected String getMyOtherOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myOtherOptionalProp ) ? props().getAny( PROP_myOtherOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myOtherOptionalProp ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ExplicitOptional> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitOptional createComponent() {
      return new React4j_ExplicitOptional();
    }
  }
}
