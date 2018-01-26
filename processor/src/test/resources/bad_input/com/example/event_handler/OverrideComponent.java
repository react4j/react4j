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
abstract class OverrideComponent
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

  @EventHandler( name = "a" )
  void handleFoo()
  {
  }

  @EventHandler( name = "b", value = CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
