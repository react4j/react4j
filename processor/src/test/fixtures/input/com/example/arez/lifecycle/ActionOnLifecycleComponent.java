package com.example.arez.lifecycle;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ActionOnLifecycleComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  @PostMount
  void postMount()
  {
  }
}
