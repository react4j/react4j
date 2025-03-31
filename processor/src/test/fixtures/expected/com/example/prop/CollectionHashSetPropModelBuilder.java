package com.example.prop;

import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CollectionHashSetPropModelBuilder {
  private CollectionHashSetPropModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final HashSet<String> myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(HashSet<String> myProp);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_CollectionHashSetPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final HashSet<String> myProp) {
      _element.input( React4j_CollectionHashSetPropModel.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
