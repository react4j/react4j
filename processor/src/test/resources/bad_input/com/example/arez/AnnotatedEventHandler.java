package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.Action;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.RenderElement;

@ReactComponent
class AnnotatedEventHandler
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected RenderElement render()
  {
    return null;
  }

  @EventHandler
  @Action
  void handleFoo()
  {
  }
}
