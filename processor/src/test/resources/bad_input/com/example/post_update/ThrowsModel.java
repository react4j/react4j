package com.example.post_update;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PostUpdate
  void postUpdate()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
