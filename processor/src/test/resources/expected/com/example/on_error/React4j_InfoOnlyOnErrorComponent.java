package com.example.on_error;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
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
import react4j.internal.OnComponentWillUnmount;

@ArezComponent(
    name = "InfoOnlyOnErrorComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE,
    dagger = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_InfoOnlyOnErrorComponent extends InfoOnlyOnErrorComponent {
  React4j_InfoOnlyOnErrorComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "InfoOnlyOnErrorComponent" );
    }
    return componentConstructor;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_InfoOnlyOnErrorComponent) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnComponentDidCatch {
    private React4j_InfoOnlyOnErrorComponent $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_InfoOnlyOnErrorComponent( this );
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      $$react4j$$_component.onError( info );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentWillUnmount, OnComponentDidCatch {
    private React4j_InfoOnlyOnErrorComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_InfoOnlyOnErrorComponent( this );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      $$react4j$$_component.onError( info );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
