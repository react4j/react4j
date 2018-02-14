package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BadType
  extends Component<BaseState>
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
