package com.example.pre_unmount;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUnmount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class StaticModel
  extends Component
{
  @PreUnmount
  static void preUnmount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
