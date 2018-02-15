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
class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp1() {
    return props().getAny( "myRequiredProp1" ).asString();
  }

  @Override
  protected String getMyRequiredProp2() {
    return props().getAny( "myRequiredProp2" ).asString();
  }

  @Override
  protected String getMyRequiredProp3() {
    return props().getAny( "myRequiredProp3" ).asString();
  }

  @Override
  protected ReactNode[] getChildren() {
    return props().getAny( "children" ).cast();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, RequiredChildrenWithManyRequired> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyRequired createComponent() {
      return new React4j_RequiredChildrenWithManyRequired();
    }
  }
}
