package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponentBuilder {
  private MultiPropComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(String myProp);
  }

  public interface Step2 {
    @Nonnull
    ReactNode myProp2(String myProp2);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_MultiPropComponent.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(final String myProp2) {
      _element.input( React4j_MultiPropComponent.Inputs.myProp2, myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
