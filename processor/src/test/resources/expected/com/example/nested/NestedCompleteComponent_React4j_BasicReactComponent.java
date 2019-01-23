package com.example.nested;

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
    name = "BasicReactComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.CONSUME,
    dagger = Feature.ENABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class NestedCompleteComponent_React4j_BasicReactComponent extends NestedCompleteComponent.BasicReactComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class InjectSupport {
    private static Provider<NestedCompleteComponent.BasicReactComponent> c_provider;

    static void setProvider(final Provider<NestedCompleteComponent.BasicReactComponent> provider) {
      c_provider = provider;
    }

    private static Provider<NestedCompleteComponent.BasicReactComponent> getProvider() {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'BasicReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_provider;
    }
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NestedCompleteComponent.BasicReactComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NestedCompleteComponent.BasicReactComponent createComponent() {
      return InjectSupport.getProvider().get();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((NestedCompleteComponent_React4j_BasicReactComponent) component() ).render();
    }
  }
}
