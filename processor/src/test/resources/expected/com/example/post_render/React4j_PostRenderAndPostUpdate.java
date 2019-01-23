package com.example.post_render;

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
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;

@ArezComponent(
    name = "PostRenderAndPostUpdate",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_PostRenderAndPostUpdate extends PostRenderAndPostUpdate {
  React4j_PostRenderAndPostUpdate(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PostRenderAndPostUpdate" );
    }
    return componentConstructor;
  }

  private void $$react4j$$_componentDidMount() {
    postRender();
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentDidUpdate() {
    postRender();
    postUpdate();
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PostRenderAndPostUpdate> implements OnComponentDidMount, OnComponentDidUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PostRenderAndPostUpdate createComponent() {
      return new Arez_React4j_PostRenderAndPostUpdate( this );
    }

    @Override
    public final void componentDidMount() {
      ((React4j_PostRenderAndPostUpdate) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_PostRenderAndPostUpdate) component() ).$$react4j$$_componentDidUpdate();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_PostRenderAndPostUpdate) component() ).render();
    }
  }
}
