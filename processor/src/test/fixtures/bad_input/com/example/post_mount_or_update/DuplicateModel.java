package com.example.post_mount_or_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateModel
  extends Component
{
  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }

  @PostMountOrUpdate
  void postRender2()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
