package com.example.nested;

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
public final class NestedCompleteComponent_BasicReactComponentFactory {
  private final String value;

  @Inject
  @SuppressWarnings("Sting:Jsr330InjectPresent")
  NestedCompleteComponent_BasicReactComponentFactory(final String value) {
    this.value = value;
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static NestedCompleteComponent_React4j_BasicReactComponent create(
      @Nonnull final NativeComponent component) {
    return InjectSupport.create( component );
  }

  private static final class InjectSupport {
    private static NestedCompleteComponent_BasicReactComponentFactory c_factory;

    private static void setFactory(
        @Nonnull final NestedCompleteComponent_BasicReactComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j component factory for the component named 'BasicReactComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static NestedCompleteComponent_React4j_BasicReactComponent create(
        @Nonnull final NativeComponent component) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'BasicReactComponent' before the component factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_NestedCompleteComponent_React4j_BasicReactComponent( component, c_factory.value );
    }
  }
}
