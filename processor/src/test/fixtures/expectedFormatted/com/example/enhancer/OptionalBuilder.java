package com.example.enhancer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@SuppressWarnings("NonApiType")
final class OptionalBuilder {
  private OptionalBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 panelTypes(final @Nullable Set<Class<?>> panelTypes) {
    return newBuilder().panelTypes(panelTypes);
  }

  @Contract(pure = true)
  static Step1 title(final @Nullable String title) {
    return newBuilder().title(title);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  static Step1 unitPanels() {
    return OptionalBuilder.newBuilder().unitPanels();
  }

  static <Step12, ReactNode2> Step1 named(
      final @NonNull Step12 arg0, final @NonNull ReactNode2 arg1) {
    return OptionalBuilder.newBuilder().<Step12, ReactNode2>named(arg0, arg1);
  }

  static Step1 collections(final ArrayList<Class<?>> arg0, final HashSet<Class<?>> arg1) {
    return OptionalBuilder.newBuilder().collections(arg0, arg1);
  }

  static Step1 titled(final String... arg0) {
    return OptionalBuilder.newBuilder().titled(arg0);
  }

  interface Step1 extends Enhancers.PanelTypes<Step1>, Enhancers.Title<Step1> {
    @Contract(pure = true)
    Step1 panelTypes(@Nullable Set<Class<?>> panelTypes);

    @Contract(pure = true)
    Step1 title(@Nullable String title);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_OptionalView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step1 panelTypes(final @Nullable Set<Class<?>> panelTypes) {
      _element.input(React4j_OptionalView.Inputs.panelTypes, panelTypes);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 title(final @Nullable String title) {
      _element.input(React4j_OptionalView.Inputs.title, title);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
