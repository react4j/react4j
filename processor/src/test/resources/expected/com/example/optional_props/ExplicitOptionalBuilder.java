package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
  }

  @Nonnull
  static Step2 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
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
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ExplicitOptional.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myOptionalProp(final String myOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myOtherOptionalProp(final String myOtherOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOtherOptionalProp, myOtherOptionalProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
