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
class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myRequiredProp1 = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp1";

  private static final String PROP_myRequiredProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myRequiredProp2";

  private static final String PROP_myRequiredProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myRequiredProp3";

  private static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp1() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp1 ) ? props().getAny( PROP_myRequiredProp1 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp1 ) );
    }
  }

  @Override
  protected String getMyRequiredProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp2 ) ? props().getAny( PROP_myRequiredProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp2 ) );
    }
  }

  @Override
  protected String getMyRequiredProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp3 ) ? props().getAny( PROP_myRequiredProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp3 ) );
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

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyRequired> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyRequired createComponent() {
      return new React4j_RequiredChildrenWithManyRequired();
    }
  }
}
