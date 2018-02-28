package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ComponentWithArezPropBuilder {
  private ComponentWithArezPropBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 value(final String value) {
    return new Builder().value( value );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 value(String value);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode model(ComponentWithArezProp.Model model);
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 value(final String value) {
      _props.set( "value", value );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode model(final ComponentWithArezProp.Model model) {
      _props.set( "model", model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_ComponentWithArezProp.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
