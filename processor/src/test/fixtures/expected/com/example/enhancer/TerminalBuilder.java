package com.example.enhancer;

import java.util.Set;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
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

  public interface Step1 extends Enhancers.PanelTypes<ReactNode> {
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
