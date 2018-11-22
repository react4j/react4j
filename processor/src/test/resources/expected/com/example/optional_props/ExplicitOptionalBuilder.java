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
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder2 key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);

    @Nonnull
    Builder2 key(@Nonnull int key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myRequiredProp(String myRequiredProp);
  }

  public interface Builder3 {
    @Nonnull
    Builder3 myOptionalProp(String myOptionalProp);

    @Nonnull
    Builder3 myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ExplicitOptional.Factory.TYPE );

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Override
    @Nonnull
    public final Builder3 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myOptionalProp(final String myOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myOtherOptionalProp(final String myOtherOptionalProp) {
      _element.props().set( React4j_ExplicitOptional.Props.myOtherOptionalProp, myOtherOptionalProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
