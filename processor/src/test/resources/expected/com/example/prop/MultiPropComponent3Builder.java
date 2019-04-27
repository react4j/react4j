package com.example.prop;

import elemental2.core.JsArray;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class MultiPropComponent3Builder {
  private MultiPropComponent3Builder() {
  }

  @Nonnull
  static Step2 myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(String myProp);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp2(String myProp2);
  }

  public interface Step3 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_MultiPropComponent3.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(final String myProp) {
      _element.props().set( React4j_MultiPropComponent3.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp2(final String myProp2) {
      _element.props().set( React4j_MultiPropComponent3.Props.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.props().set( React4j_MultiPropComponent3.Props.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
