package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BadReturnType2
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
  String handleFoo()
  {
    return null;
  }
}
