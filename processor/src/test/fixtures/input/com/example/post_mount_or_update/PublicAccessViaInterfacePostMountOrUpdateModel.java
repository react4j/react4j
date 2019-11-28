package com.example.post_mount_or_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PublicAccessViaInterfacePostMountOrUpdateModel
  extends Component
  implements PostMountOrUpdateInterface
{
  @PostMountOrUpdate
  public void postMountOrUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
