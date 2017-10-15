package com.example.arez;

import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.NoAutoAction;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;

@ReactComponent
class ComponentWithNoAutoActionEventHandler
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @NoAutoAction
  @EventHandler
  void handleFoo()
  {
  }
}
