package com.example.pre_update;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PreUpdate
  void preUpdate()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
