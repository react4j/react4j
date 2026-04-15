package com.example.prop;

import arez.annotations.ArezComponentLike;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
abstract class MutableDisposableArezComponentLikeMaybeTrackingProp
{
  @ArezComponentLike
  interface ArezComponentLikeComponent
  {
  }

  @Nullable
  @Input
  abstract ArezComponentLikeComponent model();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
