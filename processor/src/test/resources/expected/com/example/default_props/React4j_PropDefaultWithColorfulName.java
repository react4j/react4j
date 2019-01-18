package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_PropDefaultWithColorfulName extends PropDefaultWithColorfulName {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropDefaultWithColorfulName" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp12$23() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp12$23 ) ? props().getAny( Props.myProp12$23 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp12$23 ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp12$23 = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp12$23";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PropDefaultWithColorfulName> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropDefaultWithColorfulName createComponent() {
      return new React4j_PropDefaultWithColorfulName();
    }
  }
}
