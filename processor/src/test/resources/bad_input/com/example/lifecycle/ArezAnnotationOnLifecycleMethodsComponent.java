package com.example.lifecycle;

import arez.annotations.Autorun;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezAnnotationOnLifecycleMethodsComponent
  extends ReactArezComponent
{
  @Autorun
  @Override
  protected void componentWillUnmount()
  {
  }
}
