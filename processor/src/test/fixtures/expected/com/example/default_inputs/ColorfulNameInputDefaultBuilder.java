package com.example.default_inputs;

import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ColorfulNameInputDefaultBuilder {
  private ColorfulNameInputDefaultBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode myProp12$23(final String myProp12$23) {
    return newBuilder().myProp12$23( myProp12$23 );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode myProp12$23(String myProp12$23);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_ColorfulNameInputDefaultView.Factory.TYPE );
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set( React4j_ColorfulNameInputDefaultView.Inputs.myProp12$23, ColorfulNameInputDefaultView.DEFAULT_MY_PROP12$23 );
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp12$23(final String myProp12$23) {
      _element.input( React4j_ColorfulNameInputDefaultView.Inputs.myProp12$23, myProp12$23 );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
