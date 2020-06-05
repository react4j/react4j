package com.example.input;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class BasicInputViewBuilder {
  private BasicInputViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode someField(final boolean someField) {
    return newBuilder().someField( someField );
  }

  public interface Step1 {
    @Nonnull
    ReactNode someField(boolean someField);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_BasicInputView.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode someField(final boolean someField) {
      _element.input( React4j_BasicInputView.Inputs.someField, someField );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
