package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_PublicProp extends PublicProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PublicProp" );
    }
    return componentConstructor;
  }

  @Override
  public String getMyProp() {
    return null != props().getAny( "myProp" ) ? props().getAny( "myProp" ).asString() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PublicProp> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PublicProp createComponent() {
      return new React4j_PublicProp();
    }
  }
}
