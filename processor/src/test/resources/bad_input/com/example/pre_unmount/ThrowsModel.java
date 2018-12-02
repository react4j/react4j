package com.example.pre_unmount;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUnmount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PreUnmount
  void preUnmount()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
