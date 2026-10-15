package com.example.arez;

import arez.annotations.ArezComponentLike;
import arez.annotations.AutoObserve;
import arez.component.ComponentObservable;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@ArezComponentLike
@org.jspecify.annotations.NullMarked
abstract class NonTrackingHasAutoObserveFieldView
{
  interface Model
    extends ComponentObservable
  {
  }

  @Nullable
  @AutoObserve
  final Model model;

  NonTrackingHasAutoObserveFieldView( @Nullable @Input final Model model )
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
