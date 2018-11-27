package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ReturnsValueModel
  extends Component
{
  @PostUpdate
  int postUpdate()
  {
    return 0;
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
