package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NullabilityPropsComponentBuilder {
  private NullabilityPropsComponentBuilder() {
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
  static Step3 myProp(@Nonnull final String myProp) {
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
    Step3 myProp(@Nonnull String myProp);
  }

  public interface Step3 {
    @Nonnull
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NullabilityPropsComponent.Factory.TYPE );

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
    public final Step3 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.props().set( React4j_NullabilityPropsComponent.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.props().set( React4j_NullabilityPropsComponent.Props.myProp2, myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
