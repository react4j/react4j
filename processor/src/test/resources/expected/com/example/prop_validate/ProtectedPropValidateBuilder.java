package com.example.prop_validate;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ProtectedPropValidateBuilder {
  private ProtectedPropValidateBuilder() {
  }

  @Nonnull
  static Step2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Step2 key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    ReactNode myProp(String myProp);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ProtectedPropValidate.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.props().set( React4j_ProtectedPropValidate.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
