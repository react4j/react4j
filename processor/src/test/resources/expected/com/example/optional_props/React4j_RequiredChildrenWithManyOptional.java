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

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( "myPropA", RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A );
    defaultProps.set( "myPropB", RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B );
    defaultProps.set( "myPropC", RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C );
    defaultProps.set( "myPropD", RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
    return componentConstructor;
  }

  @Override
  protected String getMyPropA() {
    return Js.<Props>uncheckedCast( props() ).myPropA;
  }

  @Override
  protected String getMyPropB() {
    return Js.<Props>uncheckedCast( props() ).myPropB;
  }

  @Override
  protected String getMyPropC() {
    return Js.<Props>uncheckedCast( props() ).myPropC;
  }

  @Override
  protected String getMyPropD() {
    return Js.<Props>uncheckedCast( props() ).myPropD;
  }

  @Override
  protected ReactNode[] getChildren() {
    return Js.uncheckedCast( Js.<Props>uncheckedCast( props() ).children );
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;

    String myPropA;

    String myPropB;

    String myPropC;

    String myPropD;

    Object children;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyOptional> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new React4j_RequiredChildrenWithManyOptional();
    }
  }
}
