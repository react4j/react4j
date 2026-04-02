package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ObserveOnRenderNonObservableArezEnableProp
{
  @ArezComponent( allowEmpty = true, observable = Feature.DISABLE )
  static abstract class Model
  {
  }

  @Nullable
  @Input( observeOnRender = react4j.annotations.Feature.ENABLE )
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
