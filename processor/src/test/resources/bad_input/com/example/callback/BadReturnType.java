package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadReturnType
  extends Component
{
  @JsFunction
  public interface CustomHandler
  {
    ReactNode myRenderProp();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( CustomHandler.class )
  void handleFoo()
  {
  }
}
