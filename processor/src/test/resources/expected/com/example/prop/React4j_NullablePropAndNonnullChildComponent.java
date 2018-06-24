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
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_NullablePropAndNonnullChildComponent extends NullablePropAndNonnullChildComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NullablePropAndNonnullChildComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  @Override
  protected String getMyProp() {
    return props().getAny( "myProp" ).asString();
  }

  @Nullable
  @Override
  protected String getMyProp2() {
    return null != props().getAny( "myProp2" ) ? props().getAny( "myProp2" ).asString() : null;
  }

  @Override
  protected ReactNode getChild() {
    return null != props().getAny( "children" ) ? props().getAny( "children" ).cast() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NullablePropAndNonnullChildComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NullablePropAndNonnullChildComponent createComponent() {
      return new React4j_NullablePropAndNonnullChildComponent();
    }
  }
}
