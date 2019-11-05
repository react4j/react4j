package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class NonDisposableDisposableProp
  extends Component
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Prop( disposable = Feature.DISABLE )
  protected abstract Model getModel();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
