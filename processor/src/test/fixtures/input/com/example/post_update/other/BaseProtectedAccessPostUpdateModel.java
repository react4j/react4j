package com.example.post_update.other;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

public abstract class BaseProtectedAccessPostUpdateModel
  extends Component
{
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
