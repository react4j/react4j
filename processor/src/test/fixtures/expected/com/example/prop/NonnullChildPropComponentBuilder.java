package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NonnullChildPropComponentBuilder {
  private NonnullChildPropComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  static ReactNode child(final Renderable child) {
    return newBuilder().child( child );
  }

  interface Step1 {
    ReactNode child(Renderable child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonnullChildPropComponent.Factory.TYPE );

    @Override
    public final ReactNode child(final Renderable child) {
      final var $react4j$child = Renderable.toReactNode( child );
      _element.input( React4j_NonnullChildPropComponent.Inputs.child, ReactNode.of( $react4j$child ) );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
