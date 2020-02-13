package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeComponent;
import sting.Eager;
import sting.Injectable;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
public final class PublicReactComponentFactory {
  private final String someParam;

  @Inject
  @SuppressWarnings("Sting:Jsr330InjectPresent")
  PublicReactComponentFactory(final String someParam) {
    this.someParam = someParam;
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static React4j_PublicReactComponent create(@Nonnull final NativeComponent component) {
    return InjectSupport.create( component );
  }

  private static final class InjectSupport {
    private static PublicReactComponentFactory c_factory;

    private static void setFactory(@Nonnull final PublicReactComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j component factory for the component named 'PublicReactComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_PublicReactComponent create(@Nonnull final NativeComponent component) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'PublicReactComponent' before the component factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_PublicReactComponent( component, c_factory.someParam );
    }
  }
}
