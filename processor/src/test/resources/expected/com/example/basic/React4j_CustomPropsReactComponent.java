package com.example.basic;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "CustomPropsReactComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_CustomPropsReactComponent extends CustomPropsReactComponent {
  React4j_CustomPropsReactComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomPropsReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  boolean isSomeField() {
    return props().getAny( Props.someField ).asBoolean();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String someField = React.shouldMinimizePropKeys() ? "a" : "someField";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomPropsReactComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomPropsReactComponent createComponent() {
      return new Arez_React4j_CustomPropsReactComponent( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_CustomPropsReactComponent) component() ).render();
    }
  }
}
