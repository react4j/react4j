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
import react4j.Renderable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
@SuppressWarnings("NonApiType")
final class OptionalBuilder {
  private OptionalBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step1 panelTypes(final @Nullable Set<Class<?>> panelTypes) {
    return newBuilder().panelTypes( panelTypes );
  }

  @Contract(
      pure = true
  )
  static Step1 title(final @Nullable String title) {
    return newBuilder().title( title );
  }

  static Step1 node(final @Nullable Renderable node) {
    return newBuilder().node( node );
  }

  static Step1 nodes(final @Nullable Renderable @Nullable ... nodes) {
    return newBuilder().nodes( nodes );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  static Step1 unitPanels() {
    return OptionalBuilder.newBuilder().unitPanels();
  }

  static <Step12, ReactNode2> Step1 named(final @NonNull Step12 arg0,
      final @NonNull ReactNode2 arg1) {
    return OptionalBuilder.newBuilder().<Step12, ReactNode2>named(arg0, arg1);
  }

  static Step1 collections(final ArrayList<Class<?>> arg0, final HashSet<Class<?>> arg1) {
    return OptionalBuilder.newBuilder().collections(arg0, arg1);
  }

  static Step1 titled(final String... arg0) {
    return OptionalBuilder.newBuilder().titled(arg0);
  }

  static Step1 emptyNode() {
    return OptionalBuilder.newBuilder().emptyNode();
  }

  interface Step1 extends Buildable,
      Enhancers.PanelTypes<Step1>,
      Enhancers.Title<Step1>,
      Enhancers.Node<Step1> {
    @Contract(
        pure = true
    )
    Step1 panelTypes(@Nullable Set<Class<?>> panelTypes);

    @Contract(
        pure = true
    )
    Step1 title(@Nullable String title);

    Step1 node(@Nullable Renderable node);

    Step1 nodes(@Nullable Renderable @Nullable ... nodes);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_OptionalView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step1 panelTypes(final @Nullable Set<Class<?>> panelTypes) {
      _element.input( React4j_OptionalView.Inputs.panelTypes, panelTypes );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 title(final @Nullable String title) {
      _element.input( React4j_OptionalView.Inputs.title, title );
      return this;
    }

    @Override
    public final Step1 node(final @Nullable Renderable node) {
      final var $react4j$node = Renderable.toReactNode( node );
      _element.input( React4j_OptionalView.Inputs.node, $react4j$node );
      return this;
    }

    @Override
    public final Step1 nodes(final @Nullable Renderable @Nullable ... nodes) {
      final var $react4j$nodes = Renderable.toReactNodes( nodes );
      _element.input( React4j_OptionalView.Inputs.nodes, $react4j$nodes );
      return this;
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
