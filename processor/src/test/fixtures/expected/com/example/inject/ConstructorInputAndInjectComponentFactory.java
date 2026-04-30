package com.example.inject;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
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
final class ConstructorInputAndInjectComponentFactory {
  @Nonnull
  private final String someParam;

  ConstructorInputAndInjectComponentFactory(@Nonnull final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  static React4j_ConstructorInputAndInjectComponent create(@Nonnull final NativeView view) {
    return InjectSupport.create( view );
  }

  private static final class InjectSupport {
    private static ConstructorInputAndInjectComponentFactory c_factory;

    private static void setFactory(
        @Nonnull final ConstructorInputAndInjectComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j view factory for the view named 'ConstructorInputAndInjectComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_ConstructorInputAndInjectComponent create(
        @Nonnull final NativeView view) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j view named 'ConstructorInputAndInjectComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      String myProp;
      if ( React.shouldCheckInvariants() ) {
        myProp = null != view.inputs().getAsAny( React4j_ConstructorInputAndInjectComponent.Inputs.myProp ) ? view.inputs().getAsAny( React4j_ConstructorInputAndInjectComponent.Inputs.myProp ).asString() : null;
      } else {
        myProp = Js.uncheckedCast( view.inputs().getAsAny( React4j_ConstructorInputAndInjectComponent.Inputs.myProp ) );
      }
      return new Arez_React4j_ConstructorInputAndInjectComponent( view, c_factory.someParam, myProp );
    }
  }
}
