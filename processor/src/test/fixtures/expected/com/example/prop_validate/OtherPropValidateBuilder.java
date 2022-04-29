package com.example.prop_validate;

import java.util.BitSet;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class OtherPropValidateBuilder {
  private OtherPropValidateBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final BitSet myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(BitSet myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_OtherPropValidate.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final BitSet myProp) {
      _element.input( React4j_OtherPropValidate.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
