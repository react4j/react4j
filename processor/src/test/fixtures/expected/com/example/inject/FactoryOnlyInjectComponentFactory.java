package com.example.inject;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;

@Generated("react4j.processor.React4jProcessor")
public final class FactoryOnlyInjectComponentFactory {
  @Nonnull
  private final String someParam;

  public FactoryOnlyInjectComponentFactory(@Nonnull final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static React4j_FactoryOnlyInjectComponent create(@Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static FactoryOnlyInjectComponentFactory c_factory;

    private static void setFactory(@Nonnull final FactoryOnlyInjectComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'FactoryOnlyInjectComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_FactoryOnlyInjectComponent create(@Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'FactoryOnlyInjectComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_FactoryOnlyInjectComponent( view, c_factory.someParam );
    }
  }
}
