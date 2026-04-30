package com.example.input;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NoShouldComponentUpdateForInputsBuilder {
  private NoShouldComponentUpdateForInputsBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 paramZ(final boolean paramZ) {
    return newBuilder().paramZ( paramZ );
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 paramZ(boolean paramZ);
  }

  interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode paramP(boolean paramP);
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_NoShouldComponentUpdateForInputsView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 paramZ(final boolean paramZ) {
      _element.input( React4j_NoShouldComponentUpdateForInputsView.Inputs.paramZ, paramZ );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode paramP(final boolean paramP) {
      _element.input( React4j_NoShouldComponentUpdateForInputsView.Inputs.paramP, paramP );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<Object> inputs = _element.inputs();
      _element.setKey( String.valueOf( (boolean) inputs.get( React4j_NoShouldComponentUpdateForInputsView.Inputs.paramZ ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_NoShouldComponentUpdateForInputsView.Inputs.paramP ) ) + ( React.enableViewNames() ? "_NoShouldComponentUpdateForInputsView_4a12cafd" : NoShouldComponentUpdateForInputsView.class.getName() ) );
      return _element;
    }
  }
}
