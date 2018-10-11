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
class React4j_BooleanPropValidate extends BooleanPropValidate {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BooleanPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean getMyProp() {
    return props().getAny( PROP_myProp ).asBoolean();
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( PROP_myProp );
    if ( null != raw$myProp ) {
      final boolean typed$myProp = Js.asBoolean( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0);

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<BooleanPropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BooleanPropValidate createComponent() {
      return new React4j_BooleanPropValidate();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BooleanPropValidate> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BooleanPropValidate createComponent() {
      return new React4j_BooleanPropValidate();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      performComponentDidUpdate(arg0);
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      return performShouldComponentUpdate(arg0);
    }
  }
}
