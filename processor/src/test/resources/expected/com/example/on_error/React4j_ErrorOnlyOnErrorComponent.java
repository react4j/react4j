package com.example.on_error;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import elemental2.core.JsError;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentDidCatch;

@ArezComponent(
    name = "ErrorOnlyOnErrorComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ErrorOnlyOnErrorComponent extends ErrorOnlyOnErrorComponent {
  React4j_ErrorOnlyOnErrorComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ErrorOnlyOnErrorComponent" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentDidCatch {
    private React4j_ErrorOnlyOnErrorComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ErrorOnlyOnErrorComponent( this );
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      $$react4j$$_component.onError( error );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
