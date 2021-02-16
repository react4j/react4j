package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NestedNestedView_DeepNesting_BasicViewBuilder {
  private NestedNestedView_DeepNesting_BasicViewBuilder() {
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
    private final ReactElement _element = ReactElement.createViewElement( NestedNestedView_DeepNesting_React4j_BasicView.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
