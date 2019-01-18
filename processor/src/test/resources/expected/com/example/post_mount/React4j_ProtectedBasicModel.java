package com.example.post_mount;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.OnComponentDidMount;

@Generated("react4j.processor.ReactProcessor")
class React4j_ProtectedBasicModel extends ProtectedBasicModel {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ProtectedBasicModel" );
    }
    return componentConstructor;
  }

  private void $$react4j$$_componentDidMount() {
    postMount();
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ProtectedBasicModel> implements OnComponentDidMount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ProtectedBasicModel createComponent() {
      return new React4j_ProtectedBasicModel();
    }

    @Override
    public final void componentDidMount() {
      ((React4j_ProtectedBasicModel) component() ).$$react4j$$_componentDidMount();
    }
  }
}
