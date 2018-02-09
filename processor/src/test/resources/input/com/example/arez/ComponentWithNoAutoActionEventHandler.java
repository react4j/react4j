package com.example.arez;

import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.NoAutoAction;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithNoAutoActionEventHandler
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @NoAutoAction
  @EventHandler
  void handleFoo()
  {
  }
}
