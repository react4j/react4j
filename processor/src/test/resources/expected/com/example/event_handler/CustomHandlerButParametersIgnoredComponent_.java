package com.example.event_handler;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CustomHandlerButParametersIgnoredComponent_ extends CustomHandlerButParametersIgnoredComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private final CustomHandlerButParametersIgnoredComponent.CustomHandler _handleFoo = create_handleFoo();

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomHandlerButParametersIgnoredComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static CustomHandlerButParametersIgnoredComponent.CustomHandler _handleFoo(@Nonnull final CustomHandlerButParametersIgnoredComponent component) {
    return ((CustomHandlerButParametersIgnoredComponent_) component)._handleFoo;
  }

  @Nonnull
  private CustomHandlerButParametersIgnoredComponent.CustomHandler create_handleFoo() {
    final CustomHandlerButParametersIgnoredComponent.CustomHandler handler = arg0 -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "CustomHandlerButParametersIgnoredComponent.handleFoo" ) ) );
    }
    return handler;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, CustomHandlerButParametersIgnoredComponent> {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected CustomHandlerButParametersIgnoredComponent createComponent() {
      return new CustomHandlerButParametersIgnoredComponent_();
    }
  }
}
