package com.example.inject;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.Eager;
import sting.Injectable;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
final class React4j_ConstructorInputAndInjectArezComponentTypeComponentFactory {
  @Nonnull
  private final String someParam;

  React4j_ConstructorInputAndInjectArezComponentTypeComponentFactory(
      @Nonnull final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  static React4j_ConstructorInputAndInjectArezComponentTypeComponent create(
      @Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static React4j_ConstructorInputAndInjectArezComponentTypeComponentFactory c_factory;

    private static void setFactory(
        @Nonnull final React4j_ConstructorInputAndInjectArezComponentTypeComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'ConstructorInputAndInjectArezComponentTypeComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_ConstructorInputAndInjectArezComponentTypeComponent create(
        @Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'ConstructorInputAndInjectArezComponentTypeComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      final ConstructorInputAndInjectArezComponentTypeComponent.Model model = view.inputs().getAsAny( React4j_ConstructorInputAndInjectArezComponentTypeComponent.Inputs.model ).cast();
      return new Arez_React4j_ConstructorInputAndInjectArezComponentTypeComponent( view, c_factory.someParam, model );
    }
  }
}
