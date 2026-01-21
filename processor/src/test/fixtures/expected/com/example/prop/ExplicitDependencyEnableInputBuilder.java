package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ExplicitDependencyEnableInputBuilder {
  private ExplicitDependencyEnableInputBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode myComponent(final ExplicitDependencyEnableInput.MyComponent myComponent) {
    return newBuilder().myComponent( myComponent );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myComponent(ExplicitDependencyEnableInput.MyComponent myComponent);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ExplicitDependencyEnableInput.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myComponent(
        final ExplicitDependencyEnableInput.MyComponent myComponent) {
      _element.setKey( Identifiable.<Object>getArezId( myComponent ) + ( React.enableViewNames() ? "_ExplicitDependencyEnableInput_ff4614c5" : ExplicitDependencyEnableInput.class.getName() ) );
      _element.input( React4j_ExplicitDependencyEnableInput.Inputs.myComponent, myComponent );
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
