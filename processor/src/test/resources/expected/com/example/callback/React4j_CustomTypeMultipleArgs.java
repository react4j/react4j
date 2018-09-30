package com.example.callback;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_CustomTypeMultipleArgs extends CustomTypeMultipleArgs {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final CustomTypeMultipleArgs.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomTypeMultipleArgs" );
    }
    return componentConstructor;
  }

  @Nonnull
  private CustomTypeMultipleArgs.CustomHandler create_handleFoo() {
    final CustomTypeMultipleArgs.CustomHandler handler = (i,j) -> this.handleFoo(i,j);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomTypeMultipleArgs.handleFoo" ) ) );
    }
    return handler;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomTypeMultipleArgs> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomTypeMultipleArgs createComponent() {
      return new React4j_CustomTypeMultipleArgs();
    }
  }
}
