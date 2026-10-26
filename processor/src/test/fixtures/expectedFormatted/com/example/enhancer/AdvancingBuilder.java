package com.example.enhancer;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
public final class AdvancingBuilder {
  private AdvancingBuilder() {}

  public static <T extends AtomicInteger> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  public static <T extends AtomicInteger> Step2<T> count(final int count) {
    return AdvancingBuilder.<T>newBuilder().count(count);
  }

  public static <T extends AtomicInteger, T2 extends Number> Step2<T> number(final @NonNull T2 arg0)
      throws Exception {
    return AdvancingBuilder.<T>newBuilder().<T2>number(arg0);
  }

  public static <T extends AtomicInteger, Step22, Builder2, AdvancingBuilder2, AtomicInteger2>
      Step2<T> named(
          final @NonNull Step22 arg0,
          final @NonNull Builder2 arg1,
          final @NonNull AdvancingBuilder2 arg2,
          final @NonNull AtomicInteger2 arg3) {
    return AdvancingBuilder.<T>newBuilder()
        .<Step22, Builder2, AdvancingBuilder2, AtomicInteger2>named(arg0, arg1, arg2, arg3);
  }

  public static <T extends AtomicInteger> Step2<T> one() {
    return AdvancingBuilder.<T>newBuilder().one();
  }

  @SuppressWarnings("unused")
  public interface Step1<T extends AtomicInteger> extends Enhancers.Count<Step2<T>> {
    @Contract(pure = true)
    Step2<T> count(int count);
  }

  @SuppressWarnings("unused")
  public interface Step2<T extends AtomicInteger> extends Enhancers.Label<ReactNode> {
    @Contract(pure = true)
    ReactNode label(String label);
  }

  private static class Builder<T extends AtomicInteger> implements Step1<T>, Step2<T> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_AdvancingView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2<T> count(final int count) {
      _element.setKey(count
          + (React.enableViewNames() ? "_AdvancingView_10556bf7" : AdvancingView.class.getName()));
      _element.input(React4j_AdvancingView.Inputs.count, count);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode label(final String label) {
      _element.input(React4j_AdvancingView.Inputs.label, label);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
