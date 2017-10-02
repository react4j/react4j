package com.example.event_handler;

import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

@ReactComponent
class HandlerNotInterfaceComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler( Object.class )
  void handleFoo()
  {
  }
}
