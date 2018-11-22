package com.example.render;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class BaseRenderComponentBuilder {
  private BaseRenderComponentBuilder() {
  }

  @Nonnull
  static ReactNode key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Builder1 {
    @Nonnull
    ReactNode key(@Nonnull String key);

    @Nonnull
    ReactNode key(@Nonnull int key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_BaseRenderComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
