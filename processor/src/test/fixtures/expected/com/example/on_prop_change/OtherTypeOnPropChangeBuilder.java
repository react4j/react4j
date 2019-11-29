package com.example.on_prop_change;

import java.util.BitSet;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class OtherTypeOnPropChangeBuilder {
  private OtherTypeOnPropChangeBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final BitSet myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(BitSet myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_OtherTypeOnPropChange.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final BitSet myProp) {
      _element.props().set( React4j_OtherTypeOnPropChange.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
