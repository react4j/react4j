package com.example.prop_validate;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.OnShouldComponentUpdate;

@Generated("react4j.processor.ReactProcessor")
class React4j_StringPropValidate extends StringPropValidate {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "StringPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final String typed$myProp = Js.asString( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    final JsPropertyMap<Object> props = props();
    boolean modified = false;
    assert null != nextProps;
    if ( React.shouldValidatePropValues() ) {
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
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<StringPropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected StringPropValidate createComponent() {
      return new React4j_StringPropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<StringPropValidate> implements OnShouldComponentUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected StringPropValidate createComponent() {
      return new React4j_StringPropValidate();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_StringPropValidate) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }
  }
}
