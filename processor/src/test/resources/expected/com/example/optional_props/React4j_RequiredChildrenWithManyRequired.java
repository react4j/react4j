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
class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_myRequiredProp1 = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp1";

  static final String PROP_myRequiredProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myRequiredProp2";

  static final String PROP_myRequiredProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myRequiredProp3";

  static final String PROP_children = "children";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp1() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp1 ) ? props().getAny( PROP_myRequiredProp1 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp1 ) );
    }
  }

  @Override
  protected String getMyRequiredProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp2 ) ? props().getAny( PROP_myRequiredProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp2 ) );
    }
  }

  @Override
  protected String getMyRequiredProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_myRequiredProp3 ) ? props().getAny( PROP_myRequiredProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_myRequiredProp3 ) );
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

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyRequired> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyRequired createComponent() {
      return new React4j_RequiredChildrenWithManyRequired();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyRequired> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyRequired createComponent() {
      return new React4j_RequiredChildrenWithManyRequired();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      performComponentDidUpdate(arg0);
    }
  }
}
