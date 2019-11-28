package com.example.post_mount_or_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PostMountAndPostMountOrUpdateModel
  extends Component
{
  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }

  @PostMount
  void postMount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
