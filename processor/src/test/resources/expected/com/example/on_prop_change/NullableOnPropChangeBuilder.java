package com.example.on_prop_change;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NullableOnPropChangeBuilder {
  private NullableOnPropChangeBuilder() {
  }

  @Nonnull
  static ReactNode myProp(@Nullable final String myProp) {
    return new Builder().myProp( myProp );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(@Nullable String myProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NullableOnPropChange.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(@Nullable final String myProp) {
      _element.props().set( React4j_NullableOnPropChange.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
