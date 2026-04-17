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

@View( type = View.Type.TRACKING )
abstract class NonObservableComponentDependencyView
{
  @ArezComponent( allowEmpty = true, observable = Feature.DISABLE )
  static abstract class Model
  {
  }

  @Nonnull
  @ComponentDependency
  final Model model;

  NonObservableComponentDependencyView( @Nonnull @Input final Model model )
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
