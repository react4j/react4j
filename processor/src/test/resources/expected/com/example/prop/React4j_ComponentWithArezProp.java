package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_ComponentWithArezProp extends ComponentWithArezProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_value = ReactConfig.shouldMinimizePropKeys() ? "a" : "value";

  static final String PROP_model = ReactConfig.shouldMinimizePropKeys() ? "b" : "model";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithArezProp" );
    }
    return componentConstructor;
  }

  @Override
  protected String getValue() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_value ) ? props().getAny( PROP_value ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_value ) );
    }
  }

  @Override
  protected ComponentWithArezProp.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_model ) ? props().getAny( PROP_model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_model ) );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentWithArezProp> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithArezProp createComponent() {
      return new React4j_ComponentWithArezProp();
    }
  }
}
