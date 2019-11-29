package com.example.post_update;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class OnPropChangeAndPostUpdateModelBuilder {
  private OnPropChangeAndPostUpdateModelBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final int myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(int myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_OnPropChangeAndPostUpdateModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final int myProp) {
      _element.props().set( React4j_OnPropChangeAndPostUpdateModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
