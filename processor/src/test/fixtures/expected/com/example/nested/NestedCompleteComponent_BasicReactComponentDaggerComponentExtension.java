package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
public interface NestedCompleteComponent_BasicReactComponentDaggerComponentExtension extends NestedCompleteComponent_React4j_BasicReactComponentDaggerComponentExtension {
  default void bindBasicReactComponent() {
    InjectSupport.setFactory( getBasicReactComponentDaggerSubcomponent().createFactory() );
  }

  @Override
  DaggerSubcomponent getBasicReactComponentDaggerSubcomponent();

  final class InjectSupport {
    private static Arez_NestedCompleteComponent_React4j_BasicReactComponent.Factory c_factory;

    static void setFactory(
        @Nonnull final Arez_NestedCompleteComponent_React4j_BasicReactComponent.Factory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to re-initialize the React4j dependency injection provider for the component named 'BasicReactComponent'. Initialization should only occur a single time." );
      }
      c_factory = factory;
    }

    @Nonnull
    public static NestedCompleteComponent_React4j_BasicReactComponent create(
        @Nonnull final NativeComponent nativeComponent) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'BasicReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_factory.create( nativeComponent );
    }
  }
}
