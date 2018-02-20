package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class CustomTypeMultipleArgs
  extends Component
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

  @Callback( CustomHandler.class )
  void handleFoo( int i, int j )
  {
  }
}
