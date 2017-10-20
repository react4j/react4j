package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

@ReactComponent
class BadTypeComponent
  extends Component<BaseProps, BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMyEvent( Object o1 );
  }

  @Override
  protected RenderElement render()
  {
    return null;
  }

  @EventHandler( CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
