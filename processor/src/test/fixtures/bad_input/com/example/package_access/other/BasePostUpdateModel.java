package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.PostUpdate;

public abstract class BasePostUpdateModel
  extends Component
{
  @PostUpdate
  void postUpdate()
  {
  }
}
