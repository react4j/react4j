package com.example.jspecify;

import java.util.List;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeView;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class React4j_ConstructorContractViewFactory {
  private final List<@Nullable String> dependencies;

  private final @Nullable String optionalService;

  React4j_ConstructorContractViewFactory(
      final List<@Nullable String> dependencies, final @Nullable String optionalService) {
    this.dependencies = dependencies;
    this.optionalService = optionalService;
    InjectSupport.setFactory(this);
  }

  static React4j_ConstructorContractView create(final NativeView view) {
    return InjectSupport.create(view);
  }

  @NullMarked
  private static final class InjectSupport {
    private static @Nullable React4j_ConstructorContractViewFactory c_factory;

    private static void setFactory(final React4j_ConstructorContractViewFactory factory) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null == c_factory,
            () ->
                "Attempted to instantiate the React4j view factory for the view named 'ConstructorContractView' a second time");
      }
      c_factory = factory;
    }

    private static React4j_ConstructorContractView create(final NativeView view) {
      if (React.shouldCheckInvariants()) {
        Guards.invariant(
            () -> null != c_factory,
            () ->
                "Attempted to create an instance of the React4j view named 'ConstructorContractView' before the view factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection.");
      }
      assert null != c_factory;
      @Nullable String[] values;
      if (React.shouldCheckInvariants()) {
        final var raw$values =
            view.requireInputs().getAsAny(React4j_ConstructorContractView.Inputs.values);
        values = null != raw$values ? raw$values.cast() : null;
      } else {
        values = Js.uncheckedCast(
            view.requireInputs().getAsAny(React4j_ConstructorContractView.Inputs.values));
      }
      return new Arez_React4j_ConstructorContractView(
          view, c_factory.dependencies, c_factory.optionalService, values);
    }
  }
}
