package com.example.default_props;

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
class React4j_PropDefaultWithColorfulName extends PropDefaultWithColorfulName {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropDefaultWithColorfulName" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( Props.myProp12$23, PropDefaultWithColorfulName.DEFAULT_MY_PROP12$23 );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
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

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<PropDefaultWithColorfulName> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropDefaultWithColorfulName createComponent() {
      return new React4j_PropDefaultWithColorfulName();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PropDefaultWithColorfulName> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropDefaultWithColorfulName createComponent() {
      return new React4j_PropDefaultWithColorfulName();
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
