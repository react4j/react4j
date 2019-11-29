package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1ProtectedAccessPreUpdateModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedLifecycleMethod" )
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
