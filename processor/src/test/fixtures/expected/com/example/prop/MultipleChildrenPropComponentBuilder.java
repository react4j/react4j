package com.example.prop;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MultipleChildrenPropComponentBuilder {
  private MultipleChildrenPropComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode children(final Stream<? extends ReactNode> children) {
    return newBuilder().children( children );
  }

  @Contract(
      pure = true
  )
  static ReactNode children(final ReactNode @Nullable ... children) {
    return newBuilder().children( children );
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
    ReactNode children(Stream<? extends ReactNode> children);

    @Contract(
        pure = true
    )
    ReactNode children(ReactNode @Nullable ... children);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultipleChildrenPropComponent.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode children(final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode children(final ReactNode @Nullable ... children) {
      _element.input( React4j_MultipleChildrenPropComponent.Inputs.children, children );
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
