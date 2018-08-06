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
      return null != props().getAny( "myProp" ) ? props().getAny( "myProp" ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( "myProp" ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( "myProp2" ) ? props().getAny( "myProp2" ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( "myProp2" ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( "myProp3" ) ? props().getAny( "myProp3" ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( "myProp3" ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp4() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( "myProp4" ) ? props().getAny( "myProp4" ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( "myProp4" ) );
    }
  }

  @Nullable
  @Override
  protected ReactNode getChild() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( "children" ) ? props().getAny( "children" ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( "children" ) );
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
