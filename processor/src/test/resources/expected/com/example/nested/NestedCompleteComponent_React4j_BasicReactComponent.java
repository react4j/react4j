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
import react4j.annotations.Callback;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class NestedCompleteComponent_React4j_BasicReactComponent extends NestedCompleteComponent.BasicReactComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  private static Provider<NestedCompleteComponent.BasicReactComponent> c_provider;

  @Nonnull
  final Callback.Procedure _myEventHandler = create_myEventHandler();

  @Inject
  NestedCompleteComponent_React4j_BasicReactComponent() {
  }

  static void setProvider(final Provider<NestedCompleteComponent.BasicReactComponent> provider) {
    c_provider = provider;
  }

  private static Provider<NestedCompleteComponent.BasicReactComponent> getProvider() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'BasicReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
    }
    return c_provider;
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  String getMyProp() {
    return null != props().getAny( "myProp" ) ? props().getAny( "myProp" ).asString() : null;
  }

  @Nonnull
  private Callback.Procedure create_myEventHandler() {
    final Callback.Procedure handler = () -> this.myEventHandler();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "BasicReactComponent.myEventHandler" ) ) );
    }
    return handler;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NestedCompleteComponent.BasicReactComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NestedCompleteComponent.BasicReactComponent createComponent() {
      return getProvider().get();
    }
  }
}
