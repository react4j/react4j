package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
public interface PostConstructOnReactComponentDaggerComponentExtension extends React4j_PostConstructOnReactComponentDaggerComponentExtension {
  default void bindPostConstructOnReactComponent() {
    React4j_PostConstructOnReactComponentDaggerComponentExtension.super.bindPostConstructOnReactComponent();
    InjectSupport.setFactory( getPostConstructOnReactComponentDaggerSubcomponent().createFactory() );
  }

  @Override
  DaggerSubcomponent getPostConstructOnReactComponentDaggerSubcomponent();

  final class InjectSupport {
    private static Arez_React4j_PostConstructOnReactComponent.Factory c_factory;

    static void setFactory(
        @Nonnull final Arez_React4j_PostConstructOnReactComponent.Factory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to re-initialize the React4j dependency injection provider for the component named 'PostConstructOnReactComponent'. Initialization should only occur a single time." );
      }
      c_factory = factory;
    }

    public static React4j_PostConstructOnReactComponent create(
        @Nonnull final NativeComponent nativeComponent) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'PostConstructOnReactComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return c_factory.create( nativeComponent );
    }
  }
}
