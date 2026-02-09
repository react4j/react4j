package com.example.post_mount;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class PublicAccessViaInterfacePostMountModel
  implements PostMountInterface
{
  @PostMount
  public void postMount()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
