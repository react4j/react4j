package com.example.arez;

import arez.annotations.CascadeDispose;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonArezHasArezFieldAnnotation
  extends Component
{
  @CascadeDispose
  Object _field;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
