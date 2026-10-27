package com.example.prop;

import arez.Disposable;
import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeArezComponentBuilder {
  private ImmutablePropTypeArezComponentBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myProp(final ImmutablePropTypeArezComponent.@Nullable MyComponent myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode myProp(ImmutablePropTypeArezComponent.@Nullable MyComponent myProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeArezComponent.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode myProp(
        final ImmutablePropTypeArezComponent.@Nullable MyComponent myProp) {
      _element.setKey(Identifiable.<Object>getArezId(myProp)
          + (React.enableViewNames()
              ? "_ImmutablePropTypeArezComponent_2492c8a2"
              : ImmutablePropTypeArezComponent.class.getName()));
      assert Disposable.isNotDisposed(myProp);
      _element.input(React4j_ImmutablePropTypeArezComponent.Inputs.myProp, myProp);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
