package com.example.nested;

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
    name = "BasicReactComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class NestedNestedReactComponent_DeepNesting_React4j_BasicReactComponent extends NestedNestedReactComponent.DeepNesting.BasicReactComponent {
  NestedNestedReactComponent_DeepNesting_React4j_BasicReactComponent(
      @Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NestedNestedReactComponent.DeepNesting.BasicReactComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NestedNestedReactComponent.DeepNesting.BasicReactComponent createComponent() {
      return new Arez_NestedNestedReactComponent_DeepNesting_React4j_BasicReactComponent( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((NestedNestedReactComponent_DeepNesting_React4j_BasicReactComponent) component() ).render();
    }
  }
}
