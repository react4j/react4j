package com.example.input_semantics;

import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class DefaultEqualityBuilder {
  private DefaultEqualityBuilder() {}

  static <T extends DefaultEqualityView.Value> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  static <T extends DefaultEqualityView.Value> Step2<T> immutable(
      final DefaultEqualityView.Value immutable) {
    return DefaultEqualityBuilder.<T>newBuilder().immutable(immutable);
  }

  @SuppressWarnings("unused")
  interface Step1<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    Step2<T> immutable(DefaultEqualityView.Value immutable);
  }

  @SuppressWarnings("unused")
  interface Step2<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    Step3<T> value(DefaultEqualityView.Value value);
  }

  @SuppressWarnings("unused")
  interface Step3<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    Step4<T> childValue(DefaultEqualityView.Child childValue);
  }

  @SuppressWarnings("unused")
  interface Step4<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    Step5<T> array(DefaultEqualityView.Value... array);
  }

  @SuppressWarnings("unused")
  interface Step5<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    Step6<T> bounded(@NonNull T bounded);
  }

  @SuppressWarnings("unused")
  interface Step6<T extends DefaultEqualityView.Value> {
    @Contract(pure = true)
    ReactNode overridden(DefaultEqualityView.Value overridden);
  }

  private static class Builder<T extends DefaultEqualityView.Value>
      implements Step1<T>, Step2<T>, Step3<T>, Step4<T>, Step5<T>, Step6<T> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_DefaultEqualityView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2<T> immutable(final DefaultEqualityView.Value immutable) {
      final Object $immutable$ = immutable;
      _element.setKey(($immutable$ instanceof Keyed
              ? Keyed.getKey($immutable$)
              : $immutable$ instanceof Identifiable
                  ? Identifiable.<Object>getArezId($immutable$)
                  : String.valueOf($immutable$))
          + (React.enableViewNames()
              ? "_DefaultEqualityView_69c3e0c7"
              : DefaultEqualityView.class.getName()));
      _element.input(React4j_DefaultEqualityView.Inputs.immutable, immutable);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3<T> value(final DefaultEqualityView.Value value) {
      _element.input(React4j_DefaultEqualityView.Inputs.value, value);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4<T> childValue(final DefaultEqualityView.Child childValue) {
      _element.input(React4j_DefaultEqualityView.Inputs.childValue, childValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step5<T> array(final DefaultEqualityView.Value... array) {
      _element.input(React4j_DefaultEqualityView.Inputs.array, array);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step6<T> bounded(final @NonNull T bounded) {
      _element.input(React4j_DefaultEqualityView.Inputs.bounded, bounded);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode overridden(final DefaultEqualityView.Value overridden) {
      _element.input(React4j_DefaultEqualityView.Inputs.overridden, overridden);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
