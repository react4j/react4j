package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class AnnotatedEventHandler
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler
  @Action
  void handleFoo()
  {
  }
}
