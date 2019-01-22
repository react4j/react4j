package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_BoolJavaBeanPropComponent extends BoolJavaBeanPropComponent {
  React4j_BoolJavaBeanPropComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BoolJavaBeanPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean isFoo() {
    return props().getAny( Props.foo ).asBoolean();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String foo = React.shouldMinimizePropKeys() ? "a" : "foo";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BoolJavaBeanPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BoolJavaBeanPropComponent createComponent() {
      return new React4j_BoolJavaBeanPropComponent( this );
    }
  }
}
