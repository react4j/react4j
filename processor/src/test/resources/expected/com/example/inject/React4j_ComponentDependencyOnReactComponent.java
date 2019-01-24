package com.example.inject;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
import arez.annotations.PerInstance;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "ComponentDependencyOnReactComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.CONSUME,
    dagger = Feature.ENABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ComponentDependencyOnReactComponent extends ComponentDependencyOnReactComponent {
  React4j_ComponentDependencyOnReactComponent(
      @Nonnull @PerInstance final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentDependencyOnReactComponent" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeComponent {
    private React4j_ComponentDependencyOnReactComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = ComponentDependencyOnReactComponentDaggerComponentExtension.InjectSupport.create( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
