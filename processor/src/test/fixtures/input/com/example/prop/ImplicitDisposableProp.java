package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ImplicitDisposableProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Prop
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
