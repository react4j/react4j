package com.example.enhancer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@SuppressWarnings("NonApiType")
public final class TerminalBuilder {
  private TerminalBuilder() {
  }

  public static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  public static ReactNode panelTypes(final @Nullable Set<Class<?>> panelTypes) {
    return newBuilder().panelTypes( panelTypes );
  }

  @Contract(
      pure = true
  )
  public static ReactNode build() {
    return newBuilder().build();
  }

  public static ReactNode unitPanels() {
    return TerminalBuilder.newBuilder().unitPanels();
  }

  public static <Step12, ReactNode2> ReactNode named(final @NonNull Step12 arg0,
      final @NonNull ReactNode2 arg1) {
    return TerminalBuilder.newBuilder().<Step12, ReactNode2>named(arg0, arg1);
  }

  public static ReactNode collections(final ArrayList<Class<?>> arg0,
      final HashSet<Class<?>> arg1) {
    return TerminalBuilder.newBuilder().collections(arg0, arg1);
  }

  public interface Step1 extends Buildable, Enhancers.PanelTypes<ReactNode> {
    @Contract(
        pure = true
    )
    ReactNode panelTypes(@Nullable Set<Class<?>> panelTypes);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_TerminalView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode panelTypes(final @Nullable Set<Class<?>> panelTypes) {
      _element.input( React4j_TerminalView.Inputs.panelTypes, panelTypes );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
