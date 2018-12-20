package com.example.default_props;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class PropDefaultWithColorfulNameBuilder {
  private PropDefaultWithColorfulNameBuilder() {
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
  static ReactNode myProp12$23(final String myProp12$23) {
    return new Builder().myProp12$23( myProp12$23 );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    ReactNode myProp12$23(String myProp12$23);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_PropDefaultWithColorfulName.Factory.TYPE );
      _element.props().set( React4j_PropDefaultWithColorfulName.Props.myProp12$23, PropDefaultWithColorfulName.DEFAULT_MY_PROP12$23 );
    }

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
    public final ReactNode myProp12$23(final String myProp12$23) {
      _element.props().set( React4j_PropDefaultWithColorfulName.Props.myProp12$23, myProp12$23 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
