package com.example.prop;

import java.util.HashSet;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
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
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CollectionHashSetPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final HashSet<String> myProp) {
      _element.props().set( React4j_CollectionHashSetPropModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
