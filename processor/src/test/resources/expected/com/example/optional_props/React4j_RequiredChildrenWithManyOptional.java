package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyPropA() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropA ) ? props().getAny( Props.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropA ) );
    }
  }

  @Override
  protected String getMyPropB() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropB ) ? props().getAny( Props.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropB ) );
    }
  }

  @Override
  protected String getMyPropC() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropC ) ? props().getAny( Props.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropC ) );
    }
  }

  @Override
  protected String getMyPropD() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropD ) ? props().getAny( Props.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropD ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.children ) ? props().getAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.children ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myPropA = ReactConfig.shouldMinimizePropKeys() ? "a" : "myPropA";

    static final String myPropB = ReactConfig.shouldMinimizePropKeys() ? "b" : "myPropB";

    static final String myPropC = ReactConfig.shouldMinimizePropKeys() ? "c" : "myPropC";

    static final String myPropD = ReactConfig.shouldMinimizePropKeys() ? "d" : "myPropD";

    static final String children = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyOptional> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new React4j_RequiredChildrenWithManyOptional();
    }
  }
}
