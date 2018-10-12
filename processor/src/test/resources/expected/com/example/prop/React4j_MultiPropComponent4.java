package com.example.prop;

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
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_MultiPropComponent4 extends MultiPropComponent4 {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

  static final String PROP_myProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp2";

  static final String PROP_myProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myProp3";

  static final String PROP_myProp4 = ReactConfig.shouldMinimizePropKeys() ? "d" : "myProp4";

  static final String PROP_child = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent4" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_myProp ) ? props().getAny( PROP_myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_myProp2 ) ? props().getAny( PROP_myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp2 ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_myProp3 ) ? props().getAny( PROP_myProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp3 ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp4() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_myProp4 ) ? props().getAny( PROP_myProp4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myProp4 ) );
    }
  }

  @Nullable
  @Override
  protected ReactNode getChild() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return props().has( PROP_child ) ? props().getAny( PROP_child ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_child ) );
    }
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<MultiPropComponent4> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent4 createComponent() {
      return new React4j_MultiPropComponent4();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent4> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent4 createComponent() {
      return new React4j_MultiPropComponent4();
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
