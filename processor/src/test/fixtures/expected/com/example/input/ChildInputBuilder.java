package com.example.input;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ChildInputBuilder {
  private ChildInputBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  static ReactNode child(final @Nullable Renderable child) {
    return newBuilder().child( child );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    ReactNode child(@Nullable Renderable child);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ChildInputView.Factory.TYPE );

    @Override
    public final ReactNode child(final @Nullable Renderable child) {
      final var $react4j$child = Renderable.toReactNode( child );
      _element.input( React4j_ChildInputView.Inputs.child, ReactNode.of( $react4j$child ) );
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
