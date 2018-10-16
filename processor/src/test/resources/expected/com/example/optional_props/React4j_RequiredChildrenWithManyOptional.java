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
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myPropA = ReactConfig.shouldMinimizePropKeys() ? "a" : "myPropA";

  static final String PROP_myPropB = ReactConfig.shouldMinimizePropKeys() ? "b" : "myPropB";

  static final String PROP_myPropC = ReactConfig.shouldMinimizePropKeys() ? "c" : "myPropC";

  static final String PROP_myPropD = ReactConfig.shouldMinimizePropKeys() ? "d" : "myPropD";

  static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( PROP_myPropA, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A );
    defaultProps.set( PROP_myPropB, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B );
    defaultProps.set( PROP_myPropC, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C );
    defaultProps.set( PROP_myPropD, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
    return componentConstructor;
  }

  @Override
  protected String getMyPropA() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myPropA ) ? props().getAny( PROP_myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myPropA ) );
    }
  }

  @Override
  protected String getMyPropB() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myPropB ) ? props().getAny( PROP_myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myPropB ) );
    }
  }

  @Override
  protected String getMyPropC() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myPropC ) ? props().getAny( PROP_myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myPropC ) );
    }
  }

  @Override
  protected String getMyPropD() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myPropD ) ? props().getAny( PROP_myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myPropD ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_children ) ? props().getAny( PROP_children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_children ) );
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyOptional> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new React4j_RequiredChildrenWithManyOptional();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyOptional> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new React4j_RequiredChildrenWithManyOptional();
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
