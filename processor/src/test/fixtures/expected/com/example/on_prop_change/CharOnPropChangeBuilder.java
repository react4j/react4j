package com.example.on_prop_change;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CharOnPropChangeBuilder {
  private CharOnPropChangeBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final char myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(char myProp);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_CharOnPropChange.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final char myProp) {
      _element.input( React4j_CharOnPropChange.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
