package com.example.prop;

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
final class ImmutablePropTypeKeyedInterfaceBuilder {
  private ImmutablePropTypeKeyedInterfaceBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myProp(final ImmutablePropTypeKeyedInterface.@Nullable KeyedInterface myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode myProp(ImmutablePropTypeKeyedInterface.@Nullable KeyedInterface myProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeKeyedInterface.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode myProp(
        final ImmutablePropTypeKeyedInterface.@Nullable KeyedInterface myProp) {
      _element.setKey(Keyed.getKey(myProp)
          + (React.enableViewNames()
              ? "_ImmutablePropTypeKeyedInterface_3326071b"
              : ImmutablePropTypeKeyedInterface.class.getName()));
      _element.input(React4j_ImmutablePropTypeKeyedInterface.Inputs.myProp, myProp);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
