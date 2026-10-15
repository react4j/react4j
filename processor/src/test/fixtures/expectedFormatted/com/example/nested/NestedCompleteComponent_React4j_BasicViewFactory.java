package com.example.nested;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;
import sting.Eager;
import sting.Injectable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
final class NestedCompleteComponent_React4j_BasicViewFactory {
  private final String value;

  NestedCompleteComponent_React4j_BasicViewFactory(final String value) {
    this.value = value;
    InjectSupport.setFactory(this);
  }

  static NestedCompleteComponent_React4j_BasicView create(final NativeView view) {
    return InjectSupport.create(view);
  }

  @NullMarked
  private static final class InjectSupport {
    private static @Nullable NestedCompleteComponent_React4j_BasicViewFactory c_factory;

    private static void setFactory(final NestedCompleteComponent_React4j_BasicViewFactory factory) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null == c_factory,
            () ->
                "Attempted to instantiate the React4j view factory for the view named 'BasicView' a second time");
      }
      c_factory = factory;
    }

    private static NestedCompleteComponent_React4j_BasicView create(final NativeView view) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null != c_factory,
            () ->
                "Attempted to create an instance of the React4j view named 'BasicView' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection.");
      }
      assert null != c_factory;
      return new Arez_NestedCompleteComponent_React4j_BasicView(view, c_factory.value);
    }
  }
}
