package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class Suppressed1ProtectedAccessPropModelBuilder {
  private Suppressed1ProtectedAccessPropModelBuilder() {
  }

  @Nonnull
  static ReactNode myProp(
      @SuppressWarnings("React4j:ProtectedLifecycleMethod") final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(@SuppressWarnings("React4j:ProtectedLifecycleMethod") String myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_Suppressed1ProtectedAccessPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        @SuppressWarnings("React4j:ProtectedLifecycleMethod") final String myProp) {
      _element.props().set( React4j_Suppressed1ProtectedAccessPropModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
