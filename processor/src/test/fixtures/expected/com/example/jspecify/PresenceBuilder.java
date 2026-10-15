package com.example.jspecify;

import java.util.ArrayList;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@SuppressWarnings({
    "NonApiType",
    "deprecation"
})
final class PresenceBuilder {
  private PresenceBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 requiredNullable(final @Nullable String requiredNullable) {
    return newBuilder().requiredNullable( requiredNullable );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 requiredNullable(@Nullable String requiredNullable);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    Step2 optionalNullable(@Nullable String optionalNullable);

    @Contract(
        pure = true
    )
    Step2 defaulted(String defaulted);

    @Contract(
        pure = true
    )
    @Deprecated
    Step2 collection(@Nullable ArrayList<String> collection);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_PresenceView.Factory.TYPE );
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set( React4j_PresenceView.Inputs.defaulted, PresenceView.getDefaultedDefault() );
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step2 requiredNullable(final @Nullable String requiredNullable) {
      _element.input( React4j_PresenceView.Inputs.requiredNullable, requiredNullable );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step2 optionalNullable(final @Nullable String optionalNullable) {
      _element.input( React4j_PresenceView.Inputs.optionalNullable, optionalNullable );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step2 defaulted(final String defaulted) {
      _element.input( React4j_PresenceView.Inputs.defaulted, defaulted );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    @Deprecated
    public final Step2 collection(final @Nullable ArrayList<String> collection) {
      _element.input( React4j_PresenceView.Inputs.collection, collection );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
