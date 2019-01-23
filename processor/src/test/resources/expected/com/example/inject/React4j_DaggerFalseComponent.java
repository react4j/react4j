package com.example.inject;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@ArezComponent(
    name = "DaggerFalseComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.CONSUME,
    dagger = Feature.ENABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_DaggerFalseComponent extends DaggerFalseComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DaggerFalseComponent" );
    }
    return componentConstructor;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class InjectSupport {
    private static Provider<DaggerFalseComponent> c_provider;

    static void setProvider(final Provider<DaggerFalseComponent> provider) {
      c_provider = provider;
    }

    private static Provider<DaggerFalseComponent> getProvider() {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'DaggerFalseComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_provider;
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<DaggerFalseComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DaggerFalseComponent createComponent() {
      return InjectSupport.getProvider().get();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_DaggerFalseComponent) component() ).render();
    }
  }
}
