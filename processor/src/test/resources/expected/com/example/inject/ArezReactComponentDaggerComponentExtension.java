package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
public interface ArezReactComponentDaggerComponentExtension extends React4j_ArezReactComponentDaggerComponentExtension {
  default void bindArezReactComponent() {
    InjectSupport.setFactory( getArezReactComponentDaggerSubcomponent().createFactory() );
  }

  @Override
  DaggerSubcomponent getArezReactComponentDaggerSubcomponent();

  final class InjectSupport {
    private static Arez_React4j_ArezReactComponent.Factory c_factory;

    static void setFactory(@Nonnull final Arez_React4j_ArezReactComponent.Factory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to re-initialize the React4j dependency injection provider for the component named 'ArezReactComponent'. Initialization should only occur a single time." );
      }
      c_factory = factory;
    }

    public static React4j_ArezReactComponent create(
        @Nonnull final NativeComponent nativeComponent) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'ArezReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_factory.create( nativeComponent );
    }
  }
}
