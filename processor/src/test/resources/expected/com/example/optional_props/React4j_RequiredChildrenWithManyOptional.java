package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

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
    return null != props().getAny( "myPropA" ) ? props().getAny( "myPropA" ).asString() : null;
  }

  @Override
  protected String getMyPropB() {
    return null != props().getAny( "myPropB" ) ? props().getAny( "myPropB" ).asString() : null;
  }

  @Override
  protected String getMyPropC() {
    return null != props().getAny( "myPropC" ) ? props().getAny( "myPropC" ).asString() : null;
  }

  @Override
  protected String getMyPropD() {
    return null != props().getAny( "myPropD" ) ? props().getAny( "myPropD" ).asString() : null;
  }

  @Override
  protected ReactNode[] getChildren() {
    return null != props().getAny( "children" ) ? props().getAny( "children" ).cast() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyOptional> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new React4j_RequiredChildrenWithManyOptional();
    }
  }
}
