package com.example.event_handler;

import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactElement;

@ReactComponent
class BasicHandlerComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler
  void handleFoo()
  {
  }
}
