package com.example.inject;

import arez.Disposable;
import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ConstructorInputAndInjectArezComponentTypeComponentBuilder {
  private ConstructorInputAndInjectArezComponentTypeComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode model(final ConstructorInputAndInjectArezComponentTypeComponent.Model model) {
    return newBuilder().model( model );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode model(ConstructorInputAndInjectArezComponentTypeComponent.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ConstructorInputAndInjectArezComponentTypeComponent.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode model(
        final ConstructorInputAndInjectArezComponentTypeComponent.Model model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_ConstructorInputAndInjectArezComponentTypeComponent_8e180ee8" : ConstructorInputAndInjectArezComponentTypeComponent.class.getName() ) );
      assert Disposable.isNotDisposed( model );
      _element.input( React4j_ConstructorInputAndInjectArezComponentTypeComponent.Inputs.model, model );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
