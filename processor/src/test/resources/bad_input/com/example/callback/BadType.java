package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadType
  extends Component
{
  @JsFunction
  public interface CustomHandler
  {
    void onMyEvent( Object o1 );
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
