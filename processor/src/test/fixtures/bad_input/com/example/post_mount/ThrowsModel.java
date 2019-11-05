package com.example.post_mount;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PostMount
  void postMount()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
