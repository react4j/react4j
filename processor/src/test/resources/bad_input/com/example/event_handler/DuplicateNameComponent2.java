package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class DuplicateNameComponent2
  extends Component<BaseProps, BaseState, BaseContext>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMouseEvent( int i );
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler
  void handleFoo()
  {
  }

  @EventHandler( CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
