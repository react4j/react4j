package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class OverridingComponentDidUpdateComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  @PostUpdate
  void postUpdate()
  {
  }
}
