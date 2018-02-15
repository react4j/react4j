package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_PropDefaultWithColorfulName extends PropDefaultWithColorfulName {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropDefaultWithColorfulName" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( "myProp12$23", PropDefaultWithColorfulName.DEFAULT_MY_PROP12$23 );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
    return componentConstructor;
  }

  @Override
  protected String getMyProp12$23() {
    return props().getAny( "myProp12$23" ).asString();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, PropDefaultWithColorfulName> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropDefaultWithColorfulName createComponent() {
      return new React4j_PropDefaultWithColorfulName();
    }
  }
}
