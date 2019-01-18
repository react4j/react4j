package com.example.prop_validate;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.OnShouldComponentUpdate;

@Generated("react4j.processor.ReactProcessor")
class React4j_IntPropValidate extends IntPropValidate {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "IntPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected int getMyProp() {
    return props().getAny( Props.myProp ).asInt();
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final int typed$myProp = Js.asInt( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<IntPropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected IntPropValidate createComponent() {
      return new React4j_IntPropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<IntPropValidate> implements OnShouldComponentUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected IntPropValidate createComponent() {
      return new React4j_IntPropValidate();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_IntPropValidate) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }
  }
}
