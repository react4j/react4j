package com.example.prop_validate;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_ExplicitNamePropValidate extends ExplicitNamePropValidate {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ReactConfig.shouldValidatePropValues() ? LiteNativeReactComponent::new : NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitNamePropValidate" );
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
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( PROP_myProp );
    if ( null != raw$myProp ) {
      final String typed$myProp = Js.cast( raw$myProp );
      zapzap( typed$myProp );
    }
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ExplicitNamePropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitNamePropValidate createComponent() {
      return new React4j_ExplicitNamePropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ExplicitNamePropValidate> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitNamePropValidate createComponent() {
      return new React4j_ExplicitNamePropValidate();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      return performShouldComponentUpdate(arg0,arg1);
    }
  }
}
