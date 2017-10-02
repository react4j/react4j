package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler( CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
