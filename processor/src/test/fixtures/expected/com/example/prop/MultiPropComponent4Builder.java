package com.example.prop;

import elemental2.core.JsArray;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponent4Builder {
  private MultiPropComponent4Builder() {
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
    Step2 myProp2(@Nullable String myProp2);

    @Nonnull
    Step2 myProp3(@Nullable String myProp3);

    @Nonnull
    Step2 myProp4(@Nullable String myProp4);

    @Nonnull
    Step2 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent4.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myProp2(@Nullable final String myProp2) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myProp3(@Nullable final String myProp3) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp3, myProp3 );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myProp4(@Nullable final String myProp4) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp4, myProp4 );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 child(@Nullable final ReactNode child) {
      _element.input( React4j_MultiPropComponent4.Inputs.child, JsArray.of( child ) );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
