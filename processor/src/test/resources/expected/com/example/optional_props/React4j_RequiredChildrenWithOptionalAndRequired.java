package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_RequiredChildrenWithOptionalAndRequired extends RequiredChildrenWithOptionalAndRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithOptionalAndRequired" );
    }
    final JsPropertyMap<Object> defaultProps = JsPropertyMap.of();
    defaultProps.set( "myProp", RequiredChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", defaultProps );
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp() {
    return props().getAny( "myRequiredProp" ).asString();
  }

  @Override
  protected String getMyProp() {
    return props().getAny( "myProp" ).asString();
  }

  @Override
  protected ReactNode[] getChildren() {
    return props().getAny( "children" ).cast();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, RequiredChildrenWithOptionalAndRequired> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithOptionalAndRequired createComponent() {
      return new React4j_RequiredChildrenWithOptionalAndRequired();
    }
  }
}
