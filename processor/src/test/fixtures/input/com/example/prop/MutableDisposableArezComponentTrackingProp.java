package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class MutableDisposableArezComponentTrackingProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Nonnull
  @Input
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
