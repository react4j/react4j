package com.example.enhancer;

import org.jspecify.annotations.NullMarked;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.View;

@NullMarked
@View(type=View.Type.STATEFUL, exportBuilder=true)
abstract class AdvancingView<T extends Number>
{
  AdvancingView(@Input(name="count", enhancer=Enhancers.Count.class) int initialCount) {}

  @Input(enhancer=Enhancers.Label.class)
  abstract String label();
  @react4j.annotations.Render
  @org.jspecify.annotations.Nullable
  ReactNode render() { return null; }
}
