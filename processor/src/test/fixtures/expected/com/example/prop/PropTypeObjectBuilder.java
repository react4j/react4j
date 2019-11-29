package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class PropTypeObjectBuilder {
  private PropTypeObjectBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final PropTypeObject.MyObject myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(PropTypeObject.MyObject myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_PropTypeObject.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final PropTypeObject.MyObject myProp) {
      _element.props().set( React4j_PropTypeObject.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
