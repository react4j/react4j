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
class React4j_MultiPropComponent4 extends MultiPropComponent4 {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  private static final String PROP_myProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp2";

  private static final String PROP_myProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myProp3";

  private static final String PROP_myProp4 = ReactConfig.shouldMinimizePropKeys() ? "d" : "myProp4";

  private static final String PROP_child = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent4" );
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

  @Nullable
  @Override
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp2 ) ? props().getAny( PROP_myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp2 ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp3 ) ? props().getAny( PROP_myProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp3 ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp4() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myProp4 ) ? props().getAny( PROP_myProp4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp4 ) );
    }
  }

  @Nullable
  @Override
  protected ReactNode getChild() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_child ) ? props().getAny( PROP_child ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_child ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent4> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent4 createComponent() {
      return new React4j_MultiPropComponent4();
    }
  }
}
