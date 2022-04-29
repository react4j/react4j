package com.example.post_mount_or_update;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class BasicPostMountOrUpdateModelBuilder {
  private BasicPostMountOrUpdateModelBuilder() {
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
    private final ReactElement _element = ReactElement.createViewElement( React4j_BasicPostMountOrUpdateModel.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
