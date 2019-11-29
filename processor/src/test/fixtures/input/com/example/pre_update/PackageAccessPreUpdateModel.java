package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PackageAccessPreUpdateModel
  extends Component
{
  @PreUpdate
  void preUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
