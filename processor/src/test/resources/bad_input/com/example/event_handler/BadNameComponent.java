package com.example.event_handler;

import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

@ReactComponent
class BadNameComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler( name = "-fox" )
  void handleFoo()
  {
  }
}
