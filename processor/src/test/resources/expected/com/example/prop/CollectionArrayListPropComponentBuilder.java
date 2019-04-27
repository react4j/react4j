package com.example.prop;

import java.util.ArrayList;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CollectionArrayListPropComponentBuilder {
  private CollectionArrayListPropComponentBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ArrayList<String> myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ArrayList<String> myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CollectionArrayListPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ArrayList<String> myProp) {
      _element.props().set( React4j_CollectionArrayListPropComponent.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
