package com.example.post_mount_or_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PostRenderAndPostUpdate
  extends Component
{
  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
