package com.example.arez;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
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

@ArezComponent(
    name = "NonArezHasArezFieldAnnotation",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_NonArezHasArezFieldAnnotation extends NonArezHasArezFieldAnnotation {
  React4j_NonArezHasArezFieldAnnotation(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NonArezHasArezFieldAnnotation" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NonArezHasArezFieldAnnotation> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NonArezHasArezFieldAnnotation createComponent() {
      return new Arez_React4j_NonArezHasArezFieldAnnotation( this );
    }
  }
}
