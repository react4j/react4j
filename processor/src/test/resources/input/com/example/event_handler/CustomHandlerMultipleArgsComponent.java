package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class CustomHandlerMultipleArgsComponent
  extends Component<BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMouseEvent( int i, int j );
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler( CustomHandler.class )
  void handleFoo( int i, int j )
  {
  }
}
