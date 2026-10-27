package com.example.optional_props;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
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
final class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 myPropA(final @Nullable String myPropA) {
    return newBuilder().myPropA(myPropA);
  }

  @Contract(pure = true)
  static Step1 myPropB(final @Nullable String myPropB) {
    return newBuilder().myPropB(myPropB);
  }

  @Contract(pure = true)
  static Step1 myPropC(final @Nullable String myPropC) {
    return newBuilder().myPropC(myPropC);
  }

  @Contract(pure = true)
  static Step1 myPropD(final @Nullable String myPropD) {
    return newBuilder().myPropD(myPropD);
  }

  static ReactNode children(final Renderable @Nullable ... children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  static ReactNode children(final Stream<? extends Renderable> arg0) {
    return RequiredChildrenWithManyOptionalBuilder.newBuilder().children(arg0);
  }

  interface Step1 extends Buildable, ChildrenEnhancer<ReactNode> {
    @Contract(pure = true)
    Step1 myPropA(@Nullable String myPropA);

    @Contract(pure = true)
    Step1 myPropB(@Nullable String myPropB);

    @Contract(pure = true)
    Step1 myPropC(@Nullable String myPropC);

    @Contract(pure = true)
    Step1 myPropD(@Nullable String myPropD);

    ReactNode children(Renderable @Nullable ... children);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element =
          ReactElement.createViewElement(React4j_RequiredChildrenWithManyOptional.Factory.TYPE);
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set(
          React4j_RequiredChildrenWithManyOptional.Inputs.myPropA,
          RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A);
      inputs.set(
          React4j_RequiredChildrenWithManyOptional.Inputs.myPropB,
          RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B);
      inputs.set(
          React4j_RequiredChildrenWithManyOptional.Inputs.myPropC,
          RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C);
      inputs.set(
          React4j_RequiredChildrenWithManyOptional.Inputs.myPropD,
          RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D);
    }

    @Override
    @Contract(pure = true)
    public final Step1 myPropA(final @Nullable String myPropA) {
      _element.input(React4j_RequiredChildrenWithManyOptional.Inputs.myPropA, myPropA);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myPropB(final @Nullable String myPropB) {
      _element.input(React4j_RequiredChildrenWithManyOptional.Inputs.myPropB, myPropB);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myPropC(final @Nullable String myPropC) {
      _element.input(React4j_RequiredChildrenWithManyOptional.Inputs.myPropC, myPropC);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myPropD(final @Nullable String myPropD) {
      _element.input(React4j_RequiredChildrenWithManyOptional.Inputs.myPropD, myPropD);
      return this;
    }

    @Override
    public final ReactNode children(final Renderable @Nullable ... children) {
      final var $react4j$children = Renderable.toReactNodes(children);
      _element.input(React4j_RequiredChildrenWithManyOptional.Inputs.children, $react4j$children);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
