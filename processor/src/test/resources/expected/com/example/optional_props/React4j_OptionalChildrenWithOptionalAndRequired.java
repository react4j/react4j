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
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_OptionalChildrenWithOptionalAndRequired extends OptionalChildrenWithOptionalAndRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myRequiredProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

  static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp";

  static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OptionalChildrenWithOptionalAndRequired" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( PROP_myProp, OptionalChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
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
  protected String getMyProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp ) ? props().getAny( PROP_myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_children ) ? props().getAny( PROP_children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_children ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<OptionalChildrenWithOptionalAndRequired> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OptionalChildrenWithOptionalAndRequired createComponent() {
      return new React4j_OptionalChildrenWithOptionalAndRequired();
    }
  }
}
