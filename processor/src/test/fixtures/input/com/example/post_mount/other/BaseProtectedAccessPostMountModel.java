package com.example.post_mount.other;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;

public abstract class BaseProtectedAccessPostMountModel
  extends Component
{
  @PostMount
  protected void postMount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
