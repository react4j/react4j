package com.example.default_inputs;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ExplicitNameMethodInputDefaultViewBuilder {
  private ExplicitNameMethodInputDefaultViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  @Nonnull
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(String myProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_ExplicitNameMethodInputDefaultView.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_ExplicitNameMethodInputDefaultView.Inputs.myProp, ExplicitNameMethodInputDefaultView.zoooooooooooooom() );
    }

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.input( React4j_ExplicitNameMethodInputDefaultView.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
