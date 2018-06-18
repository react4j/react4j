package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_ExplicitOptional extends ExplicitOptional {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp() {
    return null != props().getAny( "myRequiredProp" ) ? props().getAny( "myRequiredProp" ).asString() : null;
  }

  @Override
  protected String getMyOptionalProp() {
    return null != props().getAny( "myOptionalProp" ) ? props().getAny( "myOptionalProp" ).asString() : null;
  }

  @Override
  protected String getMyOtherOptionalProp() {
    return null != props().getAny( "myOtherOptionalProp" ) ? props().getAny( "myOtherOptionalProp" ).asString() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ExplicitOptional> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitOptional createComponent() {
      return new React4j_ExplicitOptional();
    }
  }
}
