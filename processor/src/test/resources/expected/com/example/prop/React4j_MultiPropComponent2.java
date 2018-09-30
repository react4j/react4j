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
class React4j_MultiPropComponent2 extends MultiPropComponent2 {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  private static final String PROP_myProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp2";

  private static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent2" );
    }
    return componentConstructor;
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
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp2 ) ? props().getAny( PROP_myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp2 ) );
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

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent2> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent2 createComponent() {
      return new React4j_MultiPropComponent2();
    }
  }
}
