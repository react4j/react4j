package com.example.event_handler;

import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderResult;

@ReactComponent
class CustomNameComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected RenderResult render()
  {
    return null;
  }

  @EventHandler( name = "fox" )
  void handleFoo()
  {
  }
}
