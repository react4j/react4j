package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ImmutableDisposablePropModel
{
  ImmutableDisposablePropModel( @Nullable @Input( disposable = Feature.ENABLE ) final Object value )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
