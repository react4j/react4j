package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ActAsComponentPropModel
{
  @ActAsComponent
  interface Model
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
