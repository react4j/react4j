package com.example.inject;

import arez.Disposable;
import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ConstructorInputAndInjectArezComponentTypeComponentBuilder {
  private ConstructorInputAndInjectArezComponentTypeComponentBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(
      @Nonnull final ConstructorInputAndInjectArezComponentTypeComponent.Model model) {
    return newBuilder().model( model );
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(@Nonnull ConstructorInputAndInjectArezComponentTypeComponent.Model model);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ConstructorInputAndInjectArezComponentTypeComponent.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nonnull final ConstructorInputAndInjectArezComponentTypeComponent.Model model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_ConstructorInputAndInjectArezComponentTypeComponent_8e180ee8" : ConstructorInputAndInjectArezComponentTypeComponent.class.getName() ) );
      assert Disposable.isNotDisposed( model );
      Objects.requireNonNull( model );
      _element.input( React4j_ConstructorInputAndInjectArezComponentTypeComponent.Inputs.model, model );
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
