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

  @Contract(pure = true)
  static ReactNode children(final Stream<? extends ReactNode> children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static Step1 children(final ReactNode @Nullable ... children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    Step1 myProp(@Nullable String myProp);

    @Contract(pure = true)
    Step1 myProp2(@Nullable String myProp2);

    @Contract(pure = true)
    ReactNode children(Stream<? extends ReactNode> children);

    @Contract(pure = true)
    Step1 children(ReactNode @Nullable ... children);

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
    @Contract(pure = true)
    public final ReactNode children(final Stream<? extends ReactNode> children) {
      children(children.toArray(ReactNode[]::new));
      return build();
    }

    @Override
    @Contract(pure = true)
    public final Step1 children(final ReactNode @Nullable ... children) {
      _element.input(React4j_MultiPropComponent2.Inputs.children, children);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
