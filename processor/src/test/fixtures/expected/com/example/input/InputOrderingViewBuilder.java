package com.example.input;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class InputOrderingViewBuilder {
  private InputOrderingViewBuilder() {
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
    Step3 paramP(boolean paramP);
  }

  interface Step3 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step4 paramB(boolean paramB);
  }

  interface Step4 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step5 paramC(boolean paramC);
  }

  interface Step5 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode paramA(boolean paramA);
  }

  private static class Builder implements Step1,
      Step2,
      Step3,
      Step4,
      Step5 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_InputOrderingView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 paramZ(final boolean paramZ) {
      _element.input( React4j_InputOrderingView.Inputs.paramZ, paramZ );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3 paramP(final boolean paramP) {
      _element.input( React4j_InputOrderingView.Inputs.paramP, paramP );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step4 paramB(final boolean paramB) {
      _element.input( React4j_InputOrderingView.Inputs.paramB, paramB );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step5 paramC(final boolean paramC) {
      _element.input( React4j_InputOrderingView.Inputs.paramC, paramC );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode paramA(final boolean paramA) {
      _element.input( React4j_InputOrderingView.Inputs.paramA, paramA );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<Object> inputs = _element.inputs();
      _element.setKey( String.valueOf( (boolean) inputs.get( React4j_InputOrderingView.Inputs.paramZ ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_InputOrderingView.Inputs.paramP ) ) + ( React.enableViewNames() ? "_InputOrderingView_5e9a9eff" : InputOrderingView.class.getName() ) );
      return _element;
    }
  }
}
