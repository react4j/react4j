package com.example.default_inputs;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ColorfulNameInputDefaultViewBuilder {
  private ColorfulNameInputDefaultViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp12$23(final String myProp12$23) {
    return newBuilder().myProp12$23( myProp12$23 );
  }

  @Nonnull
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp12$23(String myProp12$23);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_ColorfulNameInputDefaultView.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_ColorfulNameInputDefaultView.Inputs.myProp12$23, ColorfulNameInputDefaultView.DEFAULT_MY_PROP12$23 );
    }

    @Override
    @Nonnull
    public final ReactNode myProp12$23(final String myProp12$23) {
      _element.input( React4j_ColorfulNameInputDefaultView.Inputs.myProp12$23, myProp12$23 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
