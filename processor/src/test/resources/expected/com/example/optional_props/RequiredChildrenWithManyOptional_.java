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
class RequiredChildrenWithManyOptional_ extends RequiredChildrenWithManyOptional {
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
    return props().getAny( "myPropA" ).asString();
  }

  @Override
  protected String getMyPropB() {
    return props().getAny( "myPropB" ).asString();
  }

  @Override
  protected String getMyPropC() {
    return props().getAny( "myPropC" ).asString();
  }

  @Override
  protected String getMyPropD() {
    return props().getAny( "myPropD" ).asString();
  }

  @Override
  protected ReactNode[] getChildren() {
    return props().getAny( "children" ).cast();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, RequiredChildrenWithManyOptional> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyOptional createComponent() {
      return new RequiredChildrenWithManyOptional_();
    }
  }
}
