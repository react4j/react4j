package com.example.inject;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.Eager;
import sting.Injectable;
import sting.Named;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
final class React4j_StingNamedInjectComponentFactory {
  private final String someParam;

  React4j_StingNamedInjectComponentFactory(@Named("blah") final String someParam) {
    this.someParam = someParam;
    InjectSupport.setFactory(this);
  }

  static React4j_StingNamedInjectComponent create(final NativeView view) {
    return InjectSupport.create(view);
  }

  @NullMarked
  private static final class InjectSupport {
    private static @Nullable React4j_StingNamedInjectComponentFactory c_factory;

    private static void setFactory(final React4j_StingNamedInjectComponentFactory factory) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null == c_factory,
            () ->
                "Attempted to instantiate the React4j view factory for the view named 'StingNamedInjectComponent' a second time");
      }
      c_factory = factory;
    }

    private static React4j_StingNamedInjectComponent create(final NativeView view) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null != c_factory,
            () ->
                "Attempted to create an instance of the React4j view named 'StingNamedInjectComponent' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection.");
      }
      assert null != c_factory;
      return new Arez_React4j_StingNamedInjectComponent(view, c_factory.someParam);
    }
  }
}
