package com.example.post_mount;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.OnComponentDidMount;

@Generated("react4j.processor.ReactProcessor")
class React4j_BasicModel extends BasicModel {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicModel" );
    }
    return componentConstructor;
  }

  private void $$react4j$$_componentDidMount() {
    postMount();
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BasicModel> implements OnComponentDidMount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected BasicModel createComponent() {
      return new React4j_BasicModel();
    }

    @Override
    public final void componentDidMount() {
      ((React4j_BasicModel) component() ).$$react4j$$_componentDidMount();
    }
  }
}
