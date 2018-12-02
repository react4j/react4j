package com.example.pre_unmount;

import react4j.ReactNode;
import react4j.annotations.PreUnmount;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezModel
  extends ReactArezComponent
{
  @PreUnmount
  void preUnmount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
