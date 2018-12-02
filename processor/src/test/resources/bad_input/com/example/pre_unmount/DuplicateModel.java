package com.example.pre_unmount;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUnmount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateModel
  extends Component
{
  @PreUnmount
  void preUnmount()
  {
  }

  @PreUnmount
  void preUnmount2()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
