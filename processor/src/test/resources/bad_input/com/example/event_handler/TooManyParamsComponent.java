package com.example.event_handler;

import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

@ReactComponent
class TooManyParamsComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected RenderElement render()
  {
    return null;
  }

  @EventHandler
  void handleFoo( int i )
  {
  }
}
