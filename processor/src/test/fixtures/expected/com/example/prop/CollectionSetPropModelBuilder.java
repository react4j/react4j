package com.example.prop;

import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CollectionSetPropModelBuilder {
  private CollectionSetPropModelBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final Set<String> myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(Set<String> myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CollectionSetPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final Set<String> myProp) {
      _element.props().set( React4j_CollectionSetPropModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
