package com.example.post_mount_or_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1ProtectedAccessPostMountOrUpdateModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedLifecycleMethod" )
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
