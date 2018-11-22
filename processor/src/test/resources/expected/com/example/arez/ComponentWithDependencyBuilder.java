package com.example.arez;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ComponentWithDependencyBuilder {
  private ComponentWithDependencyBuilder() {
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
  static Builder3 value(final String value) {
    return new Builder().value( value );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);

    @Nonnull
    Builder2 key(@Nonnull int key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 value(String value);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode model(ComponentWithDependency.Model model);
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ComponentWithDependency.Factory.TYPE );

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
    public final Builder3 value(final String value) {
      _element.props().set( React4j_ComponentWithDependency.Props.value, value );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode model(final ComponentWithDependency.Model model) {
      _element.props().set( React4j_ComponentWithDependency.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
