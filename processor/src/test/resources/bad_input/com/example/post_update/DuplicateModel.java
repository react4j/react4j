package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateModel
  extends Component
{
  @PostUpdate
  void postUpdate()
  {
  }

  @PostUpdate
  void postUpdate2()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
