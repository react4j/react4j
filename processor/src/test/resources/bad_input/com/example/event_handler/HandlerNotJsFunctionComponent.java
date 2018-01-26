package com.example.event_handler;

import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class HandlerNotJsFunctionComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler( Runnable.class )
  void handleFoo()
  {
  }
}
