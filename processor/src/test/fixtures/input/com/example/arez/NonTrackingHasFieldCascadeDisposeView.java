package com.example.arez;

import arez.Disposable;
import arez.annotations.CascadeDispose;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class NonTrackingHasFieldCascadeDisposeView
{
  @CascadeDispose
  Disposable _field;

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
