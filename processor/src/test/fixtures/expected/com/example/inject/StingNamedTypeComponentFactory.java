package com.example.inject;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.Eager;
import sting.Injectable;
import sting.Named;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
@Named("blah")
public final class StingNamedTypeComponentFactory {
  @Nonnull
  private final String someParam;

  StingNamedTypeComponentFactory(@Nonnull final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static React4j_StingNamedTypeComponent create(@Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static StingNamedTypeComponentFactory c_factory;

    private static void setFactory(@Nonnull final StingNamedTypeComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'StingNamedTypeComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_StingNamedTypeComponent create(@Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'StingNamedTypeComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_StingNamedTypeComponent( view, c_factory.someParam );
    }
  }
}
