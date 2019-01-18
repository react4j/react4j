package com.example.pre_unmount;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.OnComponentWillUnmount;

@Generated("react4j.processor.ReactProcessor")
class React4j_ProtectedModel extends ProtectedModel {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ProtectedModel" );
    }
    return componentConstructor;
  }

  private void $$react4j$$_componentWillUnmount() {
    preUnmount();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ProtectedModel> implements OnComponentWillUnmount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ProtectedModel createComponent() {
      return new React4j_ProtectedModel();
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_ProtectedModel) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
