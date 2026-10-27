package com.example.prop;

import arez.component.Identifiable;
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
final class ImmutablePropTypeDynamicValueBuilder {
  private ImmutablePropTypeDynamicValueBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myProp(final @Nullable Object myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode myProp(@Nullable Object myProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeDynamicValue.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode myProp(final @Nullable Object myProp) {
      final Object $myProp$ = myProp;
      _element.setKey(($myProp$ instanceof Keyed
              ? Keyed.getKey($myProp$)
              : $myProp$ instanceof Identifiable
                  ? Identifiable.<Object>getArezId($myProp$)
                  : String.valueOf($myProp$))
          + (React.enableViewNames()
              ? "_ImmutablePropTypeDynamicValue_a98f656b"
              : ImmutablePropTypeDynamicValue.class.getName()));
      _element.input(React4j_ImmutablePropTypeDynamicValue.Inputs.myProp, myProp);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
