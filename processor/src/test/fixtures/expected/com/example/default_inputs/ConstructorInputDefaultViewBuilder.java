package com.example.default_inputs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ConstructorInputDefaultViewBuilder {
  private ConstructorInputDefaultViewBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode myInput(@Nullable final String myInput) {
    return newBuilder().myInput( myInput );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myInput(@Nullable String myInput);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_ConstructorInputDefaultView.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_ConstructorInputDefaultView.Inputs.myInput, ConstructorInputDefaultView.getMyInputDefault() );
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myInput(@Nullable final String myInput) {
      _element.setKey( myInput + ( React.enableViewNames() ? "_ConstructorInputDefaultView_7de36afb" : ConstructorInputDefaultView.class.getName() ) );
      _element.input( React4j_ConstructorInputDefaultView.Inputs.myInput, myInput );
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
