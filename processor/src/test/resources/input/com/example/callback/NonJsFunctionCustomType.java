package com.example.callback;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonJsFunctionCustomType
  extends Component
{
  public interface CustomHandler
  {
    void onMouseEvent( int i );
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
