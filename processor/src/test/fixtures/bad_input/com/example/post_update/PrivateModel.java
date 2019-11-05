package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PrivateModel
  extends Component
{
  @PostUpdate
  private void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
