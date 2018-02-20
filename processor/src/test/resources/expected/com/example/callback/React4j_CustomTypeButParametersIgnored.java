package com.example.callback;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_CustomTypeButParametersIgnored extends CustomTypeButParametersIgnored {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final CustomTypeButParametersIgnored.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomTypeButParametersIgnored" );
    }
    return componentConstructor;
  }

  @Nonnull
  private CustomTypeButParametersIgnored.CustomHandler create_handleFoo() {
    final CustomTypeButParametersIgnored.CustomHandler handler = arg0 -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomTypeButParametersIgnored.handleFoo" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomTypeButParametersIgnored> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomTypeButParametersIgnored createComponent() {
      return new React4j_CustomTypeButParametersIgnored();
    }
  }
}
