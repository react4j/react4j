package com.example.arez;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.Feature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
abstract class MaybeTrackingHasComponentDependencyFieldView
{
  @ArezComponent( allowEmpty = true, observable = Feature.ENABLE )
  static abstract class Model
  {
  }

  @Nonnull
  @ComponentDependency
  final Model model;

  MaybeTrackingHasComponentDependencyFieldView( @Nonnull @Input final Model model )
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
