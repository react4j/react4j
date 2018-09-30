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
class React4j_RequiredChildrenWithOptionalAndRequired extends RequiredChildrenWithOptionalAndRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myRequiredProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

  private static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp";

  private static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithOptionalAndRequired" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( PROP_myProp, RequiredChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
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

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithOptionalAndRequired> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithOptionalAndRequired createComponent() {
      return new React4j_RequiredChildrenWithOptionalAndRequired();
    }
  }
}
