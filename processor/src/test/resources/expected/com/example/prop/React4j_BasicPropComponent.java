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
class React4j_BasicPropComponent extends BasicPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    return props().has( "myProp" ) ? props().getAny( "myProp" ).asString() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BasicPropComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicPropComponent createComponent() {
      return new React4j_BasicPropComponent();
    }
  }
}
