package com.example.lifecycle;

import arez.annotations.Observed;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezAnnotationOnLifecycleMethodsComponent
  extends ReactArezComponent
{
  @Observed
  @Override
  protected void componentWillUnmount()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
