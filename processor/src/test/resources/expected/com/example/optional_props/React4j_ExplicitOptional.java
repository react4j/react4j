package com.example.optional_props;

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
class React4j_ExplicitOptional extends ExplicitOptional {
  static final String PROP_myRequiredProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

  static final String PROP_myOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "b" : "myOptionalProp";

  static final String PROP_myOtherOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "c" : "myOtherOptionalProp";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp ) ? props().getAny( PROP_myRequiredProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp ) );
    }
  }

  @Override
  protected String getMyOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myOptionalProp ) ? props().getAny( PROP_myOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myOptionalProp ) );
    }
  }

  @Override
  protected String getMyOtherOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myOtherOptionalProp ) ? props().getAny( PROP_myOtherOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myOtherOptionalProp ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ExplicitOptional> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitOptional createComponent() {
      return new React4j_ExplicitOptional();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ExplicitOptional> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitOptional createComponent() {
      return new React4j_ExplicitOptional();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      performComponentDidUpdate( prevProps );
    }
  }
}
