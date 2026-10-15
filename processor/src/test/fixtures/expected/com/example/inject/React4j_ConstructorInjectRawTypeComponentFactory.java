package com.example.inject;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class React4j_ConstructorInjectRawTypeComponentFactory {
  @SuppressWarnings("rawtypes")
  private final Consumer someParam;

  @SuppressWarnings("rawtypes")
  React4j_ConstructorInjectRawTypeComponentFactory(final Consumer someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  static React4j_ConstructorInjectRawTypeComponent create(final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static React4j_ConstructorInjectRawTypeComponentFactory c_factory;

    private static void setFactory(final React4j_ConstructorInjectRawTypeComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'ConstructorInjectRawTypeComponent' a second time" );
      }
      c_factory = factory;
    }

    private static React4j_ConstructorInjectRawTypeComponent create(final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'ConstructorInjectRawTypeComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_ConstructorInjectRawTypeComponent( view, c_factory.someParam );
    }
  }
}
