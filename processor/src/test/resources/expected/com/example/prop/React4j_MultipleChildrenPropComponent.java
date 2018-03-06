package com.example.prop;

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
class React4j_MultipleChildrenPropComponent extends MultipleChildrenPropComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultipleChildrenPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected ReactNode[] getChildren() {
    return props().has( "children" ) ? props().getAny( "children" ).cast() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultipleChildrenPropComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultipleChildrenPropComponent createComponent() {
      return new React4j_MultipleChildrenPropComponent();
    }
  }
}
