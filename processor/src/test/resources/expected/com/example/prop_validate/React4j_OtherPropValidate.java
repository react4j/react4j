package com.example.prop_validate;

import java.util.ArrayList;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.OnShouldComponentUpdate;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_OtherPropValidate extends OtherPropValidate {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OtherPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected ArrayList getMyProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final ArrayList typed$myProp = Js.cast( raw$myProp );
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<OtherPropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OtherPropValidate createComponent() {
      return new React4j_OtherPropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<OtherPropValidate> implements OnShouldComponentUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OtherPropValidate createComponent() {
      return new React4j_OtherPropValidate();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_OtherPropValidate) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }
  }
}
