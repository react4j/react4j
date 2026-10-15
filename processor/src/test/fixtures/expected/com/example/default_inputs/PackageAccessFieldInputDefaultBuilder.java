package com.example.default_inputs;

import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class PackageAccessFieldInputDefaultBuilder {
  private PackageAccessFieldInputDefaultBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode myProp(final @Nullable String myProp) {
    return newBuilder().myProp( myProp );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode myProp(@Nullable String myProp);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_PackageAccessFieldInputDefaultView.Factory.TYPE );
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set( React4j_PackageAccessFieldInputDefaultView.Inputs.myProp, PackageAccessFieldInputDefaultView.DEFAULT_MY_PROP );
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp(final @Nullable String myProp) {
      _element.input( React4j_PackageAccessFieldInputDefaultView.Inputs.myProp, myProp );
      return build();
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
