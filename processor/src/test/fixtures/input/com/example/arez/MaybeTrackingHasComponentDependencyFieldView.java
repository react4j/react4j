package com.example.arez;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.Feature;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
@org.jspecify.annotations.NullMarked
abstract class MaybeTrackingHasComponentDependencyFieldView
{
  @ArezComponent( allowEmpty = true, observable = Feature.ENABLE )
  static abstract class Model
  {
  }

  @ComponentDependency
  final Model model;

  MaybeTrackingHasComponentDependencyFieldView( @Input final Model model )
  {
    this.model = model;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
