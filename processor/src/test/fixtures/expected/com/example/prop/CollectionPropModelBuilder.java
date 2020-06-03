package com.example.prop;

import java.util.Collection;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CollectionPropModelBuilder {
  private CollectionPropModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final Collection<String> myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(Collection<String> myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_CollectionPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final Collection<String> myProp) {
      _element.props().set( React4j_CollectionPropModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
