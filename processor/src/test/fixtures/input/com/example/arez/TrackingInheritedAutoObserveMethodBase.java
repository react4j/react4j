package com.example.arez;

import arez.annotations.ArezComponentLike;
import arez.annotations.AutoObserve;
import arez.component.ComponentObservable;
import javax.annotation.Nullable;
import react4j.annotations.Input;

@ArezComponentLike
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
