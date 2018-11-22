package com.example.on_prop_changed;

import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ParameterizedOnPropChangedBuilder {
  private ParameterizedOnPropChangedBuilder() {
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
  static ReactNode myProp(final ArrayList<String> myProp) {
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
    ReactNode myProp(ArrayList<String> myProp);
  }

  private static class Builder implements Builder1, Builder2 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ParameterizedOnPropChanged.Factory.TYPE );

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
    public final ReactNode myProp(final ArrayList<String> myProp) {
      _element.props().set( React4j_ParameterizedOnPropChanged.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
