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

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent4" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    return Js.<Props>uncheckedCast( props() ).myProp;
  }

  @Nullable
  @Override
  protected String getMyProp2() {
    return Js.<Props>uncheckedCast( props() ).myProp2;
  }

  @Nullable
  @Override
  protected String getMyProp3() {
    return Js.<Props>uncheckedCast( props() ).myProp3;
  }

  @Nullable
  @Override
  protected String getMyProp4() {
    return Js.<Props>uncheckedCast( props() ).myProp4;
  }

  @Nullable
  @Override
  protected ReactNode getChild() {
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

    String myProp;

    @Nullable
    String myProp2;

    @Nullable
    String myProp3;

    @Nullable
    String myProp4;

    @Nullable
    Object children;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent4> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent4 createComponent() {
      return new React4j_MultiPropComponent4();
    }
  }
}
