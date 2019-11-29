package com.example.prop;

import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CollectionListPropModelBuilder {
  private CollectionListPropModelBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final List<String> myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(List<String> myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CollectionListPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final List<String> myProp) {
      _element.props().set( React4j_CollectionListPropModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
