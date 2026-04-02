package com.example.prop;

import arez.component.ComponentObservable;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ObserveOnRenderComponentObservableTrackingProp
{
  interface Model
    extends ComponentObservable
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
