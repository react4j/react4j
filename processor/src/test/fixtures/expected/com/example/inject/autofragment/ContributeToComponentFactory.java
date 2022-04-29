package com.example.inject.autofragment;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.ContributeTo;
import sting.Eager;
import sting.Injectable;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
@ContributeTo("MyAutoFragment")
public final class ContributeToComponentFactory {
  @Nonnull
  private final String someParam;

  ContributeToComponentFactory(@Nonnull final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static React4j_ContributeToComponent create(@Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static ContributeToComponentFactory c_factory;

    private static void setFactory(@Nonnull final ContributeToComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'ContributeToComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_ContributeToComponent create(@Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'ContributeToComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_ContributeToComponent( view, c_factory.someParam );
    }
  }
}
