package com.example.nested;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.annotations.EventHandler;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class NestedCompleteComponent_BasicReactComponent_ extends NestedCompleteComponent.BasicReactComponent {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  private static Provider<NestedCompleteComponent.BasicReactComponent> c_provider;

  @Nonnull
  private final EventHandler.Procedure _myEventHandler = create_myEventHandler();

  @Inject
  NestedCompleteComponent_BasicReactComponent_() {
  }

  static void setProvider(final Provider<NestedCompleteComponent.BasicReactComponent> provider) {
    c_provider = provider;
  }

  private static Provider<NestedCompleteComponent.BasicReactComponent> getProvider() {
    Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'BasicReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
    return c_provider;
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  static EventHandler.Procedure _myEventHandler(@Nonnull final NestedCompleteComponent.BasicReactComponent component) {
    return ((NestedCompleteComponent_BasicReactComponent_) component)._myEventHandler;
  }

  @Override
  String getMyProp() {
    return Js.asPropertyMap( props() ).getAny( "myProp" ).asString();
  }

  @Nonnull
  private EventHandler.Procedure create_myEventHandler() {
    final EventHandler.Procedure handler = () -> this.myEventHandler();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "BasicReactComponent.myEventHandler" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, NestedCompleteComponent.BasicReactComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected NestedCompleteComponent.BasicReactComponent createComponent() {
      return getProvider().get();
    }
  }
}
