package com.example.arez;

import elemental2.core.JsArray;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ComponentWithChildPropBuilder {
  private ComponentWithChildPropBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode child(final ReactNode child) {
    return newBuilder().child( child );
  }

  public interface Step1 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ComponentWithChildProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.props().set( React4j_ComponentWithChildProp.Props.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
