package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ExplicitDependencyDisableInputBuilder {
  private ExplicitDependencyDisableInputBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode myComponent(final ExplicitDependencyDisableInput.MyComponent myComponent) {
    return newBuilder().myComponent( myComponent );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myComponent(ExplicitDependencyDisableInput.MyComponent myComponent);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ExplicitDependencyDisableInput.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myComponent(
        final ExplicitDependencyDisableInput.MyComponent myComponent) {
      _element.input( React4j_ExplicitDependencyDisableInput.Inputs.myComponent, myComponent );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
