package com.example.optional_props;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
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
  static Step3 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    Step3 myRequiredProp(String myRequiredProp);
  }

  public interface Step3 {
    @Nonnull
    Step3 myOptionalProp(String myOptionalProp);

    @Nonnull
    Step3 myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ExplicitOptional.Factory.TYPE );

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
    public final Step3 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myOptionalProp(final String myOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myOtherOptionalProp(final String myOtherOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOtherOptionalProp, myOtherOptionalProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
