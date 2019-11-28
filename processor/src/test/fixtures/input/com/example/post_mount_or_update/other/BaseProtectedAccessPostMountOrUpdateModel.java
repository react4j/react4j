package com.example.post_mount_or_update.other;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

public abstract class BaseProtectedAccessPostMountOrUpdateModel
  extends Component
{
  @PostMountOrUpdate
  protected void postMountOrUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
