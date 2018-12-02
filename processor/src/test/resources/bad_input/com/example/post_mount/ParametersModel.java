package com.example.post_mount;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ParametersModel
  extends Component
{
  @PostMount
  void postMount( int x )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
