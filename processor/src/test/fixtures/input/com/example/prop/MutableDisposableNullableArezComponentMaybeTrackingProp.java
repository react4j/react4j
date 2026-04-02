package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
abstract class MutableDisposableNullableArezComponentMaybeTrackingProp
{
  @ArezComponent( allowEmpty = true, disposeOnDeactivate = true )
  static abstract class Model
  {
  }

  @Nullable
  @Input
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
