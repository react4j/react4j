package com.example.prop;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ChildrenEnhancer;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MultipleChildrenPropComponentBuilder {
  private MultipleChildrenPropComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  static ReactNode children(final Renderable @Nullable ... children) {
    return newBuilder().children( children );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  static ReactNode children(final Stream<? extends Renderable> arg0) {
    return MultipleChildrenPropComponentBuilder.newBuilder().children(arg0);
  }

  interface Step1 extends Buildable, ChildrenEnhancer<ReactNode> {
    ReactNode children(Renderable @Nullable ... children);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultipleChildrenPropComponent.Factory.TYPE );

    @Override
    public final ReactNode children(final Renderable @Nullable ... children) {
      final var $react4j$children = Renderable.toReactNodes( children );
      _element.input( React4j_MultipleChildrenPropComponent.Inputs.children, $react4j$children );
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
