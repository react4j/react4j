package com.example.post_mount;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class Suppressed1ProtectedAccessPostMountModelBuilder {
  private Suppressed1ProtectedAccessPostMountModelBuilder() {
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_Suppressed1ProtectedAccessPostMountModel.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
