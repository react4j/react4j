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
      return null != props().getAny( Props.myRequiredProp ) ? props().getAny( Props.myRequiredProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp ) );
    }
  }

  @Override
  protected String getMyOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myOptionalProp ) ? props().getAny( Props.myOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myOptionalProp ) );
    }
  }

  @Override
  protected String getMyOtherOptionalProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myOtherOptionalProp ) ? props().getAny( Props.myOtherOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myOtherOptionalProp ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

    static final String myOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "b" : "myOptionalProp";

    static final String myOtherOptionalProp = ReactConfig.shouldMinimizePropKeys() ? "c" : "myOtherOptionalProp";
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
