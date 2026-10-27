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
final class MultiPropComponent2Builder {
  private MultiPropComponent2Builder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 myProp(final @Nullable String myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static Step1 myProp2(final @Nullable String myProp2) {
    return newBuilder().myProp2(myProp2);
  }

  static ReactNode children(final Renderable @Nullable ... children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  static ReactNode children(final Stream<? extends Renderable> arg0) {
    return MultiPropComponent2Builder.newBuilder().children(arg0);
  }

  interface Step1 extends Buildable, ChildrenEnhancer<ReactNode> {
    @Contract(pure = true)
    Step1 myProp(@Nullable String myProp);

    @Contract(pure = true)
    Step1 myProp2(@Nullable String myProp2);

    ReactNode children(Renderable @Nullable ... children);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MultiPropComponent2.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step1 myProp(final @Nullable String myProp) {
      _element.input(React4j_MultiPropComponent2.Inputs.myProp, myProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myProp2(final @Nullable String myProp2) {
      _element.input(React4j_MultiPropComponent2.Inputs.myProp2, myProp2);
      return this;
    }

    @Override
    public final ReactNode children(final Renderable @Nullable ... children) {
      final var $react4j$children = Renderable.toReactNodes(children);
      _element.input(React4j_MultiPropComponent2.Inputs.children, $react4j$children);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
