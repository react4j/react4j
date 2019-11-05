package com.example.post_mount_or_update;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PostMountOrUpdate
  void postMountOrUpdate()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
