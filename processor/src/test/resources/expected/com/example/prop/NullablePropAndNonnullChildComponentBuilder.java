package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NullablePropAndNonnullChildComponentBuilder {
  private NullablePropAndNonnullChildComponentBuilder() {
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
  static Builder3 myProp(@Nonnull final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);

    @Nonnull
    Builder2 key(@Nonnull int key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myProp(@Nonnull String myProp);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    ReactNode child(ReactNode child);
  }

  public interface Builder4 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NullablePropAndNonnullChildComponent.Factory.TYPE );

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
    public final Builder3 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.myProp2, myProp2 );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.props().set( React4j_NullablePropAndNonnullChildComponent.Props.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
