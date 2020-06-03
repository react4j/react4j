package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.Eager;
import sting.Injectable;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
public final class NestedCompleteComponent_BasicViewFactory {
  private final String value;

  @Inject
  @SuppressWarnings("Sting:Jsr330InjectPresent")
  NestedCompleteComponent_BasicViewFactory(final String value) {
    this.value = value;
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static NestedCompleteComponent_React4j_BasicView create(@Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static NestedCompleteComponent_BasicViewFactory c_factory;

    private static void setFactory(
        @Nonnull final NestedCompleteComponent_BasicViewFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'BasicView' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static NestedCompleteComponent_React4j_BasicView create(
        @Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'BasicView' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_NestedCompleteComponent_React4j_BasicView( view, c_factory.value );
    }
  }
}
