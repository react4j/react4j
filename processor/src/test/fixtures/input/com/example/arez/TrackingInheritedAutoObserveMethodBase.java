package com.example.arez;

import arez.annotations.ArezComponentLike;
import arez.annotations.AutoObserve;
import arez.component.ComponentObservable;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;

@ArezComponentLike
@org.jspecify.annotations.NullMarked
abstract class TrackingInheritedAutoObserveMethodBase
{
  interface Model
    extends ComponentObservable
  {
  }

  @Nullable
  @Input
  abstract Model getModel();

  @Nullable
  @AutoObserve
  Model observedModel()
  {
    return getModel();
  }
}
