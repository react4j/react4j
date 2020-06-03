package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class NonDisposableDisposableProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Prop( disposable = Feature.DISABLE )
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
