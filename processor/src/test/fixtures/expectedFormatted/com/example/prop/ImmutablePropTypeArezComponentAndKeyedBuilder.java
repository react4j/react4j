package com.example.prop;

import arez.Disposable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezComponentAndKeyedBuilder {
  private ImmutablePropTypeArezComponentAndKeyedBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myProp(
      final ImmutablePropTypeArezComponentAndKeyed.@Nullable MyComponent myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode myProp(ImmutablePropTypeArezComponentAndKeyed.@Nullable MyComponent myProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeArezComponentAndKeyed.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode myProp(
        final ImmutablePropTypeArezComponentAndKeyed.@Nullable MyComponent myProp) {
      _element.setKey(Keyed.getKey(myProp)
          + (React.enableViewNames()
              ? "_ImmutablePropTypeArezComponentAndKeyed_ad8da370"
              : ImmutablePropTypeArezComponentAndKeyed.class.getName()));
      assert Disposable.isNotDisposed(myProp);
      _element.input(React4j_ImmutablePropTypeArezComponentAndKeyed.Inputs.myProp, myProp);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
