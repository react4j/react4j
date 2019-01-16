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
class React4j_CharPropValidate extends CharPropValidate {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CharPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected char getMyProp() {
    return props().getAny( Props.myProp ).asChar();
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final char typed$myProp = Js.asChar( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  boolean $$react4j$$_shouldComponentUpdate(@Nullable final JsPropertyMap<Object> nextProps) {
    final JsPropertyMap<Object> props = props();
    boolean modified = false;
    assert null != nextProps;
    if ( ReactConfig.shouldValidatePropValues() ) {
      validatePropValues( nextProps );
    }
    if ( !Js.isTripleEqual( props().get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    return modified;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";
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
    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<CharPropValidate> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CharPropValidate createComponent() {
      return new React4j_CharPropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CharPropValidate> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CharPropValidate createComponent() {
      return new React4j_CharPropValidate();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_CharPropValidate) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }
  }
}
