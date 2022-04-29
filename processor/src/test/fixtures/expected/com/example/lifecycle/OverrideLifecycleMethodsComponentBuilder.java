package com.example.lifecycle;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class OverrideLifecycleMethodsComponentBuilder {
  private OverrideLifecycleMethodsComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_OverrideLifecycleMethodsComponent.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
