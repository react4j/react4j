package com.example.prop;

import arez.annotations.ArezComponentLike;
import arez.annotations.ComponentDependency;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class ArezComponentLikePropModel
{
  @ArezComponentLike
  interface Model
  {
  }

  @Nullable
  @Input
  abstract Model getModel();

  @Nullable
  @ComponentDependency( validateTypeAtRuntime = true )
  Model observedModel()
  {
    return getModel();
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
