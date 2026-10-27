package com.example.enhancer;

import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.annotations.Input;
import react4j.annotations.View;

@NullMarked
@View(type=View.Type.STATEFUL)
abstract class OptionalView
{
  @Input(enhancer=Enhancers.PanelTypes.class)
  abstract @Nullable Set<Class<?>> panelTypes();

  @Input(enhancer=Enhancers.Title.class)
  abstract @Nullable String title();

  @Input(enhancer=Enhancers.Node.class)
  abstract @Nullable ReactNode node();

  @Input
  abstract @Nullable ReactNode @Nullable [] nodes();
  @react4j.annotations.Render
  @org.jspecify.annotations.Nullable
  Renderable render() { return node(); }
}
