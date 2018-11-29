package com.example.arez.lifecycle;

import javax.annotation.Nullable;
import javax.xml.ws.Action;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ActionOnLifecycleComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  @PostMount
  protected void postMount()
  {
  }
}
