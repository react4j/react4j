package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

@ReactComponent
class CustomHandlerButParametersIgnoredComponent
  extends Component<BaseProps, BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMouseEvent( int i );
  }

  @Override
  protected RenderElement render()
  {
    return null;
  }

  @EventHandler( CustomHandler.class )
  void handleFoo()
  {
  }
}
