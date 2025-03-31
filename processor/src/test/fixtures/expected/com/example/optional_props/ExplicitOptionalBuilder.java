package com.example.optional_props;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myRequiredProp(final String myRequiredProp) {
    return newBuilder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myRequiredProp(String myRequiredProp);
  }

  public interface Step2 {
    @Nonnull
    Step2 myOptionalProp(String myOptionalProp);

    @Nonnull
    Step2 myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ExplicitOptional.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myRequiredProp(final String myRequiredProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myOptionalProp(final String myOptionalProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myOtherOptionalProp(final String myOtherOptionalProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myOtherOptionalProp, myOtherOptionalProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
