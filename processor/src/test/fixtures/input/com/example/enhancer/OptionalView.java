package com.example.enhancer;

import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
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
  @react4j.annotations.Render
  @org.jspecify.annotations.Nullable
  ReactNode render() { return null; }
}
