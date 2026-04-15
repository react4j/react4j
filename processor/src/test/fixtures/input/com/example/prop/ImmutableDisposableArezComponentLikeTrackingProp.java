package com.example.prop;

import arez.annotations.ArezComponentLike;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ImmutableDisposableArezComponentLikeTrackingProp
{
  @ArezComponentLike
  interface ArezComponentLikeComponent
  {
  }

  ImmutableDisposableArezComponentLikeTrackingProp( @Nullable @Input final ArezComponentLikeComponent model )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
