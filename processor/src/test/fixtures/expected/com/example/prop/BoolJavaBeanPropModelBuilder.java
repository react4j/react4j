package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class BoolJavaBeanPropModelBuilder {
  private BoolJavaBeanPropModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode foo(final boolean foo) {
    return newBuilder().foo( foo );
  }

  public interface Step1 {
    @Nonnull
    ReactNode foo(boolean foo);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_BoolJavaBeanPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode foo(final boolean foo) {
      _element.input( React4j_BoolJavaBeanPropModel.Inputs.foo, foo );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
